package newsbot.news;

import newsbot.command.resolver.FormatResolver;
import newsbot.engine.UserSession;
import newsbot.repository.NewsRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.memory.InMemoryNewsRepository;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;
import newsbot.shared.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewsFeedGeneratorTest {


    private static final NewsStory SPORT_STORY_1 = new NewsStory(
            "Спартак победил", "date", "author", "lenta.ru/sport1",
            NewsCategory.SPORT, "Спартак победил Динамо");

    private static final NewsStory ECON_STORY_1 = new NewsStory(
            "Доллар вырос", "date", "author", "lenta.ru/econ1",
            NewsCategory.ECONOMY, "Доллар снова 100");


    private NewsFeedGenerator generator;
    private UserProfileRepository userProfileRepo;
    private SessionRepository sessionRepo;
    private NewsRepository newsRepo;
    private FakeNewsProvider fakeProvider;
    private final UserId testUser = new UserId("user1");

    @BeforeEach
    void setUp() {
        userProfileRepo = new InMemoryUserProfileRepository();
        sessionRepo = new InMemorySessionRepository();
        fakeProvider = new FakeNewsProvider(); // Используем фейк
        newsRepo = new InMemoryNewsRepository(fakeProvider);

        generator = new NewsFeedGenerator(userProfileRepo, sessionRepo, newsRepo);
    }

    @Test
    void failsIfCategoriesAreEmpty() {

        String response = new StoryContentBuilder().getStory(generator.getOneStory(testUser), new FormatResolver(FormatResolver.OutputMode.CONSOLE));
        assertTrue(response.contains("Вы не выбрали ни одной категории"));
    }

    @Test
    void loadsNewsIfQueueIsEmpty() {

        userProfileRepo.addCategory(testUser, NewsCategory.SPORT);


        fakeProvider.setStoriesToReturn(List.of(SPORT_STORY_1));

        String response = new StoryContentBuilder().getStory(generator.getOneStory(testUser), new FormatResolver(FormatResolver.OutputMode.CONSOLE));


        assertTrue(response.contains("Спартак победил"));


        UserSession session = sessionRepo.getOrCreate(testUser);
        assertFalse(session.hasPendingNews());
    }

    @Test
    void servesNewsFromQueueIfExists() {
        // 1. Настраиваем пользователя
        userProfileRepo.addCategory(testUser, NewsCategory.SPORT);
        userProfileRepo.addCategory(testUser, NewsCategory.ECONOMY);
        Set<NewsCategory> categories = Set.of(NewsCategory.SPORT, NewsCategory.ECONOMY);

        // 2. Настраиваем провайдер
        fakeProvider.setStoriesToReturn(List.of(SPORT_STORY_1, ECON_STORY_1));

        // 3. !!! ИСПРАВЛЕНИЕ: "ПРОГРЕВАЕМ" КЭШ РЕПОЗИТОРИЯ !!!
        // Это заполнит cacheMap в newsRepo
        newsRepo.getNewsByCategory(categories, Instant.now().minusSeconds(60));

        // 4. "Руками" кладем в сессию 2 новости (имитируем, что \news уже отработал)
        UserSession session = sessionRepo.getOrCreate(testUser);
        session.setPendingNews(List.of(SPORT_STORY_1, ECON_STORY_1));
        sessionRepo.save(testUser, session);

        // 5. Провайдер теперь пуст (чтобы доказать, что он не вызывается при *чтении*)
        fakeProvider.setStoriesToReturn(List.of());

        // 6. Вызов 1 (теперь он найдет "lenta.ru/sport1" в cacheMap)
        String response1 = new StoryContentBuilder().getStory(generator.getOneStory(testUser), new FormatResolver(FormatResolver.OutputMode.CONSOLE));
        assertTrue(response1.contains("Спартак победил"), "Первая новость из очереди не найдена");
        assertTrue(sessionRepo.getOrCreate(testUser).hasPendingNews(), "Вторая новость не осталась в очереди");

        // 7. Вызов 2
        String response2 = new StoryContentBuilder().getStory(generator.getOneStory(testUser), new FormatResolver(FormatResolver.OutputMode.CONSOLE));
        assertTrue(response2.contains("Доллар вырос"), "Вторая новость из очереди не найдена");
        assertFalse(sessionRepo.getOrCreate(testUser).hasPendingNews(), "Очередь не опустела");
    }

    @Test
    void returnsNoNewsIfProviderIsEmpty() {

        userProfileRepo.addCategory(testUser, NewsCategory.SPORT);

        fakeProvider.setStoriesToReturn(List.of());

        String response = new StoryContentBuilder().getStory(generator.getOneStory(testUser), new FormatResolver(FormatResolver.OutputMode.CONSOLE));


        assertTrue(response.contains("Новых новостей по вашим категориям нет"));
    }
}