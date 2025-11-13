package br.com.yourcompany.platformcore.repository;
import br.com.yourcompany.platformcore.domain.message.Message;
import br.com.yourcompany.platformcore.domain.message.MessageRecipientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRecipientStatusRepository extends JpaRepository<MessageRecipientStatus, Long> {
    Optional<MessageRecipientStatus> findByMessageAndRecipientId(Message message, UUID recipientId);

    // VAMOS ATUALIZAR O ANTERIOR TAMBÉM (PARA BUSCAR PELOS IDs)
    Optional<MessageRecipientStatus> findByMessageIdAndRecipientId(UUID messageId, UUID recipientId);
}