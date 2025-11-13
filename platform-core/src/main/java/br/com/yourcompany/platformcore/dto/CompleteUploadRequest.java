package br.com.yourcompany.platformcore.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CompleteUploadRequest {
    private List<CompletedPartDTO> parts;
}