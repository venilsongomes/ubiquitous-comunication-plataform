package br.com.yourcompany.platformcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompletedPartDTO {
   @JsonProperty("partNumber")
    private int partNumber;

    @JsonProperty("eTag")
    private String eTag; // O "recibo" que o MinIO devolveu para o cliente
}