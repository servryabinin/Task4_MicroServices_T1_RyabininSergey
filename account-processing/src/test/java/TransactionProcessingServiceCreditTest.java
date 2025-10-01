

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.example.micro.accountprocessing.DTO.ClientTransactionMessage;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.accountprocessing.repository.PaymentRepository;
import ru.example.micro.accountprocessing.service.TransactionProcessingService;

import java.util.Optional;

import static org.mockito.Mockito.*;

class TransactionProcessingServiceCreditTest {

    private TransactionRepository txRepo;
    private AccountRepository accRepo;
    private PaymentRepository paymentRepo;
    private TransactionProcessingService service;
    private final int threshold = 5;
    private final long windowSeconds = 60;

    @BeforeEach
    void setup() {
        txRepo = mock(TransactionRepository.class);
        accRepo = mock(AccountRepository.class);
        paymentRepo = mock(PaymentRepository.class);
        service = new TransactionProcessingService(txRepo, accRepo, paymentRepo, threshold, windowSeconds);
    }

    @Test
    void creditAccount_shouldCreatePaymentSchedule() {
        // --- входные данные ---
        Account acc = new Account();
        acc.setId(1L);
        acc.setBalance(1200.0);
        acc.setInterestRate(12.0);   // 12% годовых
        acc.setIsRecalc(true);       // кредитный счёт
        acc.setStatus(Status.ACTIVE);

        ClientTransactionMessage msg = new ClientTransactionMessage();
        msg.setAccountId(1L);
        msg.setCardId(10L);
        msg.setType("DEPOSIT");
        msg.setAmount(200.0);

        // --- настройка моков ---
        when(txRepo.existsByMessageUuid("uuid-1")).thenReturn(false);
        when(accRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));
        when(paymentRepo.existsByAccountIdAndIsCreditTrueAndPayedAtIsNull(1L)).thenReturn(false);

        // --- выполнение ---
        service.processTransaction("uuid-1", msg);

        // --- проверка ---
        verify(paymentRepo, times(12)).save(any(Payment.class));
    }
}
