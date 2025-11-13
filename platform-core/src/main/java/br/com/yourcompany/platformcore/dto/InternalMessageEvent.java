package br.com.yourcompany.platformcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

/**
 * Este é o objeto que será serializado como JSON e colocado no tópico do Kafka.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternalMessageEvent {

    private UUID messageId;
    private UUID conversationId;
    private UUID senderId; // TODO: Precisamos pegar isso do usuário autenticado
    private String content;
    private Instant timestamp;
}