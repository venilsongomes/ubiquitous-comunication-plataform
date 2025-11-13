package br.com.yourcompany.platformcore.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.yourcompany.platformcore.domain.message.Message;;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    
}
