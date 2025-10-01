package ru.example.micro.clientprocessing.controller;

import org.springframework.web.bind.annotation.*;
import ru.example.micro.clientprocessing.model.ClientCard;
import ru.example.micro.clientprocessing.service.ClientCardService;
import ru.example.micro.logging.annotation.HttpIncomeRequestLog;
import ru.example.micro.logging.annotation.HttpOutcomeRequestLog;

@RestController
@RequestMapping("/api/client-cards")
public class ClientCardController {

    private final ClientCardService service;

    public ClientCardController(ClientCardService service) {
        this.service = service;
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @PostMapping
    public ClientCard create(@RequestBody ClientCard card) {
        return service.create(card);
    }
}
