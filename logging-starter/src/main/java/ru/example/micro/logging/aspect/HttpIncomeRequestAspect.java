package ru.example.micro.logging.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.example.micro.logging.model.HttpLog;
import ru.example.micro.logging.repository.HttpLogRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class HttpIncomeRequestAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final HttpLogRepository httpLogRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${logging.service-name}")
    private String serviceName; // динамическое имя микросервиса

    public HttpIncomeRequestAspect(KafkaTemplate<String, String> kafkaTemplate,
                                   HttpLogRepository httpLogRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.httpLogRepository = httpLogRepository;
    }

    @Before("@annotation(ru.example.micro.logging.annotation.HttpIncomeRequestLog)")
    public void logHttpIncome(JoinPoint joinPoint) {

        // Формируем сообщение
        Map<String, Object> message = new HashMap<>();
        message.put("timestamp", LocalDateTime.now().toString());
        message.put("serviceName", serviceName);
        message.put("methodSignature", joinPoint.getSignature().toLongString());
        message.put("parameters", Arrays.toString(joinPoint.getArgs()));
        message.put("uri", extractUri(joinPoint.getArgs()));

        // Заголовок
        Map<String, String> headers = new HashMap<>();
        headers.put("type", "INFO");

        try {
            // Отправка в Kafka
            kafkaTemplate.send("service_logs", serviceName, mapper.writeValueAsString(message));
        } catch (Exception e) {
            // Если Kafka недоступна, сохраняем в БД
            HttpLog log = new HttpLog();
            log.setServiceName(serviceName);
            log.setType("INFO");
            log.setMethodSignature(joinPoint.getSignature().toLongString());
            log.setInputParams(Arrays.toString(joinPoint.getArgs()));
            log.setUri(extractUri(joinPoint.getArgs()));
            log.setTimestamp(LocalDateTime.now());
            log.setDirection("IN");
            httpLogRepository.save(log);
        }

        // Логирование в консоль
        System.out.println("[HttpIncomeRequestLog][" + serviceName + "] " + message);
    }

    private String extractUri(Object[] args) {
        if (args != null && args.length > 0 && args[0] != null) {
            return args[0].toString();
        }
        return "unknown";
    }
}
