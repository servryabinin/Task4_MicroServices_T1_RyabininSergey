package ru.example.micro.clientprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.clientprocessing.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
