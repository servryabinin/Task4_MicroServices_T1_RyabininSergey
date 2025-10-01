
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.example.micro.accountprocessing.DTO.ClientTransactionMessage;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.accountprocessing.repository.PaymentRepository;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.accountprocessing.service.TransactionProcessingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionProcessingDuePaymentsTest {

    private TransactionRepository txRepo;
    private AccountRepository accRepo;
    private PaymentRepository paymentRepo;
    private TransactionProcessingService service;

    @BeforeEach
    void setup() {
        txRepo = mock(TransactionRepository.class);
        accRepo = mock(AccountRepository.class);
        paymentRepo = mock(PaymentRepository.class);

        service = new TransactionProcessingService(txRepo, accRepo, paymentRepo, 5, 60);
    }

    @Test
    void accrualCreditAccount_shouldProcessDuePayments() {
        // 1️⃣ Подготовка сообщения
        ClientTransactionMessage msg = new ClientTransactionMessage();
        msg.setAccountId(1L);
        msg.setCardId(10L);
        msg.setType("ACCRUAL");
        msg.setAmount(0.0); // начисление без фактического депозита

        // 2️⃣ Подготовка кредитного аккаунта
        Account acc = new Account();
        acc.setId(1L);
        acc.setBalance(500.0);
        acc.setIsRecalc(true);
        acc.setStatus(Status.ACTIVE);
        acc.setInterestRate(12.0);

        // 3️⃣ Подготовка платежа, который должен быть списан
        Payment p1 = new Payment();
        p1.setAccountId(1L);
        p1.setAmount(200.0);
        p1.setIsCredit(true);
        p1.setPaymentDate(LocalDateTime.now().minusDays(1)); // уже наступил день платежа
        p1.setPayedAt(null);

        // 4️⃣ Моки
        when(txRepo.existsByMessageUuid("uuid-1")).thenReturn(false);
        when(accRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));
        when(paymentRepo.findByAccountIdAndIsCreditTrueAndPayedAtIsNullAndPaymentDateLessThanEqual(any()))
                .thenReturn(List.of(p1));

        // 5️⃣ Вызов метода
        service.processTransaction("uuid-1", msg);

        // 6️⃣ Проверки

        // Баланс аккаунта уменьшился на сумму платежа
        assertEquals(300.0, acc.getBalance(), "Баланс после списания должен уменьшиться на сумму платежа");

        // Платеж отмечен как выполненный
        assertNotNull(p1.getPayedAt(), "Payment должен быть помечен как выполненный (payedAt не null)");

        // Платеж не просрочен
        assertFalse(p1.getExpired(), "Payment не должен быть помечен expired");

        // Проверка сохранения транзакции списания
        verify(txRepo, times(1)).save(argThat(tx ->
                "MONTHLY_PAYMENT".equals(tx.getType()) &&
                        tx.getAmount().equals(200.0) &&
                        tx.getStatus() == TransactionStatus.COMPLETE
        ));

        // Проверка сохранения аккаунта
        verify(accRepo, atLeastOnce()).save(acc);


        // Проверка сохранения payment
        verify(paymentRepo, times(1)).saveAll(List.of(p1));
    }
}
