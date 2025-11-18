package br.com.yourcompany.platformcore.repository;
import java.util.UUID;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import br.com.yourcompany.platformcore.domain.message.Message;;


public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByConversationIdOrderBySentAtDesc(UUID conversationId, org.springframework.data.domain.Pageable pageable);
    
}
