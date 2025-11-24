package newsbot.console;

import newsbot.engine.BotResponse;
import newsbot.engine.DialogueService;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class TelegramAdapter implements LongPollingSingleThreadUpdateConsumer{
    private final TelegramClient telegramClient;
    private final DialogueService dialog;

    public TelegramAdapter(String botToken, DialogueService dialog) {
        this.dialog = dialog;
        this.telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageText = update.getMessage().getText();
        long chatID = update.getMessage().getChatId();

        String currentUser = String.valueOf(chatID);

        BotResponse response = dialog.handle(currentUser, messageText);

        sendMessage(chatID, response.getMessage());

        response.getNewActiveUser().ifPresent(newUserId -> {
            if (!currentUser.equals(newUserId)) {
                BotResponse welcome = dialog.handle(newUserId, "");
                try {
                    sendMessage(chatID, welcome.getMessage());
                } catch (Exception ignored) {}
            }
        });
    }

    private void sendMessage(long chatID, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatID)
                .text(text)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
