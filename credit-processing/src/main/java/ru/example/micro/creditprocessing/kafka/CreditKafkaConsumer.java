package ru.example.micro.creditprocessing.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.example.micro.creditprocessing.DTO.ClientCreditProductMessage;
import ru.example.micro.creditprocessing.model.PaymentRegistry;
import ru.example.micro.creditprocessing.model.ProductRegistry;
import ru.example.micro.creditprocessing.repository.PaymentRegistryRepository;
import ru.example.micro.creditprocessing.repository.ProductRegistryRepository;

import java.time.LocalDateTime;

@Service
public class CreditKafkaConsumer {

    private final ProductRegistryRepository productRepo;
    private final PaymentRegistryRepository paymentRepo;
    private final RestTemplate restTemplate;
    @Value("${credit.limit}")
    private double creditLimit;


    public CreditKafkaConsumer(ProductRegistryRepository productRepo,
                               PaymentRegistryRepository paymentRepo,
                               RestTemplate restTemplate) {
        this.productRepo = productRepo;
        this.paymentRepo = paymentRepo;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "client_credit_products", groupId = "credit-processing-group")
    public void handleCreditProduct(String messageJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ClientCreditProductMessage msg = mapper.readValue(messageJson, ClientCreditProductMessage.class);

        // 1. Получаем ФИО и документ из МС-1
        String clientInfo = restTemplate.getForObject(
                "http://localhost:8081/api/clients/" + msg.getClientId(),
                String.class
        );
        System.out.println("Client info: " + clientInfo);

        // 2. Проверка лимитов
        double existingTotal = productRepo.findAll().stream()
                .filter(p -> p.getClientId().equals(msg.getClientId()))
                .mapToDouble(ProductRegistry::getInterestRate)
                .sum();

        if (existingTotal + msg.getAmount() > creditLimit) {
            System.out.println("❌ Отказ: превышен лимит по кредитам");
            return;
        }

        // TODO: проверка просрочек (по payment_registry)

        // 3. Создаём ProductRegistry
        ProductRegistry product = new ProductRegistry();
        product.setClientId(msg.getClientId());
        product.setProductId(msg.getProductId());
        product.setInterestRate(msg.getInterestRate());
        product.setMonthCount(msg.getMonthCount());
        product.setOpenDate(LocalDateTime.now());
        productRepo.save(product);

        generateSchedule(product, msg.getAmount(), msg.getInterestRate(), msg.getMonthCount());
    }

    private void generateSchedule(ProductRegistry product, double amount, double annualRate, int months) {
        double i = annualRate / 12 / 100.0;
        double annuity = amount * (i * Math.pow(1 + i, months)) / (Math.pow(1 + i, months) - 1);

        double remaining = amount;

        for (int m = 1; m <= months; m++) {
            double interest = remaining * i;
            double principal = annuity - interest;
            remaining -= principal;

            PaymentRegistry pay = new PaymentRegistry();
            pay.setProductRegistryId(product.getId());
            pay.setPaymentDate(LocalDateTime.now().plusMonths(m));
            pay.setAmount(annuity);
            pay.setInterestRateAmount(interest);
            pay.setDebtAmount(principal);
            pay.setExpired(false);
            pay.setPaymentExpirationDate(LocalDateTime.now().plusMonths(m));

            paymentRepo.save(pay);
        }
    }
}
