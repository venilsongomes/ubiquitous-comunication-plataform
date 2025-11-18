package br.com.yourcompany.platformcore.service;

import br.com.yourcompany.platformcore.domain.message.Message;
import br.com.yourcompany.platformcore.dto.MessageResponse;
import br.com.yourcompany.platformcore.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public Page<MessageResponse> listMessages(UUID conversationId, Pageable pageable) {
        // 1. Busca paginada no banco
        Page<Message> messages = messageRepository.findByConversationIdOrderBySentAtDesc(conversationId, pageable);

        // 2. Converte cada Entidade para DTO
        return messages.map(MessageResponse::fromEntity);
    }
}