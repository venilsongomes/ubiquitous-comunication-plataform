package br.com.yourcompany.platformcore.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.yourcompany.platformcore.domain.user.User;
//import org.telegram.telegrambots.meta.api.objects.User;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.dto.SendMessageRequest;
import br.com.yourcompany.platformcore.service.MessageIngestionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/conversations/{conversationId}/messages")
public class MessageController {

    @Autowired
    private MessageIngestionService ingestionService;

    @PostMapping
    public ResponseEntity<Void> sendMessage(
            @PathVariable UUID conversationId,
            @RequestBody SendMessageRequest request, @AuthenticationPrincipal User user) {

        // 1. Criar o evento interno completo
        InternalMessageEvent event = new InternalMessageEvent(
                request.getMessageId(),
                conversationId,
                user.getId(),
                request.getContent(),
                Instant.now()
        );

        
        ingestionService.submitMessage(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}