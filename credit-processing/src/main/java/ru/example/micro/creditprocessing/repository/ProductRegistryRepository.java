package ru.example.micro.creditprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.creditprocessing.model.ProductRegistry;

public interface ProductRegistryRepository extends JpaRepository<ProductRegistry, Long> {
}
