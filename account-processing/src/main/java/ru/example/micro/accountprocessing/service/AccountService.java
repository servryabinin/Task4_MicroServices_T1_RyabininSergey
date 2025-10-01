package ru.example.micro.accountprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.accountprocessing.model.Account;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public List<Account> getAll() {
        return repository.findAll();
    }

    @LogDatasourceError
    public Account getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @LogDatasourceError
    public Account save(Account account) {
        return repository.save(account);
    }

    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
