package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.engine.UserSession;
import newsbot.news.NewsPreferenceService;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;

import java.util.Objects;



public class CategoryCommand implements BotCommand {

    private final NewsPreferenceService newsPrefs;
    private final SessionRepository sessionRepo;

    public CategoryCommand(NewsPreferenceService newsPrefs, SessionRepository sessionRepo) {
        this.newsPrefs = Objects.requireNonNull(newsPrefs);
        this.sessionRepo = Objects.requireNonNull(sessionRepo);
    }

    @Override
    public String getName() {
        return "\\category";
    }

    @Override
    public BotResponse execute(UserId userId, String args) {
        String[] parts = args.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isBlank() || "list".equalsIgnoreCase(parts[0])) {
            return BotResponse.say("Ваши категории: " + newsPrefs.list(userId));
        }

        if ("add".equalsIgnoreCase(parts[0]) && parts.length >= 2) {
            String categoryName = parts[1];
            newsPrefs.add(userId, categoryName);
            clearPendingNews(userId);

            return BotResponse.say("Добавил категорию: " + categoryName);
        }

        if ("del".equalsIgnoreCase(parts[0]) && parts.length >= 2) {
            String categoryName = parts[1];
            newsPrefs.remove(userId, categoryName);
            clearPendingNews(userId);

            return BotResponse.say("Удалил категорию: " + categoryName);
        }

        return BotResponse.say("Использование: \\category [list | add <имя> | del <имя>]");
    }

    private void clearPendingNews(UserId userId) {
        UserSession session = sessionRepo.getOrCreate(userId);
        session.clearPendingNews();
        sessionRepo.save(userId, session);
    }
}