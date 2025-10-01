package ru.example.micro.clientprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.clientprocessing.model.ClientProduct;

public interface ClientProductRepository extends JpaRepository<ClientProduct, Long> {
}
