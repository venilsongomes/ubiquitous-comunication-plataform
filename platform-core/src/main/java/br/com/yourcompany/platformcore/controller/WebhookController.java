package br.com.yourcompany.platformcore.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import br.com.yourcompany.platformcore.connector.TelegramConnectorService;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private TelegramConnectorService telegramService; // Reutilizamos nosso serviço

    /**
     * Este é o endpoint que o Telegram chamará.
     * Ele recebe o objeto 'Update' completo do Telegram.
     */
    @PostMapping("/telegram")
    public void handleTelegramWebhook(@RequestBody Update update) {
        if (update == null) {
            logger.warn("Webhook do Telegram recebeu um 'update' nulo.");
            return;
        }
        
        logger.info("Webhook do Telegram recebido: Update ID {}", update.getUpdateId());
        
        // Em vez de processar aqui, passamos para o nosso serviço
        // que já tem o método 'onUpdateReceived' pronto!
        telegramService.onUpdateReceived(update);
    }
    
    @PostMapping("/instagram")
    public void handleInstagramWebhook(@RequestBody(required = false) java.util.Map<String, Object> payload) {
        if (payload == null) {
            logger.warn("Webhook do Instagram recebeu payload nulo.");
            return;
        }
        logger.info("Webhook do Instagram recebido: {}", payload);
        // Aqui você pode encaminhar para o worker ou serviço responsável
    }
    
    @PostMapping("/whatsapp")
    public void handleWhatsappWebhook(@RequestBody(required = false) java.util.Map<String, Object> payload) {
        if (payload == null) {
            logger.warn("Webhook do WhatsApp recebeu payload nulo.");
            return;
        }
        logger.info("Webhook do WhatsApp recebido: {}", payload);
        // Aqui você pode encaminhar para o worker ou serviço responsável
    }
}