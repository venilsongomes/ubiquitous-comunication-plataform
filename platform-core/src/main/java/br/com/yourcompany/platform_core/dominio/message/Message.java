package br.com.yourcompany.platform_core.dominio.message;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.ManyToAny;

import br.com.yourcompany.platform_core.dominio.Conversation.Conversation;
import br.com.yourcompany.platform_core.dominio.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Message {

    @Id
    private UUID id; // Será gerado pelo cliente

    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversationId;
    
    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    

    @CreationTimestamp
    private String content;


    public Message(Conversation conversationId, User sender, String content) {
        this.conversationId = conversationId;
        this.sender = sender;
        this.content = content;
    }

    
}
