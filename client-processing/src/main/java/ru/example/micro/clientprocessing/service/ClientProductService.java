package ru.example.micro.clientprocessing.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.example.micro.clientprocessing.model.ClientProduct;
import ru.example.micro.clientprocessing.model.ProductType;
import ru.example.micro.clientprocessing.model.Status;
import ru.example.micro.clientprocessing.repository.ClientProductRepository;
import ru.example.micro.logging.annotation.LogDatasourceError;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClientProductService {

    private final ClientProductRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ClientProductService(ClientProductRepository repository,
                                KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @LogDatasourceError
    public List<ClientProduct> getAll() {
        return repository.findAll();
    }

    @LogDatasourceError
    public ClientProduct getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @LogDatasourceError
    public ClientProduct create(ClientProduct clientProduct) {
        validate(clientProduct);
        clientProduct.setOpenDate(LocalDateTime.now());
        ClientProduct saved = repository.save(clientProduct);
        sendKafkaMessage(saved, "CREATE");
        return saved;
    }

    @LogDatasourceError
    public ClientProduct update(Long id, ClientProduct clientProduct) {
        validate(clientProduct);
        clientProduct.setId(id);
        ClientProduct saved = repository.save(clientProduct);
        sendKafkaMessage(saved, "UPDATE");
        return saved;
    }

    @LogDatasourceError
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void validate(ClientProduct clientProduct) {
        if (clientProduct.getClientId() == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (clientProduct.getProductId() == null) {
            throw new IllegalArgumentException("productId cannot be null");
        }
        if (clientProduct.getProductType() == null) {
            throw new IllegalArgumentException("productType cannot be null");
        }
        if (clientProduct.getStatus() == null) {
            clientProduct.setStatus(Status.ACTIVE); // по умолчанию
        }
    }

    private void sendKafkaMessage(ClientProduct clientProduct, String action) {
        String topic = getTopicByProductType(clientProduct.getProductType());
        if (topic != null) {
            try {
                String message = objectMapper.writeValueAsString(new KafkaMessage(clientProduct, action));
                kafkaTemplate.send(topic, message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    private String getTopicByProductType(ProductType type) {
        return switch (type) {
            case DC, CC, NS, PENS -> "client_products";
            case IPO, PC, AC -> "client_credit_products";
            default -> null;
        };
    }

    private record KafkaMessage(Long clientId, Long productId, ProductType productType,
                                Status status, LocalDateTime openDate, String action) {
        public KafkaMessage(ClientProduct cp, String action) {
            this(cp.getClientId(), cp.getProductId(), cp.getProductType(),
                    cp.getStatus(), cp.getOpenDate(), action);
        }
    }
}
