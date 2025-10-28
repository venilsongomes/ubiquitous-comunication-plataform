package br.com.yourcompany.platform_core.Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket // Habilita o servidor WebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Vamos injetar nosso futuro Handler (próximo passo)
    private final WebSocketHandler webSocketHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler, "/ws/connect")
                // Permite que clientes de qualquer origem se conectem
                // TODO: Em produção, restrinja isso para o seu domínio web
                .setAllowedOrigins("*"); 
    }
}