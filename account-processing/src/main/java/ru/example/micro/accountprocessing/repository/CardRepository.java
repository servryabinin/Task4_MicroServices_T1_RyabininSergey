package ru.example.micro.accountprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.accountprocessing.model.Card;

public interface CardRepository extends JpaRepository<Card, Long> {}
