package ru.example.micro.clientprocessing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.example.micro.clientprocessing.model.*;
import ru.example.micro.clientprocessing.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final ClientProductRepository clientProductRepository;
    private final BlackListRepository blackListRepository;
    private final ClientCardRepository clientCardRepository;

    public DataInitializer(UserRepository userRepository,
                           ClientRepository clientRepository,
                           ProductRepository productRepository,
                           ClientProductRepository clientProductRepository,
                           BlackListRepository blackListRepository, ClientCardRepository clientCardRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.clientProductRepository = clientProductRepository;
        this.blackListRepository = blackListRepository;
        this.clientCardRepository = clientCardRepository;
    }

    @Override
    public void run(String... args) {
        // --- Users ---
        if (userRepository.count() == 0) {
            User u1 = new User();
            u1.setLogin("john");
            u1.setPassword("pass123");
            u1.setEmail("john@example.com");
            userRepository.save(u1);

            User u2 = new User();
            u2.setLogin("alice");
            u2.setPassword("pass456");
            u2.setEmail("alice@example.com");
            userRepository.save(u2);
        }

        // --- Clients ---
        if (clientRepository.count() == 0) {
            Client c1 = new Client();
            c1.setClientId("770100000001");
            c1.setUserId(1L);
            c1.setFirstName("John");
            c1.setLastName("Doe");
            c1.setDateOfBirth(LocalDate.of(1990,1,1));
            c1.setDocumentType(DocumentType.PASSPORT);
            c1.setDocumentId("123456");
            clientRepository.save(c1);

            Client c2 = new Client();
            c2.setClientId("770200000002");
            c2.setUserId(2L);
            c2.setFirstName("Alice");
            c2.setLastName("Smith");
            c2.setDateOfBirth(LocalDate.of(1992,2,2));
            c2.setDocumentType(DocumentType.INT_PASSPORT);
            c2.setDocumentId("987654");
            clientRepository.save(c2);
        }

        // --- Products ---
        if (productRepository.count() == 0) {
            Product p1 = new Product();
            p1.setName("Deposit Account");
            p1.setProductKey("DC");
            p1.setProductId("DC1");
            p1.setCreateDate(LocalDateTime.now());
            productRepository.save(p1);

            Product p2 = new Product();
            p2.setName("Credit Card");
            p2.setProductKey("CC");
            p2.setProductId("CC2");
            p2.setCreateDate(LocalDateTime.now());
            productRepository.save(p2);
        }

        // --- ClientProducts ---
        if (clientProductRepository.count() == 0) {
            ClientProduct cp1 = new ClientProduct();
            cp1.setClientId(1L);
            cp1.setProductId(1L);
            cp1.setStatus(Status.ACTIVE);
            cp1.setOpenDate(LocalDateTime.now());
            cp1.setProductType(ProductType.DC);
            clientProductRepository.save(cp1);

            ClientProduct cp2 = new ClientProduct();
            cp2.setClientId(2L);
            cp2.setProductId(2L);
            cp2.setStatus(Status.CLOSED);
            cp2.setOpenDate(LocalDateTime.now());
            cp2.setProductType(ProductType.CC);
            clientProductRepository.save(cp2);
        }

        // --- BlackList ---
        if (blackListRepository.count() == 0) {
            BlackListEntry b1 = new BlackListEntry();
            b1.setDocumentId("000000001");
            blackListRepository.save(b1);

            BlackListEntry b2 = new BlackListEntry();
            b2.setDocumentId("000000002");
            blackListRepository.save(b2);
        }

        // --- ClientCards ---
        if (clientCardRepository.count() == 0) {
            ClientCard card1 = new ClientCard();
            card1.setClientId(1L);
            card1.setAccountId(1L);
            card1.setCardNumber("1111-2222-3333-4444");
            card1.setStatus(CardStatus.ACTIVE);
            clientCardRepository.save(card1);

            ClientCard card2 = new ClientCard();
            card2.setClientId(2L);
            card2.setAccountId(2L);
            card2.setCardNumber("5555-6666-7777-8888");
            card2.setStatus(CardStatus.ACTIVE);
            clientCardRepository.save(card2);
        }

    }
}
