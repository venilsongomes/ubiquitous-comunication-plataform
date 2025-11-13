package br.com.yourcompany.platformcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedPartDTO {
    private int partNumber;
    private String eTag; // O "recibo" que o MinIO devolveu para o cliente
}