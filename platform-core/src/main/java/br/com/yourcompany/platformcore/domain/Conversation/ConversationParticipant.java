package br.com.yourcompany.platformcore.domain.conversation;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import br.com.yourcompany.platformcore.domain.user.User;

import java.time.Instant;

@Entity
@Table(name = "conversation_participants")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class ConversationParticipant {

    @EmbeddedId
    private ConversationParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("conversationId") // Mapeia a parte 'conversationId' da nossa chave composta
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Mapeia a parte 'userId' da nossa chave composta
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Instant joinedAt;

    public ConversationParticipant(Conversation conversation, User user) {
        this.conversation = conversation;
        this.user = user;
        this.id = new ConversationParticipantId(conversation.getId(), user.getId());
    }
}