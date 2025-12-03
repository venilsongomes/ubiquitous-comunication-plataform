package br.com.yourcompany.platformcore.config;

import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import net.devh.boot.grpc.server.security.check.AccessPredicate;
import net.devh.boot.grpc.server.security.check.GrpcSecurityMetadataSource;
import net.devh.boot.grpc.server.security.check.ManualGrpcSecurityMetadataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.annotation.Nullable;

@Configuration
public class GrpcSecurityConfig {

    // 1. Define como ler a autenticação (neste caso, vamos permitir acesso anônimo interno por enquanto
    //    ou usar um reader customizado se quisermos repassar o JWT).
    //    Para simplificar a comunicação interna Worker -> Service, vamos usar um Reader que não exige auth.
    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return new GrpcAuthenticationReader() {
            @Nullable
            @Override
            public Authentication readAuthentication(io.grpc.ServerCall<?, ?> call, io.grpc.Metadata headers) throws AuthenticationException {
                return null; // Retorna null para indicar "sem autenticação" (anônimo)
            }
        };
    }

    // 2. Define as regras de acesso (Quem pode chamar o quê?)
    @Bean
    public GrpcSecurityMetadataSource grpcSecurityMetadataSource() {
        ManualGrpcSecurityMetadataSource source = new ManualGrpcSecurityMetadataSource();
        
        // Permite que QUALQUER UM chame o serviço de presença
        // (Pois quem chama é nosso próprio Worker interno, que está na mesma rede segura)
        source.setDefault(AccessPredicate.permitAll()); 
        
        return source;
    }
}