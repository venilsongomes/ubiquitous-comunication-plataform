package br.com.yourcompany.platformcore.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
public class CompleteUploadRequest {
    @JsonProperty("parts")
    private List<CompletedPartDTO> parts;
}