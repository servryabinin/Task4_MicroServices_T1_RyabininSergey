package ru.example.micro.creditprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.creditprocessing.model.PaymentRegistry;

public interface PaymentRegistryRepository extends JpaRepository<PaymentRegistry, Long> {
}
