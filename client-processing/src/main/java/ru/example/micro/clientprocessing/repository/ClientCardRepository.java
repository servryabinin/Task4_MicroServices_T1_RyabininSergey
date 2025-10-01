package ru.example.micro.clientprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.clientprocessing.model.ClientCard;

public interface ClientCardRepository extends JpaRepository<ClientCard, Long> {
}
