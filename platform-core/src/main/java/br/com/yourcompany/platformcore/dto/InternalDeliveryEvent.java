package br.com.yourcompany.platformcore.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

/**
 * Evento específico para a fila de entrega.
 * Ele diz "entregue ESTA mensagem PARA ESTE destinatário".
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternalDeliveryEvent {
    // Info da mensagem
    private UUID messageId;
    private UUID conversationId;
    private UUID senderId;
    private String content;
    private Instant timestamp;
    
    // A informação crucial que faltava:
    private UUID recipientId; 
}