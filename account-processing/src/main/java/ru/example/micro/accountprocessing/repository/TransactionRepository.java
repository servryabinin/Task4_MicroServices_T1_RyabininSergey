package ru.example.micro.accountprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.example.micro.accountprocessing.model.Transaction;

import java.time.Instant;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByMessageUuid(String messageUuid);

    @Modifying
    @Query("update Transaction t set t.status = 'BLOCKED' " +
            "where t.cardId = :cardId and t.timestamp >= :since and t.status <> 'COMPLETE'")
    void markRecentByCardAsBlocked(@Param("cardId") Long cardId, @Param("since") Instant since);

}
