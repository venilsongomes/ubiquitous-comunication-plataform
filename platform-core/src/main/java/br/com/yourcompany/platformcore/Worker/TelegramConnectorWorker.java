package br.com.yourcompany.platformcore.Worker;
import org.springframework.kafka.support.KafkaHeaders; // <-- IMPORTAR
import org.springframework.messaging.handler.annotation.Header; // <-- IMPORTAR
import org.springframework.messaging.handler.annotation.Payload; // <-- IMPORTAR
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import br.com.yourcompany.platformcore.connector.TelegramConnectorService;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.repository.UserIdentityRepository;

import java.util.List;

@Service
public class TelegramConnectorWorker {

    private static final Logger logger = LoggerFactory.getLogger(TelegramConnectorWorker.class);

    private static final String TOPIC_OUT_TELEGRAM = "outgoing_telegram";

    @Autowired
    private TelegramConnectorService telegramService;

    //@Autowired
   // private UserIdentityRepository userIdentityRepository; // Precisamos disso

    /**
     * Ouve o novo tópico.
     * ATENÇÃO: O InternalMessageEvent não tem o "external_id".
     * Teremos que modificar este worker na próxima etapa (Tarefa 2.3).
     * Por enquanto, vamos criar um worker que OUVE o tópico.
     */
    @KafkaListener(topics = TOPIC_OUT_TELEGRAM, groupId = "${spring.kafka.consumer.group-id}-telegram")
    public void handleTelegramMessage(
        @Payload InternalMessageEvent event,
        @Header(KafkaHeaders.RECEIVED_KEY) String externalChatId) {

        logger.info("WORKER TELEGRAM: Mensagem {} recebida para entrega no Telegram.", event.getMessageId(), externalChatId);

        // AQUI ESTÁ O DESAFIO DA PRÓXIMA TAREFA:
        // O "event" não nos diz para qual "external_id" (Chat ID) enviar.
        // Ele nos diz o "senderId" e o "conversationId".
        
        // Na Tarefa 2.3, vamos modificar o MessageProcessingWorker para
        // consultar o DB e colocar um "ExternalMessageEvent" neste tópico.
        
        // POR ENQUANTO (para testar este worker):
        // Vamos apenas enviar uma mensagem para um chat ID "mockado".
        // 1. Envie uma mensagem para o seu bot no Telegram (ex: "/start")
        // 2. O log do `TelegramConnectorService` (onUpdateReceived) mostrará seu Chat ID.
        // 3. Coloque esse Chat ID aqui:
        
       // String mockExternalChatId = "691942299"; // 

        telegramService.sendMessage(externalChatId, event.getContent());
    }
}