package br.com.yourcompany.platformcore.controller;

import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.service.MessageStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/status")
public class MessageStatusController {

    @Autowired
    private MessageStatusService statusService;

    @PostMapping("/read/{messageId}")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID messageId, @AuthenticationPrincipal User user) {

        statusService.markMessageAsRead(messageId, user.getId());
        
        // Retorna 200 OK (não precisa ser 202, pois a atualização é rápida)
        return ResponseEntity.ok().build();
    }
}