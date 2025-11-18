package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.engine.UserSession;
import newsbot.news.NewsPreferenceService;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;
import java.util.Arrays;
import java.util.Objects;

public class SetCategoriesCommand implements BotCommand {

    private final NewsPreferenceService newsPrefs;
    private final SessionRepository sessionRepository;

    public SetCategoriesCommand(NewsPreferenceService newsPrefs, SessionRepository sessionRepository) {
        this.newsPrefs = Objects.requireNonNull(newsPrefs);
        this.sessionRepository = Objects.requireNonNull(sessionRepository);
    }

    @Override
    public String getName() {
        return "\\default";
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

        UserSession session = sessionRepository.getOrCreate(userId);
        session.clearPendingNews();
        sessionRepository.save(userId, session);

        return BotResponse.say(
                "Запомнил категории: " + String.join(", ", cats) +
                        ". Ваши категории сейчас: " + newsPrefs.list(userId) +
                        ". Можете добавить ещё или используйте \\news list."
        );
    }
}