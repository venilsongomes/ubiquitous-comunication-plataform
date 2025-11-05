package br.com.yourcompany.platform_core.websocket;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.yourcompany.platform_core.dto.InternalMessageEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RealtimeMessageHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(RealtimeMessageHandler.class);

    // Mapeamento crucial: ConversationID -> Lista de Sessões (usuários)
    // Usamos CopyOnWriteArrayList para segurança em ambientes com múltiplas threads
    private final java.util.Map<UUID, List<WebSocketSession>> conversationSessions = 
            new java.util.concurrent.ConcurrentHashMap<>();

    //private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // TODO: Implementar autenticação e lógica de "inscrição"
        // Por enquanto, vamos apenas logar a nova conexão
        logger.info("Nova conexão WebSocket estabelecida: {}", session.getId());
        
        // --- SIMULAÇÃO DE INSCRIÇÃO ---
        // Em um app real, o cliente enviaria uma mensagem "subscribe"
        // {"type": "SUBSCRIBE", "conversationId": "uuid-da-conversa"}
        // Vamos simular que todos que conectam estão ouvindo uma conversa específica
        
        UUID mockConversationId = UUID.fromString("8009a91e-0f62-407a-850a-371fce8bd3a6"); // <-- Use o ID da conversa que você criou
        
        conversationSessions.computeIfAbsent(mockConversationId, k -> new CopyOnWriteArrayList<>())
                           .add(session);
        logger.info("Sessão {} inscrita na conversa {}", session.getId(), mockConversationId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Conexão WebSocket fechada: {}", session.getId());
        // Remover a sessão de todas as listas de conversas
        conversationSessions.values().forEach(list -> list.remove(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Lógica para receber mensagens do cliente (ex: pings, inscrição)
        // Por enquanto, vamos apenas logar
        logger.info("Mensagem recebida do cliente {}: {}", session.getId(), message.getPayload());
    }

    /**
     * Este é o método que nosso Consumidor Kafka (próximo passo) irá chamar!
     */
    public void sendMessageToConversation(InternalMessageEvent messageEvent) {
        UUID conversationId = messageEvent.getConversationId();
        List<WebSocketSession> sessions = conversationSessions.get(conversationId);

        if (sessions == null || sessions.isEmpty()) {
            logger.warn("Nenhum cliente WS ouvindo a conversa {}", conversationId);
            return;
        }

        try {
            // Converte nosso objeto de evento em uma string JSON
            String messagePayload = objectMapper.writeValueAsString(messageEvent);
            
            logger.info("Enviando mensagem {} para {} clientes na conversa {}", 
                    messageEvent.getMessageId(), sessions.size(), conversationId);

            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(messagePayload));
                }
            }
        } catch (IOException e) {
            logger.error("Erro ao serializar ou enviar mensagem WS: {}", e.getMessage());
        }
    }
}
