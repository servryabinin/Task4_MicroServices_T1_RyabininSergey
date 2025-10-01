package ru.example.micro.accountprocessing.DTO;

public class ClientCardMessage {
    private Long clientId;
    private Long accountId;
    private String cardNumber;
    private String cardType;
    private String status;
    private String issueDate; // рекомендуется LocalDate
    private String action; // optional

    public ClientCardMessage() {}

    public ClientCardMessage(Long clientId, Long accountId, String cardNumber, String cardType,
                             String status, String issueDate, String action) {
        this.clientId = clientId;
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.cardType = cardType;
        this.status = status;
        this.issueDate = issueDate;
        this.action = action;
    }

    // геттеры и сеттеры
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardType() { return cardType; }
    public void setCardType(String cardType) { this.cardType = cardType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIssueDate() { return issueDate; }
    public void setIssueDate(String issueDate) { this.issueDate = issueDate; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
