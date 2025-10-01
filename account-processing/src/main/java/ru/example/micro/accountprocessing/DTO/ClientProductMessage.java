package ru.example.micro.accountprocessing.DTO;

public class ClientProductMessage {
    private Long clientId;
    private Long productId;
    private String productType;
    private String status;
    private String openDate; // ISO string
    private String action; // optional: CREATE/UPDATE

    public Long getClientId() {
        return clientId;
    }
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenDate() {
        return openDate;
    }
    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
}
