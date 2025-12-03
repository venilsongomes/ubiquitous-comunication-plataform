package br.com.yourcompany.platformcore.domain.message;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

import br.com.yourcompany.platformcore.domain.conversation.Conversation;
import br.com.yourcompany.platformcore.domain.user.User;
import jakarta.persistence.Column;
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
@EqualsAndHashCode
public class Message {

    @Id
    private UUID id; // Será gerado pelo cliente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private Instant createdAt;
    
     @CreationTimestamp
    private Instant sentAt;


    public Message(UUID id, Conversation conversation, User sender, String content, Instant sentAt) {
        this.id = id;
        this.conversation = conversation;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }


    public Message(UUID id, Conversation conversation, org.apache.catalina.User sender2, String content, Instant createdAt) {
        this.id = id;
        this.conversation = conversation;
        this.sender = (User) sender2;
        this.content = content;
        this.createdAt = createdAt;
    }

    
}
