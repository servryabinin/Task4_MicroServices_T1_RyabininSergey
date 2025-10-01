package ru.example.micro.accountprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.accountprocessing.model.Payment;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByAccountIdAndIsCreditTrueAndPayedAtIsNull(Long accountId);

    List<Payment> findByAccountIdAndIsCreditTrueAndPayedAtIsNullAndPaymentDateLessThanEqual(Long accountId, LocalDateTime paymentDate);

    List<Payment> findByAccountIdAndIsCreditTrueAndPayedAtIsNull(Long accountId);



}
