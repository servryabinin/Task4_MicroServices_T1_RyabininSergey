package ru.example.micro.accountprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.accountprocessing.model.ClientProduct;
import ru.example.micro.accountprocessing.repository.ClientProductRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

@Service
public class ClientProductService {
    private final ClientProductRepository repo;

    public ClientProductService(ClientProductRepository repo) {
        this.repo = repo;
    }

    @LogDatasourceError
    public ClientProduct save(ClientProduct p) {
        return repo.save(p);
    }
}
