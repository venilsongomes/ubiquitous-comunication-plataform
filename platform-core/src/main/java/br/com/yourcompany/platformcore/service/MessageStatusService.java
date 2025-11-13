package br.com.yourcompany.platformcore.service;

import br.com.yourcompany.platformcore.domain.message.MessageRecipientStatus;
import br.com.yourcompany.platformcore.dto.StatusUpdateEvent;
import br.com.yourcompany.platformcore.repository.MessageRecipientStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class MessageStatusService {

    private static final Logger logger = LoggerFactory.getLogger(MessageStatusService.class);

    private static final String TOPIC_STATUS_UPDATES = "status_updates";

    @Autowired
    private MessageRecipientStatusRepository statusRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public void markMessageAsRead(UUID messageId, UUID readerId) {
        logger.info("Tentando marcar msg {} como LIDA pelo user {}", messageId, readerId);

        try {
            // 1. Encontrar o status "DELIVERED" (ou "SENT") no banco
            MessageRecipientStatus status = statusRepository
                    .findByMessageIdAndRecipientId(messageId, readerId)
                    .orElse(null);

            if (status == null) {
                logger.warn("Status não encontrado para msg {} e user {}. Ignorando READ.", 
                        messageId, readerId);
                return;
            }

            // 2. Idempotência: Se já foi lido, não fazer nada
            if ("READ".equals(status.getStatus())) {
                return; 
            }

            // 3. Atualizar para "READ"
            status.setStatus("READ");
            status.setReadAt(Instant.now());
            statusRepository.save(status);

            logger.info("Status da Msg {} atualizado para READ para user {}", 
                    messageId, readerId);

            // 4. Publicar o evento para notificar o REMETENTE
            StatusUpdateEvent updateEvent = new StatusUpdateEvent(
                    messageId, readerId, "READ"
            );
            kafkaTemplate.send(TOPIC_STATUS_UPDATES, messageId.toString(), updateEvent);

        } catch (Exception e) {
            logger.error("Falha ao atualizar status para READ: {}", e.getMessage(), e);
        }
    }
}