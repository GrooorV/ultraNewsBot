package newsbot.console;

import newsbot.engine.BotResponse;
import newsbot.engine.DialogueService;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;


public class TelegramAdapter implements LongPollingSingleThreadUpdateConsumer{
    private final TelegramClient telegramClient;
    private final DialogueService dialog;
    private final GeneralResponseButtons generalResponseButtons;

    public TelegramAdapter(String botToken, DialogueService dialog, GeneralResponseButtons generalResponseButtons) {
        this.dialog = dialog;
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.generalResponseButtons = generalResponseButtons;
    }

    @Override
    public void consume(Update update) {
        String command = "";
        long chatID;

        if (update.hasCallbackQuery()) {
            command = update.getCallbackQuery().getData();
            chatID = update.getCallbackQuery().getMessage().getChatId();

        } else if (update.hasMessage() && update.getMessage().hasText()) {
            command = update.getMessage().getText();
            chatID = update.getMessage().getChatId();
        } else return;

        String currentUser = String.valueOf(chatID);

        System.out.println(chatID); // это потом убрать
        System.out.println(command); // это потом убрать
        BotResponse response = dialog.handle(currentUser, command);

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
                .replyMarkup(generalResponseButtons.setMessageWithGeneralButtons())
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}