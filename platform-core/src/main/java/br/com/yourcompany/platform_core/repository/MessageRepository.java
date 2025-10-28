package br.com.yourcompany.platform_core.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yourcompany.platform_core.dominio.message.Message;;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    
}
