package br.com.yourcompany.platform_core.dto;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CreateConversationRequest {

    private String type; // "PRIVATE" ou "GROUP"
    private String groupName; // Opcional, só para grupos
    private List<UUID> participantIds; // Lista de IDs dos usuários na conversa

    public String getType() {
        return type;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<UUID> getParticipantIds() {
        return participantIds;
    }

}
