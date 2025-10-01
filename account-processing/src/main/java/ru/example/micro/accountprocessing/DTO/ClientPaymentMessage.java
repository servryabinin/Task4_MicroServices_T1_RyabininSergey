package ru.example.micro.accountprocessing.DTO;

import java.util.UUID;

public class ClientPaymentMessage {
    private UUID uuid;
    private Long accountId;
    private Double amount;

    // геттеры и сеттеры
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
