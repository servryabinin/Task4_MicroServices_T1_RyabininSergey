package ru.example.micro.accountprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.accountprocessing.model.ClientProduct;

public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
}
