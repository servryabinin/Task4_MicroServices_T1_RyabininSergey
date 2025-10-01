import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.example.micro.accountprocessing.DTO.ClientTransactionMessage;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.accountprocessing.repository.PaymentRepository;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.accountprocessing.service.TransactionProcessingService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionProcessingServiceTest {

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
    void deposit_shouldIncreaseBalance_andCreateCompleteTransaction() {
        ClientTransactionMessage msg = new ClientTransactionMessage();
        msg.setAccountId(1L);
        msg.setCardId(10L);
        msg.setType("DEPOSIT");
        msg.setAmount(200.0);
        msg.setTimestamp(null);

        Account acc = new Account();
        acc.setId(1L);
        acc.setBalance(1000.0);
        acc.setStatus(Status.ACTIVE);

        when(txRepo.existsByMessageUuid("uuid-1")).thenReturn(false);
        when(accRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));

        service.processTransaction("uuid-1", msg);

        // verify account saved with updated balance
        assertEquals(1200.0, acc.getBalance());

        // verify transaction saved
        verify(txRepo, times(1)).save(any());
        verify(accRepo, times(1)).save(acc);
    }
}
