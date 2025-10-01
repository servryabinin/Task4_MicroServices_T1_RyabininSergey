package ru.example.micro.clientprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.clientprocessing.model.BlackListEntry;
import ru.example.micro.clientprocessing.repository.BlackListRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class BlackListService {

    private final BlackListRepository repository;

    public BlackListService(BlackListRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public boolean isBlocked(String documentId) {
        return repository.existsByDocumentId(documentId);
    }

    @LogDatasourceError
    public BlackListEntry addToBlackList(String documentId, String reason) {
        if (isBlocked(documentId)) return repository.findByDocumentId(documentId);
        BlackListEntry entry = new BlackListEntry();
        entry.setDocumentId(documentId);
        entry.setReason(reason);
        return repository.save(entry);
    }

    @LogDatasourceError
    public void removeFromBlackList(String documentId) {
        BlackListEntry entry = repository.findByDocumentId(documentId);
        if (entry != null) {
            repository.delete(entry);
        }
    }

    @LogDatasourceError
    public List<BlackListEntry> getAll() {
        return repository.findAll();
    }
}
