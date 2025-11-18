package br.com.yourcompany.platformcore.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UniversalMessageRequest {
    private UUID messageId;
    private UUID conversationId; // O ID do chat
    private String from;         // UUID ou username do remetente (usaremos o contexto de segurança)
    private List<String> to;     // Lista de destinatários (usuários/grupos)
    private List<String> channels; // Canais de destino desejados (ex: ["whatsapp", "internal"])
    private UniversalPayload payload;
    private UniversalMetadata metadata;
}