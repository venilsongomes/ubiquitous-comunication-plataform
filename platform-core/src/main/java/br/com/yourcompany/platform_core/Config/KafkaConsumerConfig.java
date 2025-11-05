package br.com.yourcompany.platform_core.Config;

import br.com.yourcompany.platform_core.dto.InternalMessageEvent;
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
import org.telegram.telegrambots.meta.api.objects.Update; // <-- IMPORTAR UPDATE AQUI

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // --- FÁBRICA Nº 1: PARA NOSSOS EVENTOS INTERNOS (O Padrão) ---
    // (Isto permanece igual)
    @Bean
    @Primary
    public ConsumerFactory<String, InternalMessageEvent> internalEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        JsonDeserializer<InternalMessageEvent> deserializer = 
                new JsonDeserializer<>(InternalMessageEvent.class);
        deserializer.addTrustedPackages("br.com.yourcompany.platformcore.dto");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "kafkaListenerContainerFactory")
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, InternalMessageEvent> 
            kafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, InternalMessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(internalEventConsumerFactory());
        return factory;
    }

    // --- FÁBRICA Nº 2: ESTEIRA DEDICADA PARA O TELEGRAM ---
    
    @Bean
    public ConsumerFactory<String, Update> telegramUpdateConsumerFactory() { // <-- TIPO ESPECÍFICO 'Update'
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Não precisamos de cabeçalhos de tipo.
        // Apenas diga ao deserializador para sempre tentar criar um 'Update'.
        JsonDeserializer<Update> deserializer = new JsonDeserializer<>(Update.class); // <-- TIPO ESPECÍFICO 'Update'
        
        // Confie no pacote do Telegram
        deserializer.addTrustedPackages("org.telegram.telegrambots.meta.api.objects");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean(name = "telegramListenerFactory") // <-- NOVO NOME FÁCIL
    public ConcurrentKafkaListenerContainerFactory<String, Update> // <-- TIPO ESPECÍFICO 'Update'
            telegramKafkaListenerContainerFactory() {
        
        ConcurrentKafkaListenerContainerFactory<String, Update> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(telegramUpdateConsumerFactory()); // <-- Usa a nova factory
        return factory;
    }
}