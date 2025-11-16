package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.news.NewsPreferenceService;
import newsbot.shared.UserId;
import java.util.Objects;

public class NewsCommand implements  BotCommand {

    private final NewsPreferenceService newsPrefs;

    public NewsCommand(NewsPreferenceService newsPrefs) {
        this.newsPrefs = Objects.requireNonNull(newsPrefs);
    }

    @Override
    public String getName() {
        return "\\news";
    }


    @Override
    public BotResponse execute(UserId userId, String args) {
        String[] parts = args.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isBlank() || "list".equalsIgnoreCase(parts[0])) {
            return BotResponse.say("Ваши категории: " + newsPrefs.list(userId));
        }

        if ("add".equalsIgnoreCase(parts[0]) && parts.length >= 2) {
            newsPrefs.add(userId, parts[1]);
            return BotResponse.say("Добавил категорию: " + parts[1]);
        }

        if ("del".equalsIgnoreCase(parts[0]) && parts.length >= 2) {
            newsPrefs.remove(userId, parts[1]);
            return BotResponse.say("Удалил категорию: " + parts[1]);
        }

        return BotResponse.say("Использование: \\news list | \\news add <категория> | \\news del <категория>");
    }
}
