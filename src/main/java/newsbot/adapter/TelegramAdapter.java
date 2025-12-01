package newsbot.adapter;

import newsbot.ui.GeneralResponseButtons;
import newsbot.engine.BotResponse;
import newsbot.engine.DialogueService;
import newsbot.repository.UserProfileRepository;
import newsbot.shared.UserId;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class TelegramAdapter implements LongPollingSingleThreadUpdateConsumer{
    private final TelegramClient telegramClient;
    private final DialogueService dialog;
    private final GeneralResponseButtons generalResponseButtons;
    private final UserProfileRepository userProfileRepo;

    public TelegramAdapter(String botToken,
                           DialogueService dialog,
                           GeneralResponseButtons generalResponseButtons,
                           UserProfileRepository userProfileRepo) {
        this.dialog = dialog;
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.generalResponseButtons = generalResponseButtons;
        this.userProfileRepo = userProfileRepo;
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
        UserId userId = new UserId(String.valueOf(chatID));

        String imgTagStart = "<image>";
        String imgTagEnd = "</image>";

        String imageUrl = null;
        String caption = text;

        var replyMarkup = generalResponseButtons.getMessageWithGeneralButtons(
                userProfileRepo.getCategories(userId));

        int startIdx = text.indexOf(imgTagStart);
        if (startIdx != -1) {
            int endIdx = text.indexOf(imgTagEnd, startIdx + imgTagStart.length());
            if (endIdx != -1) {
                imageUrl = text.substring(startIdx + imgTagStart.length(), endIdx).trim();
                caption = (text.substring(0, startIdx) + text.substring(endIdx + imgTagEnd.length())).trim();
            }
        }

        try {
            if (imageUrl != null && !imageUrl.isBlank()) {
                SendPhoto photo = SendPhoto.builder()
                        .parseMode("HTML")
                        .chatId(chatID)
                        .caption(caption)
                        .replyMarkup(replyMarkup)
                        .photo(new InputFile(imageUrl))
                        .build();
                telegramClient.execute(photo);
            } else {
                SendMessage message = SendMessage.builder()
                        .parseMode("HTML")
                        .chatId(chatID)
                        .text(text)
                        .replyMarkup(replyMarkup)
                        .build();
                telegramClient.execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}