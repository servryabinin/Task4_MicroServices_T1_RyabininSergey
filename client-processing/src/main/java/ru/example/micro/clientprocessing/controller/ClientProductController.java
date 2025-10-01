package ru.example.micro.clientprocessing.controller;

import org.springframework.web.bind.annotation.*;
import ru.example.micro.clientprocessing.model.ClientProduct;
import ru.example.micro.clientprocessing.service.ClientProductService;
import ru.example.micro.logging.annotation.HttpIncomeRequestLog;
import ru.example.micro.logging.annotation.HttpOutcomeRequestLog;

import java.util.List;

@RestController
@RequestMapping("/api/client-products")
public class ClientProductController {

    private final ClientProductService service;

    public ClientProductController(ClientProductService service) {
        this.service = service;
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @GetMapping
    public List<ClientProduct> getAll() {
        return service.getAll();
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @GetMapping("/{id}")
    public ClientProduct getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @PostMapping
    public ClientProduct create(@RequestBody ClientProduct clientProduct) {
        return service.create(clientProduct);
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @PutMapping("/{id}")
    public ClientProduct update(@PathVariable Long id, @RequestBody ClientProduct clientProduct) {
        return service.update(id, clientProduct);
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
