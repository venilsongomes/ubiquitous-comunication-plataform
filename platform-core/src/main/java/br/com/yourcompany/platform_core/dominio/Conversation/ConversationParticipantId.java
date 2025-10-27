package br.com.yourcompany.platform_core.dominio.Conversation;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
@Embeddable
@EqualsAndHashCode // <--- GARANTA QUE ESTA ANOTAÇÃO ESTEJA AQUI

public class ConversationParticipantId implements Serializable {
   
    private UUID conversationId;
    private UUID userId;

    // Construtores, getters, setters, equals e hashCode
    public ConversationParticipantId() {
    }
    public ConversationParticipantId(UUID conversationId, UUID userId) {
        this.conversationId = conversationId;
        this.userId = userId;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public UUID getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConversationParticipantId)) return false;
        ConversationParticipantId that = (ConversationParticipantId) o;
        return conversationId.equals(that.conversationId) && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, userId);
    }

   
}
