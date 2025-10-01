package ru.example.micro.creditprocessing.controller;

import org.springframework.web.bind.annotation.*;
import ru.example.micro.creditprocessing.model.PaymentRegistry;
import ru.example.micro.creditprocessing.service.PaymentRegistryService;
import ru.example.micro.logging.annotation.HttpIncomeRequestLog;
import ru.example.micro.logging.annotation.HttpOutcomeRequestLog;

import java.util.List;

@RestController
@RequestMapping("/api/payment-registries")
public class PaymentRegistryController {
    private final PaymentRegistryService service;

    public PaymentRegistryController(PaymentRegistryService service) {
        this.service = service;
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @GetMapping
    public List<PaymentRegistry> getAll() { return service.getAll(); }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @GetMapping("/{id}")
    public PaymentRegistry getById(@PathVariable Long id) { return service.getById(id); }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @PostMapping
    public PaymentRegistry create(@RequestBody PaymentRegistry payment) { return service.save(payment); }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @PutMapping("/{id}")
    public PaymentRegistry update(@PathVariable Long id, @RequestBody PaymentRegistry payment) {
        payment.setId(id);
        return service.save(payment);
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }
}
