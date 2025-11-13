package br.com.yourcompany.platformcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateEvent {
    private UUID messageId;
    private UUID recipientId; // Quem entregou/leu
    private String newStatus;   // "DELIVERED" ou "READ"
}