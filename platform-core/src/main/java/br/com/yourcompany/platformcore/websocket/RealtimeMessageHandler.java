package br.com.yourcompany.platformcore.websocket;

import br.com.yourcompany.platformcore.dto.InternalDeliveryEvent;
import br.com.yourcompany.platformcore.dto.StatusUpdateEvent; // <-- Novo
import br.com.yourcompany.platformcore.domain.message.MessageRecipientStatus; // <-- Novo
import br.com.yourcompany.platformcore.repository.MessageRecipientStatusRepository; // <-- Novo
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate; // <-- Novo
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant; // <-- Novo
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RealtimeMessageHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessageHandler.class);

    // Novo Tópico para notificar o remetente
    private static final String TOPIC_STATUS_UPDATES = "status_updates";

    // Mapeamento: UserID -> Lista de Sessões (ex: abas do navegador)
    private final java.util.Map<UUID, List<WebSocketSession>> userSessions = 
            new java.util.concurrent.ConcurrentHashMap<>();

    @Autowired private ObjectMapper objectMapper;
    @Autowired private KafkaTemplate<String, Object> kafkaTemplate; // <-- Novo
    @Autowired private MessageRecipientStatusRepository statusRepository; // <-- Novo


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Nova conexão WebSocket estabelecida: {}", session.getId());
        
        // --- SIMULAÇÃO DE AUTENTICAÇÃO E INSCRIÇÃO ---
        // Em um app real, o cliente enviaria um token JWT.
        // Vamos simular que a sessão pertence a um usuário específico.
        
        // Use o ID do usuário que é o DESTINATÁRIO das suas mensagens
        // (ex: o usuário "gemini" ou "bot_interno" que você criou)
        UUID mockUserId = UUID.fromString("15aa1478-0e22-4458-8dc5-5e5b9dd588e8"); // <-- MUDE ISSO
        
        userSessions.computeIfAbsent(mockUserId, k -> new CopyOnWriteArrayList<>())
                           .add(session);
        logger.info("Sessão {} autenticada como User {}", session.getId(), mockUserId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Conexão WebSocket fechada: {}", session.getId());
        // Remover a sessão de todas as listas de usuários
        userSessions.values().forEach(list -> list.remove(session));
    }

    // ... (handleTextMessage fica igual) ...

    /**
     * Renomeado e atualizado para a Tarefa 3.4
     * Recebe um evento de entrega e o envia para o usuário correto.
     */
    public void sendMessageToRecipient(InternalDeliveryEvent event) {
        UUID recipientId = event.getRecipientId();
        List<WebSocketSession> sessions = userSessions.get(recipientId);

        if (sessions == null || sessions.isEmpty()) {
            logger.warn("Nenhum cliente WS online para o user {}", recipientId);
            // Mesmo se não for entregue, a mensagem já está salva no DB como "SENT".
            return;
        }

        boolean delivered = false;
        try {
            String messagePayload = objectMapper.writeValueAsString(event);
            
            logger.info("Enviando mensagem {} para {} sessões do user {}", 
                    event.getMessageId(), sessions.size(), recipientId);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(messagePayload));
                    delivered = true;
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao serializar ou enviar mensagem WS: {}", e.getMessage());
        }

        // --- ATUALIZAÇÃO DE STATUS (A LÓGICA DA TAREFA 3.4) ---
        if (delivered) {
            updateStatusToDelivered(event.getMessageId(), recipientId);
        }
    }

    private void updateStatusToDelivered(UUID messageId, UUID recipientId) {
        try {
            // 1. Encontrar o status "SENT" no banco
            MessageRecipientStatus status = statusRepository
                    .findByMessageIdAndRecipientId(messageId, recipientId)
                    .orElse(null); // Ignora se não encontrar (embora não devesse)

            if (status != null && !"DELIVERED".equals(status.getStatus())) {
                // 2. Atualizar para "DELIVERED"
                status.setStatus("DELIVERED");
                status.setDeliveredAt(Instant.now());
                statusRepository.save(status);
                
                logger.info("Status da Msg {} atualizado para DELIVERED para user {}", 
                        messageId, recipientId);

                // 3. Publicar o evento para notificar o REMETENTE
                StatusUpdateEvent updateEvent = new StatusUpdateEvent(
                        messageId, recipientId, "DELIVERED"
                );
                kafkaTemplate.send(TOPIC_STATUS_UPDATES, messageId.toString(), updateEvent);
            }
        } catch (Exception e) {
            logger.error("Falha ao atualizar status para DELIVERED: {}", e.getMessage());
        }
    }
}