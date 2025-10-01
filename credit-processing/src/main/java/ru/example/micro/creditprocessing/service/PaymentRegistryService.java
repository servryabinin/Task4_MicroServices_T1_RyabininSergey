package ru.example.micro.creditprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.creditprocessing.model.PaymentRegistry;
import ru.example.micro.creditprocessing.repository.PaymentRegistryRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class PaymentRegistryService {
    private final PaymentRegistryRepository repository;

    public PaymentRegistryService(PaymentRegistryRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public List<PaymentRegistry> getAll() { return repository.findAll(); }

    @LogDatasourceError
    public PaymentRegistry getById(Long id) { return repository.findById(id).orElse(null); }

    @LogDatasourceError
    public PaymentRegistry save(PaymentRegistry payment) { return repository.save(payment); }

    @LogDatasourceError
    public void delete(Long id) { repository.deleteById(id); }
}
