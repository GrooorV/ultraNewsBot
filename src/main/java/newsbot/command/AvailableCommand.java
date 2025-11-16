package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.news.NewsPreferenceService;
import newsbot.shared.UserId;
import java.util.Objects;

public class AvailableCommand implements BotCommand {

    private final NewsPreferenceService newsPrefs;

    public AvailableCommand(NewsPreferenceService newsPrefs) {
        this.newsPrefs = Objects.requireNonNull(newsPrefs);
    }

    @Override
    public String getName() {
        return "\\available";
    }
    @Override
    public BotResponse execute(UserId userId, String args) {
        return BotResponse.say("Доступные категории: " + newsPrefs.available());
    }
}
