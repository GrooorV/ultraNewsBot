package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.engine.UserSession;
import newsbot.news.NewsCategory;
import newsbot.news.NewsPreferenceService;
import newsbot.news.NewsStory;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;
import newsbot.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryCommandTest {


    private static final NewsStory TECH_STORY_1 = new NewsStory(
            "Вышел новый Java", "date", "author", "lenta.ru/tech1",
            NewsCategory.TECHNOLOGY, "Java 40");


    private CategoryCommand command;
    private UserProfileRepository userProfileRepo;
    private SessionRepository sessionRepo;
    private NewsPreferenceService newsPrefs;
    private final UserId testUser = new UserId("test-user");

    @BeforeEach
    void setUp() {
        userProfileRepo = new InMemoryUserProfileRepository();
        sessionRepo = new InMemorySessionRepository();
        newsPrefs = new NewsPreferenceService(userProfileRepo);

        command = new CategoryCommand(newsPrefs, sessionRepo);
    }

    @Test
    void listCommandWorks() {
        BotResponse r = command.execute(testUser, "list");
        assertTrue(r.getMessage().contains("ни одной"));

        userProfileRepo.addCategory(testUser, NewsCategory.ECONOMY);

        BotResponse r2 = command.execute(testUser, ""); // list по умолчанию
        assertTrue(r2.getMessage().contains("экономика"));
    }

    @Test
    void addCommandWorks() {
        BotResponse r = command.execute(testUser, "add спорт");
        assertTrue(r.getMessage().contains("Добавил категорию: спорт"));
        assertEquals("спорт ", newsPrefs.list(testUser));
    }

    @Test
    void delCommandWorks() {
        newsPrefs.add(testUser, "спорт"); // Сначала добавим
        assertEquals("спорт ", newsPrefs.list(testUser));

        BotResponse r = command.execute(testUser, "del спорт");
        assertTrue(r.getMessage().contains("Удалил категорию: спорт"));
        assertTrue(newsPrefs.list(testUser).contains("ни одной"));
    }

    @Test
    void addCommandClearsPendingNews() {
        UserSession session = sessionRepo.getOrCreate(testUser);
        session.setPendingNews(List.of(TECH_STORY_1)); // Используем локальную константу
        sessionRepo.save(testUser, session);
        assertTrue(session.hasPendingNews()); // Убедились, что очередь есть


        command.execute(testUser, "add спорт");


        UserSession updatedSession = sessionRepo.getOrCreate(testUser);
        assertFalse(updatedSession.hasPendingNews());
    }
}