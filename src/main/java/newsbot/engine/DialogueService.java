package newsbot.engine;

import newsbot.news.NewsPreference;
import newsbot.news.NewsPreferenceService;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;

import java.util.Arrays;


public class DialogueService {
    private DialogueEngine engine;
    private SessionRepository sessions;
    private NewsPreferenceService newsPrefs;


    public  DialogueService(DialogueEngine engine, SessionRepository sessions, NewsPreferenceService newsPrefs) {
        this.engine = engine;
        this.sessions = sessions;
        this.newsPrefs = newsPrefs;
    }

    public String handle(String rawUserId, String rawInput) {
        UserId userId = new UserId(rawUserId);
        UserSession session = sessions.getOrCreate(userId);

        // Справка
        if (engine.isHelp(rawInput)) {
            return helpText();
        }

        // Просмотр доступных категорий
        if ("\\available".equalsIgnoreCase(rawInput)) {
            return "Доступные категории: " + newsPrefs.available();
        }

        // Команды новостей
        if (rawInput.startsWith("\\news")) {
            return handleNews(userId, rawInput);
        }

        // Онбординг
        if (!session.isStarted()) {
            session.markStarted();
            sessions.save(userId, session);
            return "Начнём! " + engine.onboardText();
        }

        // Интерпретация свободного текста как списка категорий
        if (engine.looksLikeCategoryInput(rawInput)) {
            String[] cats = Arrays.stream(rawInput.split("[,;]"))
                    .map(String::trim).filter(s -> !s.isBlank()).toArray(String[]::new);
            for (String c : cats) newsPrefs.add(userId, c);
            return "Запомнил категории: " + String.join(", ", cats) +
                    ". Ваши категории сейчас: " + newsPrefs.list(userId) +
                    ". Можете добавить ещё или используйте \\news list.";
        }

        return engine.onboardText();
    }

    private String handleNews(UserId userId, String input) {
        String[] parts = input.split("\\s+");
        if (parts.length == 1 || "list".equalsIgnoreCase(parts[1])) {
            return "Ваши категории: " + newsPrefs.list(userId);
        } else if ("add".equalsIgnoreCase(parts[1]) && parts.length >= 3) {
            newsPrefs.add(userId, parts[2]);
            return "Добавил категорию: " + parts[2];
        } else if ("del".equalsIgnoreCase(parts[1]) && parts.length >= 3) {
            newsPrefs.remove(userId, parts[2]);
            return "Удалил категорию: " + parts[2];
        } else {
            return "Использование: \\news list | \\news add <категория> | \\news del <категория>";
        }
    }

    private String helpText() {
        return """
               Я новостной бот.
               Команды:
               - \\help — показать эту справку
               - \\available — список доступных категорий
               - \\news list | add <категория> | del <категория> — управлять предпочтениями
               Можно просто перечислить категории через запятую, и я их запомню.
               """;
    }
}
