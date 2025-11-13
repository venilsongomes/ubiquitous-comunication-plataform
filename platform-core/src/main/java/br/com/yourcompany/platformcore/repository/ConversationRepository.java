package br.com.yourcompany.platformcore.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.yourcompany.platformcore.domain.Conversation.Conversation;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    @Query("SELECT c FROM Conversation c " +
           "JOIN c.participants p1 " +
           "JOIN c.participants p2 " +
           "WHERE c.type = 'PRIVATE' " +
           "AND p1.user.id = :userId1 " +
           "AND p2.user.id = :userId2")
    Optional<Conversation> findPrivateConversationBetweenUsers(
            @Param("userId1") UUID userId1, 
            @Param("userId2") UUID userId2);
}