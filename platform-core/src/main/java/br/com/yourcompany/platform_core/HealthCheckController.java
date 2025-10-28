package br.com.yourcompany.platform_core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    @Autowired
    private ConnectivityCheckRepository repository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String KAFKA_TOPIC = "health-check-topic";

    @GetMapping("/db")
    public String checkDatabase() {
        try {
            ConnectivityCheck check = new ConnectivityCheck("PostgreSQL");
            repository.save(check);
            long count = repository.count();
            return "SUCCESS: Connected to PostgreSQL. Total checks recorded: " + count;
        } catch (Exception e) {
            return "ERROR: Could not connect to PostgreSQL. " + e.getMessage();
        }
    }

    @GetMapping("/kafka")
    public String checkKafka() {
        try {
            String message = "Kafka health check at " + Instant.now().toString();
            kafkaTemplate.send(KAFKA_TOPIC, message);
            return "SUCCESS: Message sent to Kafka topic '" + KAFKA_TOPIC + "'.";
        } catch (Exception e) {
            return "ERROR: Could not send message to Kafka. " + e.getMessage();
        }
    }
}