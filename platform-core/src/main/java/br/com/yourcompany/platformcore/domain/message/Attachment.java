package br.com.yourcompany.platformcore.domain.message;

import br.com.yourcompany.platformcore.domain.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader; // Quem está enviando

    // A mensagem a qual este anexo pertence.
    // Será NULO até que o upload seja concluído e a mensagem seja enviada.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(nullable = false)
    private String filename;

    private String mimeType;

    private long fileSize;

    @Column(nullable = false)
    private String status; // Ex: "PENDING", "COMPLETED", "FAILED"

    // O ID do upload multipart no S3/MinIO
    private String externalUploadId;
    
    // A URL final do arquivo DEPOIS que o upload for concluído
    private String fileUrl;

    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "object_key")
    private String objectKey;

    public Attachment(User uploader, String filename, String mimeType, long fileSize, String status, String externalUploadId, String objectKey) {
         this.objectKey = objectKey;
        this.uploader = uploader;
        this.filename = filename;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.status = status;
        this.externalUploadId = externalUploadId;
    }
}