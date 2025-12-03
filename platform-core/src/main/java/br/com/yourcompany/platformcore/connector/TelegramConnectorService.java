package br.com.yourcompany.platformcore.connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.springframework.beans.factory.annotation.Autowired; // <-- Adicionar
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class TelegramConnectorService extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramConnectorService.class);
    private static final String TOPIC_IN_TELEGRAM = "incoming_telegram_updates";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private final String botUsername;

    public TelegramConnectorService(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${telegram.bot.username}") String botUsername) {
        super(botToken);
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    /**
     * Este método será usado pela Tarefa 2.4 (recebimento de webhooks)
     * Por enquanto, vamos apenas logar.
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();
            logger.info("TELEGRAM (IN): Mensagem recebida de {}: {}", chatId, text);
            try {
                kafkaTemplate.send(TOPIC_IN_TELEGRAM, update);
                logger.info("Update {} do Telegram publicado no Kafka.", update.getUpdateId());
            } catch (Exception e) {
                logger.error("Erro ao enviar mensagem para o Kafka: {}", e.getMessage());
            }
        }
    }

    /**
     * Este é o método que nosso Worker (próximo passo) irá chamar!
     */
    public void sendMessage(String externalChatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(externalChatId)
                .text(text)
                .build();
        try {
            execute(message); // Envia a mensagem para a API do Telegram
            logger.info("TELEGRAM (OUT): Mensagem enviada para {}: {}", externalChatId, text);
        } catch (TelegramApiException e) {
            logger.error("TELEGRAM (OUT): Erro ao enviar mensagem para {}: {}", externalChatId, e.getMessage());
        }
    }

    /**
     * Registra o bot no Telegram quando a aplicação Spring inicia
     */
    //@PostConstruct

    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            logger.info("Conector do Telegram registrado com sucesso: {}", this.botUsername);
        } catch (TelegramApiException e) {
            logger.error("Erro ao registrar o bot do Telegram: {}", e.getMessage());
        }
    }
}