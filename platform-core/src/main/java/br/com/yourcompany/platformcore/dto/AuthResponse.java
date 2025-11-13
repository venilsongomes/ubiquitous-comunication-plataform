package br.com.yourcompany.platformcore.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter @AllArgsConstructor
public class AuthResponse {
    private String token;
}