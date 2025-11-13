package br.com.yourcompany.platformcore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@Component
public class BucketInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BucketInitializer.class);

    @Autowired
    private S3Client s3Client; // Usando o cliente AWS

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public void run(String... args) {
        try {
            // Tenta verificar se o bucket existe
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            logger.info("AWS S3: Bucket '{}' já existe.", bucketName);

        } catch (NoSuchBucketException e) {
            // Se recebermos "NoSuchBucket", então criamos
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                logger.info("AWS S3: Bucket '{}' criado com sucesso.", bucketName);
            } catch (Exception ex) {
                logger.error("Erro crítico ao criar bucket S3: {}", ex.getMessage());
            }
        } catch (Exception e) {
            // Outros erros (ex: conexão recusada se o MinIO estiver desligado)
            logger.error("Erro ao conectar ao S3/MinIO: {}", e.getMessage());
        }
    }
}