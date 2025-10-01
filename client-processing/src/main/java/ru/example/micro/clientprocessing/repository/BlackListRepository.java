package ru.example.micro.clientprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.micro.clientprocessing.model.BlackListEntry;

public interface BlackListRepository extends JpaRepository<BlackListEntry, Long> {
    boolean existsByDocumentId(String documentId);
    BlackListEntry findByDocumentId(String documentId);
}
