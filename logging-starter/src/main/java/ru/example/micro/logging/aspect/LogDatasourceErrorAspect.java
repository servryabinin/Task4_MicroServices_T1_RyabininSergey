package ru.example.micro.logging.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.example.micro.logging.model.ErrorLog;
import ru.example.micro.logging.repository.ErrorLogRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class LogDatasourceErrorAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ErrorLogRepository errorLogRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${logging.service-name}")
    private String serviceName; // Динамическое имя микросервиса

    public LogDatasourceErrorAspect(KafkaTemplate<String, String> kafkaTemplate,
                                    ErrorLogRepository errorLogRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.errorLogRepository = errorLogRepository;
    }

    @Around("@annotation(ru.example.micro.logging.annotation.LogDatasourceError)")
    public Object logError(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Exception ex) {
            // Формируем сообщение для Kafka и БД
            Map<String, Object> logMessage = new HashMap<>();
            logMessage.put("timestamp", LocalDateTime.now().toString());
            logMessage.put("method", joinPoint.getSignature().toShortString());
            logMessage.put("exception", ex.getClass().getName());
            logMessage.put("message", ex.getMessage());
            logMessage.put("stacktrace", ex.getStackTrace());
            logMessage.put("parameters", joinPoint.getArgs());

            Map<String, String> headers = new HashMap<>();
            headers.put("type", "ERROR");

            try {
                // Отправка в Kafka
                kafkaTemplate.send(new ProducerRecord<>("service_logs", serviceName, mapper.writeValueAsString(logMessage)));
            } catch (Exception kafkaEx) {
                // Если Kafka недоступна -> сохраняем в БД
                ErrorLog errorLog = new ErrorLog();
                errorLog.setTimestamp(LocalDateTime.now());
                errorLog.setServiceName(serviceName);
                errorLog.setExceptionText(ex.getMessage());
                errorLog.setMethodSignature(joinPoint.getSignature().toShortString());
                errorLogRepository.save(errorLog);
            }

            // Лог в консоль
            System.err.println("[LogDatasourceError][" + serviceName + "] " + logMessage);
            ex.printStackTrace();

            throw ex; // проброс исключения дальше
        }
    }
}
