package br.com.yourcompany.platformcore.controller;
import br.com.yourcompany.platformcore.dto.InitiateUploadRequest;
import br.com.yourcompany.platformcore.dto.InitiateUploadResponse;
import br.com.yourcompany.platformcore.service.AttachmentUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.dto.CompleteUploadRequest;
import org.springframework.http.HttpHeaders; // <-- Novo
import org.springframework.http.HttpStatus;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/uploads")
public class UploadController {

    @Autowired
    private AttachmentUploadService uploadService;

    @PostMapping("/initiate")
    public ResponseEntity<InitiateUploadResponse> initiateUpload(
            @RequestBody InitiateUploadRequest request, @AuthenticationPrincipal User user) {

      
        InitiateUploadResponse response = uploadService.initiateUpload(request, user.getId()); // <-- 3. USAR O ID REAL
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{attachmentId}/complete")
    public ResponseEntity<Void> completeUpload( @PathVariable UUID attachmentId, @RequestBody CompleteUploadRequest request) {

        uploadService.completeUpload(attachmentId, request);
        
        return ResponseEntity.ok().build();
    }
    // ... (initiateUpload e completeUpload continuam aqui) ...

    @GetMapping("/{attachmentId}/download")
    public ResponseEntity<Void> downloadFile(@PathVariable UUID attachmentId, @AuthenticationPrincipal User user) {

      String presignedUrl = uploadService.getDownloadUrl(attachmentId, user.getId());

        // 2. Retornar um HTTP 302 Redirect
        return ResponseEntity.status(HttpStatus.FOUND) // 302 Found
                .header(HttpHeaders.LOCATION, presignedUrl) // O cabeçalho 'Location' diz ao navegador para onde ir
                .build();
    }
}

