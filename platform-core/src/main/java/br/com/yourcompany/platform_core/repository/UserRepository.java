package br.com.yourcompany.platform_core.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.yourcompany.platform_core.dominio.user.User;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    // Método mágico do Spring Data: 
    // Ele entende que queremos buscar um usuário pelo seu campo 'username'
    Optional<User> findByUsername(String username);
}
