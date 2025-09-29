package newsbot.news;

import newsbot.repository.memory.InMemoryUserProfileRepository;
import newsbot.shared.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NewsPreferenceServiceTest {

    @Test
    void addListRemove() {
        NewsPreferenceService svc = new NewsPreferenceService(new InMemoryUserProfileRepository());
        UserId u = new UserId("alice");

        assertEquals("[]", svc.list(u));
        svc.add(u, "спорт");
        svc.add(u, "Экономика");
        String list = svc.list(u);
        assertTrue(list.contains("спорт"));
        assertTrue(list.toLowerCase().contains("экономика"));

        svc.remove(u, "спорт");
        assertFalse(svc.list(u).toLowerCase().contains("спорт"));
    }

    @Test
    void availableShowsFixedList() {
        NewsPreferenceService svc = new NewsPreferenceService(new InMemoryUserProfileRepository());
        String available = svc.available().toLowerCase();
        assertTrue(available.contains("спорт"));
        assertTrue(available.contains("экономика"));
        assertTrue(available.contains("технологии"));
    }
}
