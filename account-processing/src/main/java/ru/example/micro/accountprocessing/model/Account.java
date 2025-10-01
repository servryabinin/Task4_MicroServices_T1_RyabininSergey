package ru.example.micro.accountprocessing.model;

import jakarta.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;
    private Long productId;
    private Double balance;
    private Double interestRate;
    private Boolean isRecalc;
    private Boolean cardExist;

    @Enumerated(EnumType.STRING)
    private Status status;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public Boolean getIsRecalc() { return isRecalc; }
    public void setIsRecalc(Boolean isRecalc) { this.isRecalc = isRecalc; }
    public Boolean getCardExist() { return cardExist; }
    public void setCardExist(Boolean cardExist) { this.cardExist = cardExist; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}

