package br.com.yourcompany.platform_core.dto;

import java.util.UUID;

import br.com.yourcompany.platform_core.dominio.Conversation.Conversation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConversationResponse {
    private UUID id;
    private String type;
    private String groupName;

    public static ConversationResponse fromEntity(Conversation conversation) {
        return new ConversationResponse(
            conversation.getId(),
            conversation.getType().name(), // Converte o Enum para String
            conversation.getGroupName()
        );
    }
    
}
