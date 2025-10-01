package ru.example.micro.clientprocessing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.example.micro.clientprocessing.model.BlackListEntry;
import ru.example.micro.clientprocessing.service.BlackListService;
import ru.example.micro.logging.annotation.HttpIncomeRequestLog;
import ru.example.micro.logging.annotation.HttpOutcomeRequestLog;

import java.util.List;

@RestController
@RequestMapping("/api/blacklist")
public class BlackListController {

    private final BlackListService service;

    public BlackListController(BlackListService service) {
        this.service = service;
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @GetMapping
    public List<BlackListEntry> getAll() {
        return service.getAll();
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @PostMapping
    public BlackListEntry add(@RequestParam String documentId, @RequestParam(required = false) String reason) {
        return service.addToBlackList(documentId, reason);
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> remove(@PathVariable String documentId) {
        service.removeFromBlackList(documentId);
        return ResponseEntity.noContent().build();
    }

    @HttpIncomeRequestLog
    @HttpOutcomeRequestLog
    @GetMapping("/check/{documentId}")
    public ResponseEntity<Boolean> check(@PathVariable String documentId) {
        return ResponseEntity.ok(service.isBlocked(documentId));
    }
}
