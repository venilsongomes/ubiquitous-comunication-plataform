package br.com.yourcompany.platform_core.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.yourcompany.platform_core.dominio.Conversation.ConversationParticipant;
import br.com.yourcompany.platform_core.dominio.Conversation.ConversationParticipantId;

@Repository
public interface ConversationParticipantRepository 
    extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    List<ConversationParticipant> findByConversationId(UUID conversationId);
}
