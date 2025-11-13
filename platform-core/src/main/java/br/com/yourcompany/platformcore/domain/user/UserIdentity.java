package br.com.yourcompany.platformcore.domain.user;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_identities", uniqueConstraints = {
    // Garante que um ID externo (ex: um número de WhatsApp) só possa ser
    // usado por um usuário.
    @UniqueConstraint(columnNames = {"platform", "external_id"}),
    // Garante que um usuário interno só possa ter uma identidade por plataforma.
    @UniqueConstraint(columnNames = {"user_id", "platform"})
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String platform; // Ex: "telegram", "whatsapp", "instagram"

    @Column(nullable = false)
    private String externalId; // Ex: "123456789" (ID do chat do Telegram)

    public UserIdentity(User user, String platform, String externalId) {
        this.user = user;
        this.platform = platform;
        this.externalId = externalId;
    }
}