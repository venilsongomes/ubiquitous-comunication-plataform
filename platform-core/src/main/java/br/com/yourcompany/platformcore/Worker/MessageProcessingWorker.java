package br.com.yourcompany.platformcore.Worker;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.yourcompany.platformcore.domain.Conversation.Conversation;
import br.com.yourcompany.platformcore.domain.Conversation.ConversationParticipant;
import br.com.yourcompany.platformcore.domain.message.Message;
import br.com.yourcompany.platformcore.domain.message.MessageRecipientStatus;
import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.domain.user.UserIdentity;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.dto.InternalDeliveryEvent;
import br.com.yourcompany.platformcore.repository.ConversationParticipantRepository;
import br.com.yourcompany.platformcore.repository.ConversationRepository;
import br.com.yourcompany.platformcore.repository.MessageRecipientStatusRepository;
import br.com.yourcompany.platformcore.repository.MessageRepository;
import br.com.yourcompany.platformcore.repository.UserIdentityRepository;
import br.com.yourcompany.platformcore.repository.UserRepository;


import java.util.List;

@Service
public class MessageProcessingWorker {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingWorker.class);
    @Autowired
    private MessageRecipientStatusRepository messageRecipientStatusRepository;

    // Tópicos que este worker produz
    private static final String TOPIC_INBOUND = "inbound_messages";
    private static final String TOPIC_OUT_INTERNAL = "internal_delivery";
    private static final String TOPIC_OUT_TELEGRAM = "outgoing_telegram"; // <-- NOVO

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Repositórios para salvar
    @Autowired private MessageRepository messageRepository;
    @Autowired private ConversationRepository conversationRepository;
    @Autowired private UserRepository userRepository;
    
    // Repositórios para rotear
    @Autowired private ConversationParticipantRepository participantRepository; // <-- NOVO
    @Autowired private UserIdentityRepository userIdentityRepository; // <-- NOV

    /**
     * Novo método de ajuda para rotear uma mensagem para um único destinatário.
     */

    private void routeMessageToRecipient(InternalMessageEvent event, User recipient, Message savedMessage) {

        MessageRecipientStatus status = new MessageRecipientStatus(
            savedMessage,
            recipient,
            "SENT" 
    );
        messageRecipientStatusRepository.save(status);

        InternalDeliveryEvent deliveryEvent = new InternalDeliveryEvent(
                event.getMessageId(),
                event.getConversationId(),
                event.getSenderId(),
                event.getContent(),
                event.getTimestamp(),
                recipient.getId() // 2. Adicionar o ID do destinatário!
        );

        kafkaTemplate.send(TOPIC_OUT_INTERNAL, event.getConversationId().toString(), deliveryEvent);
        logger.info("Msg {}: Roteada para Internal (WS) para user {}", event.getMessageId(), recipient.getId());

        // ROTA 2: Entrega Externa (Telegram, etc.)
        // Procuramos no DB por "endereços" externos para este usuário
        List<UserIdentity> identities = userIdentityRepository.findByUserId(recipient.getId());

        for (UserIdentity identity : identities) {
            String platform = identity.getPlatform();
            String externalId = identity.getExternalId(); // ex: Chat ID do Telegram
            String topic;

            switch (platform) {
                case "telegram":
                    topic = TOPIC_OUT_TELEGRAM;
                    break;
                // case "whatsapp":
                //     topic = "outgoing_whatsapp";
                //     break;
                default:
                    logger.warn("Roteamento para plataforma '{}' desconhecida.", platform);
                    continue;
            }

            // Publicamos no tópico correto, usando o externalId (Chat ID) como CHAVE do Kafka.
            // O nosso worker do conector (próximo passo) vai ler esta chave.
            kafkaTemplate.send(topic, externalId, event);
            logger.info("Msg {}: Roteada para {} (ID Externo: {})", 
                    event.getMessageId(), platform, externalId);
        }
    }
     @KafkaListener(topics = TOPIC_INBOUND, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleMessage(InternalMessageEvent event) {
        logger.info("MENSAGEM RECEBIDA DO KAFKA! ID: {}", event.getMessageId());

        try {
            // --- ETAPA 1: PERSISTIR A MENSAGEM (Como antes) ---
            User sender = userRepository.findById(event.getSenderId())
                    .orElseThrow(() -> new EntityNotFoundException("Remetente não encontrado: " + event.getSenderId()));
            
            Conversation conversation = conversationRepository.findById(event.getConversationId())
                    .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada: " + event.getConversationId()));

            Message message = new Message(
                    event.getMessageId(),
                    conversation,
                    sender,
                    event.getContent()
            );
            Message savedMessage = messageRepository.save(message);
            logger.info("Mensagem {} salva no banco de dados.", savedMessage.getId());


            // 1. Encontrar todos os participantes da conversa
            List<ConversationParticipant> participants = 
                    participantRepository.findByConversationId(event.getConversationId());

            // 2. Para cada participante, rotear a mensagem
            for (ConversationParticipant participant : participants) {
                
                // 3. Não enviar a mensagem de volta para o remetente
                if (participant.getUser().getId().equals(event.getSenderId())) {
                    continue; 
                }

                User recipient = participant.getUser();
                routeMessageToRecipient(event, recipient, message);
            }

        } catch (Exception e) {
            logger.error("Erro ao processar a mensagem {}: {}", event.getMessageId(), e.getMessage());
            // TODO: Enviar para uma Dead-Letter Queue (DLQ)
        }
    }
}