package br.com.yourcompany.platform_core.dominio.Conversation;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List; // <-- ADICIONAR
import jakarta.persistence.OneToMany; // <-- ADICIONAR
import jakarta.persistence.CascadeType; // <-- ADICIONAR

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
     
    @Enumerated( EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type;

    private String groupName;

    @CreationTimestamp
    private Instant createdAt;
    
    @OneToMany(
        mappedBy = "conversation", // "conversation" é o nome do campo na classe ConversationParticipant
        cascade = CascadeType.ALL, // Garante que se deletarmos uma conversa, os participantes saem juntos
        orphanRemoval = true
    )
    private List<ConversationParticipant> participants;

    public Conversation(ConversationType type, String groupName) {
        this.type = type;
        this.groupName = groupName;
    }




    
}
