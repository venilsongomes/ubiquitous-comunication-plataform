package br.com.yourcompany.platformcore.worker;
import br.com.yourcompany.platformcore.domain.conversation.Conversation;
import br.com.yourcompany.platformcore.domain.conversation.ConversationParticipant;
import br.com.yourcompany.platformcore.domain.message.Message;
import br.com.yourcompany.platformcore.domain.message.MessageRecipientStatus;
import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.domain.user.UserIdentity;
import br.com.yourcompany.platformcore.dto.InternalDeliveryEvent;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.grpc.PresenceServiceGrpc; // <-- Classe gerada
import br.com.yourcompany.platformcore.grpc.UserPresenceRequest; // <-- Classe gerada
import br.com.yourcompany.platformcore.grpc.UserPresenceResponse;// <-- Classe gerada
import br.com.yourcompany.platformcore.repository.*;
import net.devh.boot.grpc.client.inject.GrpcClient; // <-- Anotação do Cliente
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageProcessingWorker {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingWorker.class);

    private static final String TOPIC_INBOUND = "inbound_messages";
    private static final String TOPIC_OUT_INTERNAL = "internal_delivery";
    private static final String TOPIC_OUT_TELEGRAM = "outgoing_telegram";

    @Autowired private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired private MessageRepository messageRepository;
    @Autowired private ConversationRepository conversationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ConversationParticipantRepository participantRepository;
    @Autowired private UserIdentityRepository userIdentityRepository;
    @Autowired private MessageRecipientStatusRepository recipientStatusRepository;

    // --- O CLIENTE gRPC ---
    // "presenceService" deve bater com o nome configurado no application.properties
    @GrpcClient("presenceService") 
    private PresenceServiceGrpc.PresenceServiceBlockingStub presenceClient;

    @KafkaListener(topics = TOPIC_INBOUND, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleMessage(InternalMessageEvent event) {
        try {
            User sender = userRepository.findById(event.getSenderId()).orElseThrow();
            Conversation conversation = conversationRepository.findById(event.getConversationId()).orElseThrow();

            Message message = new Message(event.getMessageId(), conversation, sender, event.getContent());
            Message savedMessage = messageRepository.save(message);
            
            List<ConversationParticipant> participants = 
                    participantRepository.findByConversationId(event.getConversationId());

            for (ConversationParticipant participant : participants) {
                User recipient = participant.getUser();
                if (recipient.getId().equals(event.getSenderId())) continue;

                routeMessageToRecipient(event, recipient, savedMessage);
            }
        } catch (Exception e) {
            logger.error("Erro no processamento: ", e);
        }
    }

    private void routeMessageToRecipient(InternalMessageEvent event, User recipient, Message savedMessage) {
        // 1. Salva status SENT
        recipientStatusRepository.save(new MessageRecipientStatus(savedMessage, recipient, "SENT"));

        // 2. CHECK gRPC: O usuário está online?
        boolean isOnline = false;
        try {
            UserPresenceRequest request = UserPresenceRequest.newBuilder()
                    .setUserId(recipient.getId().toString())
                    .build();
            
            // --- CHAMADA REMOTA ---
            UserPresenceResponse response = presenceClient.isUserOnline(request);
            isOnline = response.getIsOnline();
            
            logger.info("gRPC Check: User {} está online? {}", recipient.getId(), isOnline);
        } catch (Exception e) {
            logger.error("Falha na chamada gRPC: {}", e.getMessage());
        }

        // 3. Se ONLINE -> Manda para WebSocket
        if (isOnline) {
            InternalDeliveryEvent deliveryEvent = new InternalDeliveryEvent(
                event.getMessageId(), event.getConversationId(), event.getSenderId(),
                event.getContent(), event.getTimestamp(), recipient.getId()
            );
            kafkaTemplate.send(TOPIC_OUT_INTERNAL, event.getConversationId().toString(), deliveryEvent);
            logger.info(">> Roteado para WebSocket (Online)");
        } else {
            logger.info("XX Não roteado para WebSocket (Offline)");
        }

        // 4. Check de Identidades Externas (Telegram)
        // ... (seu código do Telegram continua aqui) ...
         List<UserIdentity> identities = userIdentityRepository.findByUserId(recipient.getId());
         // ... (loop das identidades)
         for (UserIdentity identity : identities) {
             if ("telegram".equals(identity.getPlatform())) {
                 kafkaTemplate.send(TOPIC_OUT_TELEGRAM, identity.getExternalId(), event);
                 logger.info(">> Roteado para Telegram");
             }
         }
    }
}