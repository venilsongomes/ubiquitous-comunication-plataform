package br.com.yourcompany.platformcore.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import br.com.yourcompany.platformcore.domain.Conversation.Conversation;
import br.com.yourcompany.platformcore.domain.Conversation.ConversationParticipant;
import br.com.yourcompany.platformcore.domain.Conversation.ConversationType;
import br.com.yourcompany.platformcore.domain.message.Message;
import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.domain.user.UserIdentity;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.repository.ConversationParticipantRepository;
import br.com.yourcompany.platformcore.repository.ConversationRepository;
import br.com.yourcompany.platformcore.repository.UserIdentityRepository;
import br.com.yourcompany.platformcore.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExternalIngestionWorker {

    private static final Logger logger = LoggerFactory.getLogger(ExternalIngestionWorker.class);

    private static final String TOPIC_IN_TELEGRAM = "incoming_telegram_updates";
    private static final String TOPIC_OUT_INBOUND = "inbound_messages";

    @Autowired private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired private UserIdentityRepository userIdentityRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ConversationRepository conversationRepository;
    @Autowired private ConversationParticipantRepository participantRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    private static final UUID TELEGRAM_BOT_USER_ID = UUID.fromString("83fd8be3-3e4e-4844-89ca-d49ab6c70539"); 
    // ^^^ MUDE ISSO para um UUID de usuário seu (pode ser o "gemini" que criamos)
    // --- Fim da Simulação ---


    //@KafkaListener(topics = TOPIC_IN_TELEGRAM, groupId = "${spring.kafka.consumer.group-id}-ingestion")
    @KafkaListener(
        topics = TOPIC_IN_TELEGRAM, 
        groupId = "${spring.kafka.consumer.group-id}-ingestion",
        containerFactory = "telegramListenerFactory" // <-- USA A NOVA FÁBRICA
    )
    @Transactional
    public void handleExternalMessage(Update update) {
        //if (!(payload instanceof Update)) {
           // logger.warn("Mensagem consumida não é um 'Update' do Telegram. Ignorando.");
           // return;
       // }
       if (update == null) {
            logger.warn("Mensagem consumida é nula. Ignorando.");
            return;
        }
        
        //Update update = (Update) payload;
        
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return; // Ignora updates que não são mensagens de texto
        }

        org.telegram.telegrambots.meta.api.objects.Message telegramMessage = update.getMessage();
        String externalSenderId = telegramMessage.getFrom().getId().toString();
        String text = telegramMessage.getText();
        
        logger.info("WORKER INGESTION: Processando msg de 'telegram:{}'", externalSenderId);

        try {
            // 1. Encontrar o usuário interno (remetente)
            User sender = findInternalUser("telegram", externalSenderId);

            // 2. Encontrar o "usuário" do nosso bot (destinatário)
            User botUser = userRepository.findById(TELEGRAM_BOT_USER_ID)
                    .orElseThrow(() -> new RuntimeException("Usuário Bot não encontrado!"));

            // 3. Encontrar (ou criar) a conversa 1:1 entre eles
            Conversation conversation = findOrCreatePrivateConversation(sender, botUser);
            
            // 4. Gerar um novo ID para nossa mensagem interna
            UUID messageId = UUID.randomUUID();

            // 5. Criar o Evento Interno
            InternalMessageEvent event = new InternalMessageEvent(
                    messageId,
                    conversation.getId(),
                    sender.getId(), // O remetente é o usuário do Telegram
                    text,
                    Instant.now()
            );

            // 6. Publicar no nosso pipeline principal
            kafkaTemplate.send(TOPIC_OUT_INBOUND, conversation.getId().toString(), event);
            logger.info("WORKER INGESTION: Mensagem externa de 'telegram:{}' roteada para 'inbound_messages'", 
                    externalSenderId);

        } catch (Exception e) {
            logger.error("Falha ao processar mensagem de 'telegram:{}': {}", externalSenderId, e.getMessage());
            // TODO: Enviar para Dead-Letter Queue
        }
    }

    private User findInternalUser(String platform, String externalId) {
        // Encontra a identidade (ex: telegram:12345)
        UserIdentity identity = userIdentityRepository.findByPlatformAndExternalId(platform, externalId)
                // Se não existir, CRIAMOS um novo usuário para esta pessoa!
                .orElseGet(() -> registerNewExternalUser(platform, externalId));
        return identity.getUser();
    }

    private UserIdentity registerNewExternalUser(String platform, String externalId) {
        logger.info("Novo usuário! Registrando {} com ID externo {}", platform, externalId);
        // Cria um novo usuário "genérico"
       // Código corrigido:
        String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString()); // <-- Crie uma senha aleatória e criptografada
        User newUser = new User("user_telegram_" + externalId, "Usuário do " + platform, randomPassword);
        User savedUser = userRepository.save(newUser);
        
        // Cria a "ponte" (identidade)
        UserIdentity newIdentity = new UserIdentity(savedUser, platform, externalId);
        return userIdentityRepository.save(newIdentity);
    }

    private Conversation findOrCreatePrivateConversation(User user1, User user2) {
        // Tenta encontrar uma conversa 1:1 existente
        return conversationRepository.findPrivateConversationBetweenUsers(user1.getId(), user2.getId())
                // Se não existir, cria uma nova
                .orElseGet(() -> {
                    logger.info("Criando nova conversa 1:1 para user {} e {}", user1.getId(), user2.getId());
                    Conversation newConversation = new Conversation(ConversationType.PRIVATE, null);
                    Conversation savedConversation = conversationRepository.save(newConversation);
                    
                    // Adiciona os dois participantes
                    participantRepository.save(new ConversationParticipant(savedConversation, user1));
                    participantRepository.save(new ConversationParticipant(savedConversation, user2));
                    
                    return savedConversation;
                });
    }
}