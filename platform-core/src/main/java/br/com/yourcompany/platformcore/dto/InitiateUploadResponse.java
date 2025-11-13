package br.com.yourcompany.platformcore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InitiateUploadResponse {
    // O ID do nosso anexo no banco de dados
    private UUID attachmentId;
    // O ID do upload no MinIO
    private String uploadId;
    // A lista de URLs para onde o cliente deve enviar os "chunks"
    private List<String> presignedUrls;
}