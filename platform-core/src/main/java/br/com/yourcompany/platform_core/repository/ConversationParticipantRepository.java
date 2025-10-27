package br.com.yourcompany.platform_core.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.yourcompany.platform_core.dominio.Conversation.ConversationParticipant;
import br.com.yourcompany.platform_core.dominio.Conversation.ConversationParticipantId;

@Repository
public interface ConversationParticipantRepository 
    extends JpaRepository<ConversationParticipant, ConversationParticipantId> {
}
