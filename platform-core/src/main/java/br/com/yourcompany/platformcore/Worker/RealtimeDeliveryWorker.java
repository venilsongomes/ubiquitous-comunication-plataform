package br.com.yourcompany.platformcore.worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.dto.InternalDeliveryEvent;
import br.com.yourcompany.platformcore.websocket.RealtimeMessageHandler;

@Service
public class RealtimeDeliveryWorker {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeDeliveryWorker.class);

    private static final String TOPIC_IN_INTERNAL = "internal_delivery";

    @Autowired
    private RealtimeMessageHandler messageHandler; // O Handler que criamos

    // Ouve o tópico onde o MessageProcessingWorker publicou
    @KafkaListener(topics = TOPIC_IN_INTERNAL,
       groupId = "${spring.kafka.consumer.group-id}-realtime", containerFactory = "deliveryListenerFactory")

    public void handleRealtimeMessage(InternalDeliveryEvent event) {
        
       logger.info("WORKER REAL-TIME: Mensagem {} recebida para entrega ao user {}.", 
                event.getMessageId(), event.getRecipientId());
        
        // Chama o Handler para enviar a mensagem via WebSocket
       messageHandler.sendMessageToRecipient(event);
    }
}