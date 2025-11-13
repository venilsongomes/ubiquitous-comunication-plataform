package br.com.yourcompany.platformcore.domain.user;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
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
import org.springframework.security.core.userdetails.UserDetails; 

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username;

    private String displayName;

    @CreationTimestamp
    private Instant createdAt;

    private String password;


    public User(String username, String displayName, String password) {
        this.username = username;
        this.displayName = displayName;
        this.password = password;

}
public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por enquanto, não usamos "Roles" (ex: ADMIN, USER)
        return List.of(); 
    }
    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }
}
