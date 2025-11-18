package br.com.yourcompany.platformcore.grpc.server;
import br.com.yourcompany.platformcore.grpc.PresenceServiceGrpc;
import br.com.yourcompany.platformcore.grpc.UserPresenceRequest;
import br.com.yourcompany.platformcore.grpc.UserPresenceResponse;
import br.com.yourcompany.platformcore.service.PresenceService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@GrpcService // <-- Anotação mágica que expõe isso na porta 9090
public class PresenceGrpcController extends PresenceServiceGrpc.PresenceServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(PresenceGrpcController.class);

    @Autowired
    private PresenceService presenceService; // Nosso serviço do Redis

    @Override
    public void isUserOnline(UserPresenceRequest request, StreamObserver<UserPresenceResponse> responseObserver) {
        String userIdStr = request.getUserId();
        logger.info("gRPC: Recebida consulta de presença para User ID: {}", userIdStr);

        boolean isOnline = false;
        try {
            // Consulta o Redis
            isOnline = presenceService.isUserOnline(UUID.fromString(userIdStr));
        } catch (Exception e) {
            logger.error("Erro ao consultar Redis via gRPC", e);
        }

        // Monta a resposta (Protobuf)
        UserPresenceResponse response = UserPresenceResponse.newBuilder()
                .setIsOnline(isOnline)
                .build();

        // Envia a resposta
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}