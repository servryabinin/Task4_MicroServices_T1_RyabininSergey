package ru.example.micro.accountprocessing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private String cardId;
    private String paymentSystem;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    // Getters и Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getPaymentSystem() { return paymentSystem; }
    public void setPaymentSystem(String paymentSystem) { this.paymentSystem = paymentSystem; }

    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
}
