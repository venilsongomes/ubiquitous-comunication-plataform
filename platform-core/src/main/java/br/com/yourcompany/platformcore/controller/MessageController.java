package br.com.yourcompany.platformcore.controller;
import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.dto.InternalMessageEvent;
import br.com.yourcompany.platformcore.dto.MessageResponse;
import br.com.yourcompany.platformcore.dto.UniversalMessageRequest; // <-- NOVO DTO
import br.com.yourcompany.platformcore.service.MessageIngestionService;
import br.com.yourcompany.platformcore.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1") // <-- MUDANÇA: URL base simplificada para /api/v1
public class MessageController {

    @Autowired private MessageIngestionService ingestionService;
    @Autowired private MessageService messageService;

    // --- NOVO ENDPOINT DE ENVIO ---
    @PostMapping("/messages") // <-- MUDANÇA: URL simplificada para /api/v1/messages
    public ResponseEntity<Void> sendMessage(
            @RequestBody UniversalMessageRequest request, // <-- NOVO DTO
            @AuthenticationPrincipal User user) {

        // 1. O Controller faz a validação e mapeia para o evento interno
        // Nota: O 'conversationId' é obrigatório no nosso pipeline,
        // mas no design universal, ele pode ser inferido do campo 'to'.
        
        // 2. Criar o evento interno (InternalMessageEvent)
        InternalMessageEvent event = new InternalMessageEvent(
                request.getMessageId(),
                request.getConversationId(), // Usamos o ID que veio no corpo da requisição
                user.getId(), // O ID do remetente é o ID REAL do JWT
                request.getPayload().getText(), // Extrair o texto do payload aninhado
                Instant.now()
        );

        ingestionService.submitMessage(event);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    
    // Endpoint de Listagem (URL mais específica)
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<MessageResponse>> listMessages(
            @PathVariable UUID conversationId,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable) {

        // Nota: Você pode adicionar aqui a verificação de que o 'user' é um participante da conversa
        
        Page<MessageResponse> response = messageService.listMessages(conversationId, pageable);
        return ResponseEntity.ok(response);
    }
}