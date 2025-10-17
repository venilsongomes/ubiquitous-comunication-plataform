package br.com.yourcompany.platform_core.dominio.user;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String displayName;

    
    @CreationTimestamp
    private Instant createdAt;


    public User(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

}
