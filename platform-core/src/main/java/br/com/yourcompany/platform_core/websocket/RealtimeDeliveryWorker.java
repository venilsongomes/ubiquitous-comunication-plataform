package br.com.yourcompany.platform_core.websocket;
import br.com.yourcompany.platform_core.dto.InternalMessageEvent;
import br.com.yourcompany.platform_core.websocket.RealtimeMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RealtimeDeliveryWorker {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeDeliveryWorker.class);

    private static final String TOPIC_IN_INTERNAL = "internal_delivery";

    @Autowired
    private RealtimeMessageHandler messageHandler; // O Handler que criamos

    // Ouve o tópico onde o MessageProcessingWorker publicou
    @KafkaListener(topics = TOPIC_IN_INTERNAL, groupId = "${spring.kafka.consumer.group-id}-realtime")
    public void handleRealtimeMessage(InternalMessageEvent event) {
        
        logger.info("WORKER REAL-TIME: Mensagem {} recebida para entrega.", event.getMessageId());
        
        // Chama o Handler para enviar a mensagem via WebSocket
        messageHandler.sendMessageToConversation(event);
    }
}