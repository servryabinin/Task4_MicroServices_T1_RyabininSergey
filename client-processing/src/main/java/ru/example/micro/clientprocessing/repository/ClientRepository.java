package ru.example.micro.clientprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.clientprocessing.model.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
