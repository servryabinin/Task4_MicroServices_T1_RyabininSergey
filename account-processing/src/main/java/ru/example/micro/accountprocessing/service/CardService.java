package ru.example.micro.accountprocessing.service;

import org.springframework.stereotype.Service;
import ru.example.micro.accountprocessing.model.Card;
import ru.example.micro.accountprocessing.repository.CardRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.util.List;

@Service
public class CardService {
    private final CardRepository repository;

    public CardService(CardRepository repository) {
        this.repository = repository;
    }

    @LogDatasourceError
    public List<Card> getAll() {
        return repository.findAll();
    }

    @LogDatasourceError
    public Card getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @LogDatasourceError
    public Card save(Card card) {
        return repository.save(card);
    }

    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
