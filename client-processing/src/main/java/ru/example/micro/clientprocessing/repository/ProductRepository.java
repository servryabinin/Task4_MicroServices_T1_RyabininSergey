package ru.example.micro.clientprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.clientprocessing.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByProductKey(String productKey);
}
