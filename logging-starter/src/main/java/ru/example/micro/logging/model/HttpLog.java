package ru.example.micro.logging.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "http_log")
public class HttpLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String type; // INFO
    private String methodSignature;

    @Lob
    private String inputParams;

    @Lob
    private String responseBody;

    private String uri;
    private LocalDateTime timestamp;

    private String direction; // "IN" или "OUT"

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMethodSignature() { return methodSignature; }
    public void setMethodSignature(String methodSignature) { this.methodSignature = methodSignature; }

    public String getInputParams() { return inputParams; }
    public void setInputParams(String inputParams) { this.inputParams = inputParams; }

    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
}
