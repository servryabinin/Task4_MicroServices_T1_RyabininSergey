package ru.example.micro.clientprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.clientprocessing.model.Product;
import ru.example.micro.clientprocessing.repository.ProductRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public List<Product> getAll() {
        return repository.findAll();
    }

    @LogDatasourceError
    public Product getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @LogDatasourceError
    public Product save(Product product) {
        if (product.getId() == null) { // новый продукт
            product.setCreateDate(LocalDateTime.now());
            if (repository.existsByProductKey(product.getProductKey())) {
                throw new IllegalArgumentException("Product key must be unique");
            }
        }
        return repository.save(product);
    }

    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
