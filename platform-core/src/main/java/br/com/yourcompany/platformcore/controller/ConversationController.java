package br.com.yourcompany.platformcore.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.yourcompany.platformcore.dto.ConversationResponse;
import br.com.yourcompany.platformcore.dto.CreateConversationRequest;
import br.com.yourcompany.platformcore.service.ConversationService;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @RequestBody CreateConversationRequest request) {
        
        ConversationResponse response = conversationService.createConversation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}