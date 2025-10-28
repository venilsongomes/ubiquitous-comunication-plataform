package br.com.yourcompany.platform_core.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.yourcompany.platform_core.dto.InternalMessageEvent;

@Service
public class MessageIngestionService {

    private static final Logger logger = LoggerFactory.getLogger(MessageIngestionService.class);

    // O nome do nosso tópico de entrada principal
    private static final String TOPIC = "inbound_messages"; 

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void submitMessage(InternalMessageEvent event) {
        try {
            // A chave da mensagem (messageId) garante que mensagens com o mesmo ID
            // caiam na mesma partição do Kafka, ajudando a manter a ordem.
            String key = event.getMessageId().toString();
            
            kafkaTemplate.send(TOPIC, key, event);
            
            logger.info("Mensagem publicada no Kafka. Key: {}", key);

        } catch (Exception e) {
            logger.error("ERRO ao publicar mensagem no Kafka.", e);
            // Em um sistema real, aqui você publicaria em uma "dead-letter-queue"
            throw new RuntimeException("Falha ao submeter mensagem ao broker.", e);
        }
    }
}