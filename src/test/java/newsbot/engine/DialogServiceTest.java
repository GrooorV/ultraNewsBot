package newsbot.engine;

import newsbot.news.NewsPreferenceService;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogServiceTest {

    @Test
    void onboardingThenCategoryParsing() {
        DialogueEngine engine = new DialogueEngine();
        DialogueService service = new DialogueService(
                engine,
                new InMemorySessionRepository(),
                new NewsPreferenceService(new InMemoryUserProfileRepository())
        );

        String r1 = service.handle("alice", "");
        assertTrue(r1.startsWith("Начнём!"));

        String r2 = service.handle("alice", "спорт, экономика");
        assertTrue(r2.contains("Запомнил категории: спорт, экономика"));
        assertTrue(r2.contains("Ваши категории"));
    }

    @Test
    void newsCommandsAndMultiUserIndependence() {
        DialogueService svc = new DialogueService(
                new DialogueEngine(),
                new InMemorySessionRepository(),
                new NewsPreferenceService(new InMemoryUserProfileRepository())
        );

        // user1
        svc.handle("u1", ""); // start
        assertTrue(svc.handle("u1", "\\news list").contains("[]"));
        assertTrue(svc.handle("u1", "\\news add спорт").contains("Добавил"));
        assertTrue(svc.handle("u1", "\\news list").contains("спорт"));

        // user2 — независимая сессия/профиль
        svc.handle("u2", ""); // start
        String list2 = svc.handle("u2", "\\news list");
        assertEquals("Ваши категории: []", list2);
    }

    @Test
    void helpAndAvailable() {
        DialogueService svc = new DialogueService(
                new DialogueEngine(),
                new InMemorySessionRepository(),
                new NewsPreferenceService(new InMemoryUserProfileRepository())
        );

        assertTrue(svc.handle("u", "\\help").toLowerCase().contains("новостной"));
        assertTrue(svc.handle("u", "\\available").toLowerCase().contains("доступные категории"));
    }
}
