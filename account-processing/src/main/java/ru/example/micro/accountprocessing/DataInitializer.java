package ru.example.micro.accountprocessing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.repository.*;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final PaymentRepository paymentRepository;
    private final TransactionRepository transactionRepository;

    public DataInitializer(AccountRepository accountRepository,
                           CardRepository cardRepository,
                           PaymentRepository paymentRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) {
        // Accounts
        if (accountRepository.count() == 0) {
            Account a1 = new Account();
            a1.setClientId(1L);
            a1.setProductId(1L);
            a1.setBalance(1000.0);
            a1.setStatus(Status.ACTIVE);
            a1.setIsRecalc(true);
            a1.setCardExist(true);
            accountRepository.save(a1);

            Account a2 = new Account();
            a2.setClientId(2L);
            a2.setProductId(2L);
            a2.setBalance(500.0);
            a2.setStatus(Status.CLOSED);
            a2.setIsRecalc(false);
            a2.setCardExist(false);
            accountRepository.save(a2);
        }

        // Cards
        if (cardRepository.count() == 0) {
            Card c1 = new Card();
            c1.setAccountId(1L);
            c1.setCardId("1111-2222-3333-4444");
            c1.setPaymentSystem("VISA");
            c1.setStatus(CardStatus.ACTIVE);
            cardRepository.save(c1);
        }

        // Payments
        if (paymentRepository.count() == 0) {
            Payment p1 = new Payment();
            p1.setAccountId(1L);
            p1.setAmount(200.0);
            p1.setIsCredit(true);
            p1.setPaymentDate(LocalDateTime.now());
            p1.setType("DEPOSIT");
            paymentRepository.save(p1);
        }

        // Transactions
        if (transactionRepository.count() == 0) {
            Transaction t1 = new Transaction();
            t1.setAccountId(1L);
            t1.setCardId(1L);
            t1.setAmount(150.0);
            t1.setType("PAYMENT");
            t1.setStatus(TransactionStatus.COMPLETE);
            t1.setTimestamp(LocalDateTime.now());
            transactionRepository.save(t1);
        }
    }
}
