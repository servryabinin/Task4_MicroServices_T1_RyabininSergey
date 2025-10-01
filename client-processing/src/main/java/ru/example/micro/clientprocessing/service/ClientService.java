package ru.example.micro.clientprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.clientprocessing.model.Client;
import ru.example.micro.clientprocessing.repository.ClientRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository repository;

    public ClientService(ClientRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public List<Client> getAll() {
        return repository.findAll();
    }

    @LogDatasourceError
    public Client getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @LogDatasourceError
    public Client save(Client client) {
        return repository.save(client);
    }

    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
