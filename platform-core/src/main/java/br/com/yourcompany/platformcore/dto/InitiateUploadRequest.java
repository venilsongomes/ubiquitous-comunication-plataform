package br.com.yourcompany.platformcore.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InitiateUploadRequest {
    private String filename;
    private String mimeType;
    private long fileSize;
}