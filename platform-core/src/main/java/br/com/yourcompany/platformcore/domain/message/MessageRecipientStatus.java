package br.com.yourcompany.platformcore.domain.message;
import br.com.yourcompany.platformcore.domain.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "message_recipient_status")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"message", "recipient"}) // Chave primária composta
public class MessageRecipientStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Chave primária simples

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient; // O destinatário

    @Column(nullable = false)
    private String status; // Ex: "SENT", "DELIVERED", "READ"

    private Instant deliveredAt;
    private Instant readAt;

    public MessageRecipientStatus(Message message, User recipient, String status) {
        this.message = message;
        this.recipient = recipient;
        this.status = status;
    }
}