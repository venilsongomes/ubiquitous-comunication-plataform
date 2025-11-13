package br.com.yourcompany.platformcore.service;

import br.com.yourcompany.platformcore.domain.message.Attachment;
import br.com.yourcompany.platformcore.domain.user.User;
import br.com.yourcompany.platformcore.dto.CompleteUploadRequest;
import br.com.yourcompany.platformcore.dto.InitiateUploadRequest;
import br.com.yourcompany.platformcore.dto.InitiateUploadResponse;
import br.com.yourcompany.platformcore.repository.AttachmentRepository;
import br.com.yourcompany.platformcore.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AttachmentUploadService {

    private static final Logger logger = LoggerFactory.getLogger(AttachmentUploadService.class);
    private static final long PART_SIZE = 5 * 1024 * 1024; // 5 MB

    @Autowired private S3Client s3Client;        // Novo cliente AWS
    @Autowired private S3Presigner s3Presigner;  // Novo presigner AWS
    @Autowired private AttachmentRepository attachmentRepository;
    @Autowired private UserRepository userRepository;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Transactional
    public InitiateUploadResponse initiateUpload(InitiateUploadRequest request, UUID uploaderId) {
        try {
            User uploader = userRepository.findById(uploaderId)
                    .orElseThrow(() -> new EntityNotFoundException("Uploader não encontrado"));

            int partCount = (int) Math.ceil((double) request.getFileSize() / PART_SIZE);
            if (partCount == 0) partCount = 1;

            String objectName = UUID.randomUUID() + "/" + request.getFilename();

            // 1. AWS SDK: Criar Multipart Upload
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType(request.getMimeType()) // Bom definir o Content-Type aqui
                    .build();

            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            String uploadId = createResponse.uploadId();

            logger.info("Upload Multipart AWS iniciado. Upload ID: {}", uploadId);

            // 2. Salvar no DB (igual ao anterior)
            Attachment attachment = new Attachment(
                    uploader, request.getFilename(), request.getMimeType(),
                    request.getFileSize(), "PENDING", uploadId, objectName
            );
            Attachment savedAttachment = attachmentRepository.save(attachment);

            // 3. Gerar URLs usando o S3Presigner
            List<String> presignedUrls = IntStream.rangeClosed(1, partCount)
                    .mapToObj(partNumber -> getPresignedUrlForPart(bucketName, objectName, uploadId, partNumber))
                    .collect(Collectors.toList());

            InitiateUploadResponse response = new InitiateUploadResponse();
            response.setAttachmentId(savedAttachment.getId());
            response.setUploadId(uploadId);
            response.setPresignedUrls(presignedUrls);

            return response;

        } catch (Exception e) {
            logger.error("Erro AWS S3 ao iniciar upload: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao iniciar upload S3.", e);
        }
    }

    private String getPresignedUrlForPart(String bucket, String key, String uploadId, int partNumber) {
        // AWS SDK v2: Usamos UploadPartPresignRequest para partes específicas
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build();

        UploadPartPresignRequest presignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .uploadPartRequest(uploadPartRequest)
                .build();

        PresignedUploadPartRequest presigned = s3Presigner.presignUploadPart(presignRequest);
        return presigned.url().toString();
    }


    @Transactional
    public void completeUpload(UUID attachmentId, CompleteUploadRequest request) {
        try {
            // 1. Buscar o anexo no banco e validar
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Anexo não encontrado"));

            if (!"PENDING".equals(attachment.getStatus())) {
                throw new IllegalStateException("Upload já foi finalizado ou cancelado.");
            }

            // 2. Converter DTOs e LIMPAR O ETAG (A CORREÇÃO ESTÁ AQUI)
            // 2. Converter nossos DTOs para o formato que o SDK da AWS espera
            List<CompletedPart> awsParts = request.getParts().stream()
                    .map(part -> {
                        // --- ESTA É A CORREÇÃO ---
                        // Adiciona as aspas que o S3/MinIO espera
                        String eTagComAspas = "\"" + part.getETag() + "\""; 
                        
                        return CompletedPart.builder()
                                .partNumber(part.getPartNumber())
                                .eTag(eTagComAspas) // <-- Envia o ETag formatado
                                .build();
                    })
                    .collect(Collectors.toList());

            CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                    .parts(awsParts)
                    .build();

            // 3. Pegar a 'key' do objeto no banco
            String objectKey = attachment.getObjectKey();
            if (objectKey == null) {
                throw new IllegalStateException("Erro crítico: Object Key não encontrada para o anexo " + attachmentId);
            }

            // 4. Avisar o MinIO que terminamos
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .uploadId(attachment.getExternalUploadId())
                    .multipartUpload(completedUpload)
                    .build();

            s3Client.completeMultipartUpload(completeRequest);
            logger.info("Upload AWS finalizado com sucesso para: {}", objectKey);

            // 5. Gerar a URL final
            String finalUrl = s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build()).toExternalForm();

            // 6. Atualizar o status no DB
            attachment.setStatus("COMPLETED");
            attachment.setFileUrl(finalUrl); // Agora salvamos a URL real
            attachmentRepository.save(attachment);

        } catch (Exception e) {
            logger.error("Erro ao finalizar upload AWS: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao finalizar upload.", e);
        }
    }

// ... (initiateUpload e completeUpload continuam aqui) ...

    @Transactional(readOnly = true) // Esta operação é apenas de leitura
    public String getDownloadUrl(UUID attachmentId, UUID userId) {
        try {
            // 1. Buscar o anexo no banco
            Attachment attachment = attachmentRepository.findById(attachmentId)
                    .orElseThrow(() -> new EntityNotFoundException("Anexo não encontrado"));

            // 2. [Lógica de Permissão - TODO]
            // Em um sistema real, aqui você verificaria se 'userId'
            // é participante da conversa à qual este anexo pertence.
            // Por enquanto, apenas verificamos se o upload está completo.
            
            if (!"COMPLETED".equals(attachment.getStatus())) {
                throw new IllegalStateException("O upload deste arquivo ainda não foi finalizado.");
            }

            // 3. Gerar a URL pré-assinada para download (GET)
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(attachment.getObjectKey()) // A chave que salvamos no 'complete'
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5)) // URL expira em 5 minutos
                    .getObjectRequest(getObjectRequest)
                    .build();

            // 4. Criar a URL
            PresignedGetObjectRequest presignedUrl = s3Presigner.presignGetObject(presignRequest);
            
            logger.info("URL de download gerada para o anexo {}", attachmentId);
            return presignedUrl.url().toString();

        } catch (Exception e) {
            logger.error("Erro ao gerar URL de download: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao obter URL de download.", e);
        }
    }

}