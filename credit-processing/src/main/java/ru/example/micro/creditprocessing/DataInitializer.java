package ru.example.micro.creditprocessing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.example.micro.creditprocessing.model.*;
import ru.example.micro.creditprocessing.repository.*;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRegistryRepository productRegistryRepository;
    private final PaymentRegistryRepository paymentRegistryRepository;

    public DataInitializer(ProductRegistryRepository productRegistryRepository,
                           PaymentRegistryRepository paymentRegistryRepository) {
        this.productRegistryRepository = productRegistryRepository;
        this.paymentRegistryRepository = paymentRegistryRepository;
    }

    @Override
    public void run(String... args) {
        // ProductRegistry
        if (productRegistryRepository.count() == 0) {
            ProductRegistry pr1 = new ProductRegistry();
            pr1.setClientId(1L);
            pr1.setAccountId(1L);
            pr1.setProductId(1L);
            pr1.setInterestRate(5.0);
            pr1.setOpenDate(LocalDateTime.now());
            productRegistryRepository.save(pr1);
        }

        // PaymentRegistry
        if (paymentRegistryRepository.count() == 0) {
            PaymentRegistry pay1 = new PaymentRegistry();
            pay1.setProductRegistryId(1L);
            pay1.setAmount(100.0);
            pay1.setInterestRateAmount(5.0);
            pay1.setDebtAmount(50.0);
            pay1.setExpired(false);
            pay1.setPaymentDate(LocalDateTime.now());
            pay1.setPaymentExpirationDate(LocalDateTime.now().plusDays(30));
            paymentRegistryRepository.save(pay1);
        }
    }
}
