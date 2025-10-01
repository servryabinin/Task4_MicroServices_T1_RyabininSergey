package ru.example.micro.accountprocessing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.micro.accountprocessing.DTO.ClientPaymentMessage;
import ru.example.micro.accountprocessing.DTO.ClientTransactionMessage;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.accountprocessing.repository.PaymentRepository;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
public class TransactionProcessingService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;
    private final int threshold;
    private final Duration window;
    private final ConcurrentHashMap<Long, Deque<Instant>> cardTxWindow = new ConcurrentHashMap<>();


    public TransactionProcessingService(TransactionRepository transactionRepository,
                                        AccountRepository accountRepository, PaymentRepository paymentRepository,
                                        @Value("${app.antifraud.threshold}") int threshold,
                                        @Value("${app.antifraud.window-seconds}") long windowSeconds) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.paymentRepository = paymentRepository;
        this.threshold = threshold;
        this.window = Duration.ofSeconds(windowSeconds);
    }

    @LogDatasourceError
    private void processDuePayments(Account account, LocalDateTime txTimestamp) {
        List<Payment> due = paymentRepository
                .findByAccountIdAndIsCreditTrueAndPayedAtIsNullAndPaymentDateLessThanEqual(account.getId(), txTimestamp);

        for (Payment p : due) {
            if (account.getBalance() >= p.getAmount()) {
                // Списываем с баланса
                account.setBalance(account.getBalance() - p.getAmount());

                // Отмечаем платеж как выполненный
                p.setPayedAt(LocalDateTime.now());

                // Создаём транзакцию списания
                Transaction payTx = new Transaction();
                payTx.setAccountId(account.getId());
                payTx.setType("MONTHLY_PAYMENT");
                payTx.setAmount(p.getAmount());
                payTx.setStatus(TransactionStatus.COMPLETE);
                payTx.setTimestamp(LocalDateTime.now());
                transactionRepository.save(payTx);
            } else {
                // Недостаточно средств → помечаем expired
                p.setExpired(true);
            }
        }

        paymentRepository.saveAll(due);
        accountRepository.save(account);
    }


    @LogDatasourceError
    private boolean checkAndHandleAntifraud(Long cardId, Account account) {
        Instant now = Instant.now();
        Deque<Instant> dq = cardTxWindow.computeIfAbsent(cardId, id -> new ConcurrentLinkedDeque<>());

        synchronized (dq) { // синхронизация на deque, чтобы избежать race conditions
            dq.addLast(now);
            // Удаляем старые метки старше окна
            while (!dq.isEmpty() && dq.peekFirst().isBefore(now.minus(window))) {
                dq.removeFirst();
            }

            // Если превышен порог
            if (dq.size() > threshold) {
                account.setStatus(Status.BLOCKED);
                accountRepository.save(account);

                // массовое обновление транзакций этой карты
                transactionRepository.markRecentByCardAsBlocked(cardId, now.minus(window));
                return true;
            }
        }

        return false;
    }

    @LogDatasourceError
    @Transactional
    public void processTransaction(String messageUuid, ClientTransactionMessage msg) {
        // 1) идемпотентность
        if (messageUuid != null && transactionRepository.existsByMessageUuid(messageUuid)) {
            System.out.println("Повторное сообщение, пропускаем: " + messageUuid);
            return;
        }

        // 2) взять счёт под блокировкой
        Optional<Account> optAcc = accountRepository.findByIdForUpdate(msg.getAccountId());
        if (optAcc.isEmpty()) {
            System.out.println("Account not found: " + msg.getAccountId());
            return;
        }
        Account account = optAcc.get();

        if (checkAndHandleAntifraud(msg.getCardId(), account)) {
            Transaction t = buildTransactionFromMsg(msg, TransactionStatus.BLOCKED, messageUuid);
            transactionRepository.save(t);
            return; // прекращаем обработку транзакции
        }


        // 3) если счёт заблокирован/арестован — сохраняем транзакцию как BLOCKED и выходим
        if (account.getStatus() == Status.BLOCKED || account.getStatus() == Status.ARRESTED) {
            Transaction tBlocked = buildTransactionFromMsg(msg, TransactionStatus.BLOCKED, messageUuid);
            transactionRepository.save(tBlocked);
            System.out.println("Account is blocked/arrested, transaction marked BLOCKED");
            return;
        }


        // 4) создаём транзакцию в PROCESSING
        Transaction tx = buildTransactionFromMsg(msg, null, messageUuid);
        tx.setStatus(TransactionStatus.PROCESSING);

        // 5) бизнес-логика по типу транзакции
        // безопасно работать с null-балансом
        Double bal = account.getBalance() == null ? 0.0 : account.getBalance();

        String type = msg.getType() == null ? "" : msg.getType().toUpperCase().trim();
        switch (type) {
            case "DEPOSIT":
            case "ACCRUAL":
                bal = bal + (msg.getAmount() == null ? 0.0 : msg.getAmount());
                account.setBalance(bal);
                tx.setStatus(TransactionStatus.COMPLETE);
                break;

            case "WITHDRAWAL":
            case "PAYMENT":
                double amount = msg.getAmount() == null ? 0.0 : msg.getAmount();
                if (bal >= amount) {
                    bal = bal - amount;
                    account.setBalance(bal);
                    tx.setStatus(TransactionStatus.COMPLETE);
                } else {
                    // недостаточно средств — отклоняем или помечаем как CANCELLED
                    tx.setStatus(TransactionStatus.CANCELLED);
                    System.out.println("Insufficient funds for account " + account.getId());
                }
                break;

            default:
                tx.setStatus(TransactionStatus.CANCELLED);
                System.out.println("Unknown transaction type: " + msg.getType());
        }

        // 6) сохраняем (учитывая возможную гонку по message_uuid)
        try {
            transactionRepository.save(tx);
        } catch (DataIntegrityViolationException ex) {
            // возможно кто-то уже сохранил транзакцию с таким message_uuid -> идемпотентно игнорируем
            System.out.println("Duplicate messageUuid (likely concurrent): " + messageUuid);
            return;
        }

       accountRepository.save(account);

        if ("ACCRUAL".equals(type) && Boolean.TRUE.equals(account.getIsRecalc())) {
            processDuePayments(account, tx.getTimestamp());
        }

        if (account.getIsRecalc() != null && account.getIsRecalc()) {
            createPaymentSchedule(account);
        }
    }
    @LogDatasourceError
    private Transaction buildTransactionFromMsg(ClientTransactionMessage msg, TransactionStatus status, String messageUuid) {
        Transaction t = new Transaction();
        t.setAccountId(msg.getAccountId());
        t.setCardId(msg.getCardId());
        t.setType(msg.getType());
        t.setAmount(msg.getAmount());
        try {
            if (msg.getTimestamp() != null) {
                t.setTimestamp(LocalDateTime.parse(msg.getTimestamp()));
            } else {
                t.setTimestamp(LocalDateTime.now());
            }
        } catch (Exception ex) {
            t.setTimestamp(LocalDateTime.now());
        }
        t.setStatus(status);
        t.setMessageUuid(messageUuid);
        return t;
    }


    @LogDatasourceError
    private void createPaymentSchedule(Account account) {
        // Проверяем, есть ли уже график
        if (paymentRepository.existsByAccountIdAndIsCreditTrueAndPayedAtIsNull(account.getId())) return;

        // Простая формула: interest-only (ежемесячный процент)
        double monthly = account.getBalance() * (account.getInterestRate() / 100.0) / 12.0;

        // День месяца для платежа — можно использовать день транзакции или открытия счёта
        int dayOfMonth = LocalDateTime.now().getDayOfMonth();

        LocalDateTime next = LocalDateTime.now().plusMonths(1).withDayOfMonth(dayOfMonth);

        for (int i = 0; i < 12; i++) {
            Payment p = new Payment();
            p.setAccountId(account.getId());
            p.setAmount(monthly);
            p.setIsCredit(true);
            p.setPaymentDate(next.plusMonths(i));
            p.setType("SCHEDULED");
            p.setExpired(false);
            p.setInstallmentNumber(i + 1);
            paymentRepository.save(p);
        }
    }
    @LogDatasourceError
    @Transactional
    public void processClientPayment(ClientPaymentMessage msg) {
        if (msg.getAccountId() == null || msg.getAmount() == null) {
            System.out.println("Invalid payment message: " + msg);
            return;
        }

        Optional<Account> optAcc = accountRepository.findByIdForUpdate(msg.getAccountId());
        if (optAcc.isEmpty()) {
            System.out.println("Account not found: " + msg.getAccountId());
            return;
        }

        Account account = optAcc.get();

        // Вычисляем текущую задолженность по кредитным платежам
        List<Payment> unpaidPayments = paymentRepository
                .findByAccountIdAndIsCreditTrueAndPayedAtIsNull(account.getId());

        double totalDebt = unpaidPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        if (Double.compare(msg.getAmount(), totalDebt) == 0) {
            // пересчитать баланс счета
            double newBalance = (account.getBalance() == null ? 0.0 : account.getBalance()) + msg.getAmount();
            account.setBalance(newBalance);

            // Создать новый Payment
            Payment newPayment = new Payment();
            newPayment.setAccountId(account.getId());
            newPayment.setAmount(msg.getAmount());
            newPayment.setIsCredit(true);
            newPayment.setPaymentDate(LocalDateTime.now());
            newPayment.setPayedAt(LocalDateTime.now());

            paymentRepository.save(newPayment);

            // Обновляем существующие платежи, которые еще не были оплачены
            unpaidPayments.forEach(p -> p.setPayedAt(LocalDateTime.now()));
            paymentRepository.saveAll(unpaidPayments);

            accountRepository.save(account);

            System.out.println("Payment processed for account " + account.getId() +
                    ", balance updated to " + account.getBalance());
        } else {
            System.out.println("Payment amount does not match total debt: " + msg.getAmount() + " != " + totalDebt);
        }
    }

}
