package br.com.yourcompany.platformcore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class PresenceService {

    private static final String KEY_PREFIX = "user:presence:";
    // O usuário é considerado offline se não enviar "ping" por 60 segundos
    private static final Duration TTL = Duration.ofSeconds(60);

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * Marca o usuário como ONLINE.
     * Deve ser chamado quando conecta e periodicamente (heartbeat).
     */
    public void setUserOnline(UUID userId) {
        String key = KEY_PREFIX + userId.toString();
        redisTemplate.opsForValue().set(key, "ONLINE", TTL);
    }

    /**
     * Remove o status (marca como OFFLINE).
     * Chamado no disconnect.
     */
    public void setUserOffline(UUID userId) {
        String key = KEY_PREFIX + userId.toString();
        redisTemplate.delete(key);
    }

    /**
     * Verifica se está online.
     */
    public boolean isUserOnline(UUID userId) {
        String key = KEY_PREFIX + userId.toString();
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}