package br.com.yourcompany.platform_core.Worker;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.com.yourcompany.platform_core.dominio.Conversation.Conversation;
import br.com.yourcompany.platform_core.dominio.message.Message;
import br.com.yourcompany.platform_core.dominio.user.User;
import br.com.yourcompany.platform_core.dto.InternalMessageEvent;
import br.com.yourcompany.platform_core.repository.ConversationRepository;
import br.com.yourcompany.platform_core.repository.MessageRepository;
import br.com.yourcompany.platform_core.repository.UserRepository;

@Service
public class MessageProcessingWorker {

    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingWorker.class);

    private static final String TOPIC_INBOUND = "inbound_messages";
    private static final String TOPIC_OUT_INTERNAL = "internal_delivery"; // Para Fase 1.6
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // Repositórios para salvar no banco
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private UserRepository userRepository;


    // Esta é a "mágica". Esta anotação transforma este método em um ouvinte
    @KafkaListener(topics = TOPIC_INBOUND, groupId = "${spring.kafka.consumer.group-id}")
    @Transactional // Garante que salvar no DB e republicar sejam atômicos
    public void handleMessage(InternalMessageEvent event) {
        logger.info("MENSAGEM RECEBIDA DO KAFKA! ID: {}", event.getMessageId());

        try {
            // 1. Converter o DTO do evento em uma Entidade de banco de dados
            //    Para isso, precisamos buscar as entidades reais (User e Conversation)
            
            User sender = (User) userRepository.findById(event.getSenderId())
                    .orElseThrow(() -> new EntityNotFoundException("Remetente não encontrado: " + event.getSenderId()));

            Conversation conversation = (Conversation) conversationRepository.findById(event.getConversationId())
                    .orElseThrow(() -> new EntityNotFoundException("Conversa não encontrada: " + event.getConversationId()));

            // 2. Criar a Entidade Message
            Message message = new Message(
                event.getMessageId(),
                conversation,
                sender,
                event.getContent()
            );

            // 3. Persistir no Banco de Dados
            messageRepository.save(message);
            logger.info("Mensagem {} salva no banco de dados.", message.getId());

            // 4. Republicar para entrega em tempo real (Tarefa 1.6)
            kafkaTemplate.send(TOPIC_OUT_INTERNAL, conversation.getId().toString(), event);
            logger.info("Mensagem {} republicada para entrega interna.", message.getId());

        } catch (Exception e) {
            logger.error("Erro ao processar a mensagem {}: {}", event.getMessageId(), e.getMessage());
            // TODO: Enviar para uma Dead-Letter Queue (DLQ)
        }
    }
}