
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import ru.example.micro.accountprocessing.DTO.ClientTransactionMessage;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.accountprocessing.service.TransactionProcessingService;

import java.time.Duration;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class TransactionProcessingBlockedTest {

    private TransactionRepository txRepo;
    private AccountRepository accRepo;
    private TransactionProcessingService service;

    // Локальные значения для антифрода
    private final int threshold = 5;
    private final long windowSeconds = 60;

    @BeforeEach
    void setup() {
        txRepo = mock(TransactionRepository.class);
        accRepo = mock(AccountRepository.class);
        service = new TransactionProcessingService(txRepo, accRepo, null, threshold, windowSeconds);
    }

    @Test
    void blockedAccount_shouldMarkTransactionBlocked() {
        ClientTransactionMessage msg = new ClientTransactionMessage();
        msg.setAccountId(1L);
        msg.setCardId(10L);
        msg.setType("DEPOSIT");
        msg.setAmount(100.0);

        Account acc = new Account();
        acc.setId(1L);
        acc.setStatus(Status.BLOCKED);

        when(accRepo.findByIdForUpdate(1L)).thenReturn(Optional.of(acc));
        when(txRepo.existsByMessageUuid("uuid-1")).thenReturn(false);

        service.processTransaction("uuid-1", msg);

        verify(txRepo, times(1)).save(argThat(tx ->
                tx.getStatus() == TransactionStatus.BLOCKED &&
                        tx.getAccountId().equals(1L) &&
                        tx.getAmount().equals(100.0)
        ));

        // Баланс не должен меняться
        assertNull(acc.getBalance(), "Баланс аккаунта не должен меняться");
    }
}
