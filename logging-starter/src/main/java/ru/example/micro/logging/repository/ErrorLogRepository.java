package ru.example.micro.logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.logging.model.ErrorLog;

public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
}
