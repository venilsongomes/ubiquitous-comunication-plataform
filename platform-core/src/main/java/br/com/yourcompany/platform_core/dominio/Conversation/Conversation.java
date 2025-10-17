package br.com.yourcompany.platform_core.dominio.Conversation;

import java.time.Instant;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
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

    public Conversation(ConversationType type, String groupName) {
        this.type = type;
        this.groupName = groupName;
    }




    
}
