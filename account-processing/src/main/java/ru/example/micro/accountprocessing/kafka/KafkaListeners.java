package ru.example.micro.accountprocessing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.example.micro.accountprocessing.DTO.ClientPaymentMessage;
import ru.example.micro.accountprocessing.DTO.ClientProductMessage;
import ru.example.micro.accountprocessing.DTO.ClientTransactionMessage;
import ru.example.micro.accountprocessing.DTO.ClientCardMessage;
import ru.example.micro.accountprocessing.model.*;
import ru.example.micro.accountprocessing.service.ClientProductService;
import ru.example.micro.accountprocessing.repository.AccountRepository;
import ru.example.micro.accountprocessing.repository.CardRepository;
import ru.example.micro.accountprocessing.repository.TransactionRepository;
import ru.example.micro.accountprocessing.service.TransactionProcessingService;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class KafkaListeners {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final ClientProductService clientProductService;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionProcessingService transactionProcessingService;

    public KafkaListeners(ClientProductService clientProductService,
                          AccountRepository accountRepository,
                          CardRepository cardRepository,
                          TransactionRepository transactionRepository, TransactionProcessingService transactionProcessingService) {
        this.clientProductService = clientProductService;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.transactionProcessingService = transactionProcessingService;
    }

    @KafkaListener(topics = "client_products", groupId = "account-processing-group")
    public void onClientProduct(String payload) {
        try {
            ClientProductMessage msg = mapper.readValue(payload, ClientProductMessage.class);
            ClientProduct p = new ClientProduct();
            p.setClientId(msg.getClientId());
            p.setProductId(msg.getProductId());
            p.setProductType(msg.getProductType());
            p.setStatus(msg.getStatus());
            if (msg.getOpenDate() != null) p.setOpenDate(LocalDateTime.parse(msg.getOpenDate()));
            clientProductService.save(p);
            System.out.println("Saved client product from kafka: " + payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "client_transactions", groupId = "account-processing-group")
    public void onClientTransaction(ConsumerRecord<String, String> record) {
        try {
            String key = record.key(); // UUID
            String payload = record.value();
            ClientTransactionMessage msg = mapper.readValue(payload, ClientTransactionMessage.class);
            transactionProcessingService.processTransaction(key, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "client_cards", groupId = "account-processing-group")
    public void onClientCard(String payload) {
        try {
            ClientCardMessage msg = mapper.readValue(payload, ClientCardMessage.class);
            Long accountId = msg.getAccountId();
            Optional<Account> optAcc = accountRepository.findById(accountId);
            if (!optAcc.isPresent()) {
                System.out.println("Account not found for card creation: " + accountId);
                return;
            }
            Account account = optAcc.get();

            if (account.getStatus() == Status.BLOCKED) {
                System.out.println("Account is blocked, ignoring card creation for account: " + accountId);
                return;
            }
            // создать карту
            Card card = new Card();
            card.setAccountId(accountId);
            card.setCardId(msg.getCardNumber());
            card.setPaymentSystem(msg.getCardType());
            card.setStatus(CardStatus.valueOf(msg.getStatus()));
            cardRepository.save(card);
            System.out.println("Saved card from kafka: " + payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "client_payments", groupId = "account-processing-group")
    public void onClientPayment(ConsumerRecord<String, String> record) {
        try {
            String key = record.key(); // UUID
            String payload = record.value();
            ClientPaymentMessage msg = mapper.readValue(payload, ClientPaymentMessage.class);

            // передаем в сервис для обработки
            transactionProcessingService.processClientPayment(msg);

            System.out.println("Processed client payment from kafka, key: " + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
