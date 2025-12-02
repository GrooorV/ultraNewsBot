package newsbot.command;

import newsbot.command.resolver.FormatResolver;
import newsbot.engine.BotResponse;
import newsbot.news.NewsStory;
import newsbot.news.StoryContentBuilder;
import newsbot.shared.UserId;
import newsbot.news.NewsFeedGenerator;
import java.util.List;

public class NewsCommand implements BotCommand {

    private final NewsFeedGenerator feedGenerator;
    private final FormatResolver format;

    public NewsCommand(NewsFeedGenerator feedGenerator, FormatResolver format) {
        this.feedGenerator = feedGenerator;
        this.format = format;
    }

    @Override
    public String getName() {
        return "\\news";
    }


    @Override
    public BotResponse execute(UserId userId, String args) {
        String[] parts = args.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isBlank() || "get".equalsIgnoreCase(parts[0])) {
            List<NewsStory> newStories = feedGenerator.getOneStory(userId);
            return BotResponse.say(StoryContentBuilder.getStory(newStories, format));
        }

        // Сообщение об ошибке стало проще
        return BotResponse.say("Использование: \\news [get]");
    }
}
