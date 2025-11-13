package br.com.yourcompany.platformcore.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SendMessageRequest {
    
    // O ID de idempotência gerado pelo cliente
    private UUID messageId; 
    
    private String content;

    // TODO: Adicionar campos para anexos, etc., no futuro.
}