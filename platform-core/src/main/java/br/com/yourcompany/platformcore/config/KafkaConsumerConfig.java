package br.com.yourcompany.platformcore.config;
import br.com.yourcompany.platformcore.dto.InternalDeliveryEvent;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // --- FÁBRICA Nº 1: PARA NOSSOS EVENTOS INTERNOS (O Padrão) ---
    // (Este é o que estava com o nome errado)
    @Bean
    @Primary
    public ConsumerFactory<String, InternalMessageEvent> internalEventConsumerFactory() { // <-- NOME CORRIGIDO
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        JsonDeserializer<InternalMessageEvent> deserializer =
                new JsonDeserializer<>(InternalMessageEvent.class);
        deserializer.addTrustedPackages("br.com.yourcompany.platformcore.dto");
        deserializer.setUseTypeHeaders(true); // Lê os rótulos de tipo

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "kafkaListenerContainerFactory") // <-- Este é o da sua foto
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, InternalMessageEvent>
            kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, InternalMessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(internalEventConsumerFactory()); // <-- Agora vai encontrar!
        return factory;
    }

    // --- FÁBRICA Nº 2: ESTEIRA DEDICADA PARA O TELEGRAM ---
    // (Esta deve estar correta)
    @Bean
    public ConsumerFactory<String, Update> telegramUpdateConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        JsonDeserializer<Update> deserializer = new JsonDeserializer<>(Update.class);
        deserializer.addTrustedPackages("org.telegram.telegrambots.meta.api.objects");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "telegramListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Update>
            telegramKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Update> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(telegramUpdateConsumerFactory());
        return factory;
    }
    
    // --- FÁBRICA Nº 3: ESTEIRA DEDICADA PARA ENTREGA INTERNA ---
    // (Este era o bean que estava faltando no seu erro anterior)
    @Bean
    public ConsumerFactory<String, InternalDeliveryEvent> deliveryEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        JsonDeserializer<InternalDeliveryEvent> deserializer =
                new JsonDeserializer<>(InternalDeliveryEvent.class);
        deserializer.addTrustedPackages("br.com.yourcompany.platformcore.dto");
        deserializer.setUseTypeHeaders(true); // Lê os rótulos de tipo

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "deliveryListenerFactory") // <-- Este é o bean que o Spring não encontrou
    public ConcurrentKafkaListenerContainerFactory<String, InternalDeliveryEvent>
            deliveryKafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, InternalDeliveryEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryEventConsumerFactory());
        return factory;
    }

    @Bean
    public NewTopic inboundMessagesTopic() {
        return TopicBuilder.name("inbound_messages")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic internalDeliveryTopic() {
        return TopicBuilder.name("internal_delivery")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic outgoingTelegramTopic() {
        return TopicBuilder.name("outgoing_telegram")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic incomingTelegramTopic() {
        return TopicBuilder.name("incoming_telegram_updates")
                .partitions(1)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic statusUpdatesTopic() {
        return TopicBuilder.name("status_updates") // <-- O TÓPICO QUE FALTAVA
                .partitions(1)
                .replicas(1)
                .build();
    }
}