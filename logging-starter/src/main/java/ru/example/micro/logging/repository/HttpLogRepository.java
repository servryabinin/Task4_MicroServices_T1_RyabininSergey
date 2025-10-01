package ru.example.micro.logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.example.micro.logging.model.HttpLog;

@Repository
public interface HttpLogRepository extends JpaRepository<HttpLog, Long> {
}
