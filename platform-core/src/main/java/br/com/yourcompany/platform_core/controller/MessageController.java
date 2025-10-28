package br.com.yourcompany.platform_core.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.yourcompany.platform_core.dto.InternalMessageEvent;
import br.com.yourcompany.platform_core.dto.SendMessageRequest;
import br.com.yourcompany.platform_core.service.MessageIngestionService;

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
            @RequestBody SendMessageRequest request) {

        // --- Simulação de Autenticação ---
        // Na arquitetura final, o ID do usuário viria de um token JWT (Spring Security)
        // Por enquanto, vamos "fingir" que o remetente é um dos usuários que já criamos.
        // TODO: Substituir este UUID 'hardcoded' pelo ID do usuário autenticado.
        UUID mockSenderId = UUID.fromString("b96c10b4-7b17-4973-a36b-83160166460d"); // <-- Pegue um ID válido do seu DB
        // -------------------------------------

        // 1. Criar o evento interno completo
        InternalMessageEvent event = new InternalMessageEvent(
                request.getMessageId(),
                conversationId,
                mockSenderId, // ID do remetente
                request.getContent(),
                Instant.now()
        );

        // 2. Submeter ao serviço de ingestão (que envia ao Kafka)
        ingestionService.submitMessage(event);

        // 3. Retornar 202 Accepted
        // Isso diz ao cliente: "Recebido! Obrigado, estou processando."
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}