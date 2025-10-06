package newsbot.engine;

import newsbot.news.NewsPreferenceService;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DialogServiceTest {

    private DialogueService newService() {
        return new DialogueService(
                new DialogueEngine(),
                new InMemorySessionRepository(),
                new NewsPreferenceService(new InMemoryUserProfileRepository())
        );
    }

    @Test
    void showsOnboardingAtFirstEmptyInput() {
        DialogueService svc = newService();
        String r = svc.handle("u", "");
        assertTrue(r.startsWith("Начнём!"));
        assertTrue(r.toLowerCase().contains("какие категории"));
    }

    @Test
    void parsesCommaAndSemicolonSeparatedCategories() {
        DialogueService svc = newService();
        svc.handle("u", ""); // start
        String r = svc.handle("u", "спорт; экономика, технологии");
        assertTrue(r.contains("Запомнил категории"));
        String list = svc.handle("u", "\\news list");
        assertTrue(list.contains("спорт"));
        assertTrue(list.toLowerCase().contains("экономика"));
        assertTrue(list.toLowerCase().contains("технологии"));
    }

    @Test
    void helpAndAvailableCommandsWork() {
        DialogueService svc = newService();
        assertTrue(svc.handle("u", "\\help").toLowerCase().contains("новостной"));
        String avail = svc.handle("u", "\\available").toLowerCase();
        assertTrue(avail.contains("доступные категории"));
        assertTrue(avail.contains("спорт"));
    }

    @Test
    void newsAddAndDelCommands() {
        DialogueService svc = newService();
        svc.handle("u", ""); // start
        assertTrue(svc.handle("u", "\\news list").contains("[]"));
        assertTrue(svc.handle("u", "\\news add спорт").contains("Добавил"));
        assertTrue(svc.handle("u", "\\news list").contains("спорт"));
        assertTrue(svc.handle("u", "\\news del спорт").contains("Удалил"));
        assertTrue(svc.handle("u", "\\news list").contains("[]"));
    }

    @Test
    void multiUserProfilesAreIndependent() {
        DialogueService svc = newService();

        // user1
        svc.handle("u1", "");
        svc.handle("u1", "\\news add спорт");

        // user2
        svc.handle("u2", "");
        String list2 = svc.handle("u2", "\\news list");
        assertEquals("Ваши категории: []", list2);

        // user1 still has category
        String list1 = svc.handle("u1", "\\news list");
        assertTrue(list1.contains("спорт"));
    }

    @Test
    void emptyInputAfterStartRepeatsOnboardingTip() {
        DialogueService svc = newService();
        svc.handle("u", ""); // onboarding
        String r = svc.handle("u", ""); // empty again
        assertTrue(r.toLowerCase().contains("какие категории"));
    }
}
