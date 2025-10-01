package ru.example.micro.clientprocessing.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.example.micro.clientprocessing.model.ClientCard;
import ru.example.micro.clientprocessing.repository.ClientCardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.example.micro.logging.annotation.LogDatasourceError;

@Service
public class ClientCardService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ClientCardService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @LogDatasourceError
    public ClientCard create(ClientCard clientCard) {
        try {
            String message = objectMapper.writeValueAsString(clientCard);
            kafkaTemplate.send("client_cards", message);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing ClientCard", e);
        }
        return clientCard;
    }
}
