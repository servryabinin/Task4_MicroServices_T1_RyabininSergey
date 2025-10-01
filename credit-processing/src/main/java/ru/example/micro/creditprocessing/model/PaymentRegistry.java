package ru.example.micro.creditprocessing.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_registry")
public class PaymentRegistry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productRegistryId;
    private LocalDateTime paymentDate;
    private Double amount;
    private Double interestRateAmount;
    private Double debtAmount;
    private Boolean expired;
    private LocalDateTime paymentExpirationDate;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductRegistryId() { return productRegistryId; }
    public void setProductRegistryId(Long productRegistryId) { this.productRegistryId = productRegistryId; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getInterestRateAmount() { return interestRateAmount; }
    public void setInterestRateAmount(Double interestRateAmount) { this.interestRateAmount = interestRateAmount; }

    public Double getDebtAmount() { return debtAmount; }
    public void setDebtAmount(Double debtAmount) { this.debtAmount = debtAmount; }

    public Boolean getExpired() { return expired; }
    public void setExpired(Boolean expired) { this.expired = expired; }

    public LocalDateTime getPaymentExpirationDate() { return paymentExpirationDate; }
    public void setPaymentExpirationDate(LocalDateTime paymentExpirationDate) { this.paymentExpirationDate = paymentExpirationDate; }
}
