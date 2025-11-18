package br.com.yourcompany.platformcore.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UniversalPayload {
    private String type; // Ex: "text", "image"
    private String text; // Conteúdo principal da mensagem
}