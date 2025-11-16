package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.news.NewsPreferenceService;
import newsbot.shared.UserId;
import java.util.Arrays;
import java.util.Objects;

public class SetCategoriesCommand implements BotCommand {

    private final NewsPreferenceService newsPrefs;

    public SetCategoriesCommand(NewsPreferenceService newsPrefs) {
        this.newsPrefs = Objects.requireNonNull(newsPrefs);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public BotResponse execute(UserId userId, String args) {
        String[] cats = Arrays.stream(args.split("[,;]"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);

        if (cats.length == 0) {
            return BotResponse.say("Не распознал категорий. Попробуйте еще раз.");
        }

        for (String c : cats) {
            newsPrefs.add(userId, c);
        }

        return BotResponse.say(
                "Запомнил категории: " + String.join(", ", cats) +
                        ". Ваши категории сейчас: " + newsPrefs.list(userId) +
                        ". Можете добавить ещё или используйте \\news list."
        );
    }
}