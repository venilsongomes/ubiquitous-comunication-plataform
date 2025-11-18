package br.com.yourcompany.platformcore.dto;

import br.com.yourcompany.platformcore.domain.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private UUID id;
    private String content;
    private UUID senderId;
    private String senderName;
    private Instant sentAt;

    // Converte Entidade -> DTO
    public static MessageResponse fromEntity(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getSender().getId(),
                message.getSender().getDisplayName(), // Ou username
                message.getSentAt()
                
        );
    }
}