package br.com.yourcompany.platformcore.repository;

import br.com.yourcompany.platformcore.domain.message.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, UUID> {
}