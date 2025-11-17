package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.engine.UserSession;
import newsbot.news.NewsPreferenceService;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;
import newsbot.news.NewsFeedGenerator;
import java.util.Objects;

public class NewsCommand implements BotCommand {

    private final NewsFeedGenerator feedGenerator;

    public NewsCommand(NewsFeedGenerator feedGenerator) {
        this.feedGenerator = feedGenerator;
    }

    @Override
    public String getName() {
        return "\\news";
    }


    @Override
    public BotResponse execute(UserId userId, String args) {
        String[] parts = args.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isBlank() || "get".equalsIgnoreCase(parts[0])) {
            return BotResponse.say(feedGenerator.getOneStory(userId));
        }

        // Сообщение об ошибке стало проще
        return BotResponse.say("Использование: \\news [get]");
    }
}
