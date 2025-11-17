package br.com.yourcompany.platformcore.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import br.com.yourcompany.platformcore.websocket.RealtimeMessageHandler;

@Configuration
@EnableWebSocket // Habilita o servidor WebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Vamos injetar nosso futuro Handler (próximo passo)
    private final WebSocketHandler webSocketHandler;
    @Autowired
    private RealtimeMessageHandler messageHandler;

    public WebSocketConfig(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(messageHandler, "/ws/connect")
               
                .setAllowedOrigins("*"); 
    }
}