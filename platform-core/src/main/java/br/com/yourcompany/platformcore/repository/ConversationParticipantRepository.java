package br.com.yourcompany.platformcore.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.yourcompany.platformcore.domain.conversation.ConversationParticipant;
import br.com.yourcompany.platformcore.domain.conversation.ConversationParticipantId;

@Repository
public interface ConversationParticipantRepository 
    extends JpaRepository<ConversationParticipant, ConversationParticipantId> {

    List<ConversationParticipant> findByConversationId(UUID conversationId);
}
