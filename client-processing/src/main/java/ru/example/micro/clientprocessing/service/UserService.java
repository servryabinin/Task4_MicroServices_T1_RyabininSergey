package ru.example.micro.clientprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.clientprocessing.model.User;
import ru.example.micro.clientprocessing.repository.UserRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public List<User> getAll() {
        return repository.findAll();
    }

    @LogDatasourceError
    public User getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @LogDatasourceError
    public User save(User user) {
        return repository.save(user);
    }

    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
