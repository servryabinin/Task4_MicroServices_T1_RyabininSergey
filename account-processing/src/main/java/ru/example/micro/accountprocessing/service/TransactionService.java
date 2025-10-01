package ru.example.micro.accountprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.accountprocessing.model.Transaction;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository repository;

    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }
    @LogDatasourceError
    public List<Transaction> getAll() {
        return repository.findAll();
    }
    @LogDatasourceError
    public Transaction getById(Long id) {
        return repository.findById(id).orElse(null);
    }
    @LogDatasourceError
    public Transaction save(Transaction transaction) {
        return repository.save(transaction);
    }
    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
