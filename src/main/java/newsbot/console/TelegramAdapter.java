package newsbot.console;

import newsbot.engine.BotResponse;
import newsbot.engine.DialogueService;
import newsbot.repository.UserProfileRepository;
import newsbot.shared.UserId;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
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
        System.out.println("i was here");
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
        UserId userId = new UserId(String.valueOf(chatID));
        SendMessage message = SendMessage.builder()
                .chatId(chatID)
                .text(text)
                .replyMarkup(generalResponseButtons.getMessageWithGeneralButtons(userProfileRepo.getCategories(userId)))
                .disableWebPagePreview(true)
                .build();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}