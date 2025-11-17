package newsbot.engine;

import newsbot.command.*;
import newsbot.command.resolver.CommandResolver;
import newsbot.news.LentaNewsProvider;
import newsbot.news.NewsFeedGenerator;
import newsbot.news.NewsPreferenceService;
import newsbot.news.NewsProvider;
import newsbot.repository.NewsRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.memory.InMemoryNewsRepository;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DialogueServiceTest {

    private DialogueService svc;
    private UserProfileRepository userProfileRepo;
    private SessionRepository sessionRepo;
    private NewsRepository newsRepo;
    private NewsProvider newsProvider;
    private NewsFeedGenerator feedGenerator;

    /**
     * Этот метод @BeforeEach воссоздает "сборку" зависимостей
     * так же, как это делает ConsoleApp.main
     */
    @BeforeEach
    void setupFullService() {
        // 1. Репозитории
        newsProvider = new LentaNewsProvider();

        userProfileRepo = new InMemoryUserProfileRepository();
        sessionRepo = new InMemorySessionRepository();
        newsRepo = new InMemoryNewsRepository(newsProvider);

        feedGenerator = new NewsFeedGenerator(userProfileRepo, sessionRepo, newsRepo);
        // 2. Сервисы и движок
        DialogueEngine engine = new DialogueEngine();
        NewsPreferenceService newsPrefs = new NewsPreferenceService(userProfileRepo);

        // 3. Команды
        BotCommand helpCommand = new HelpCommand();
        BotCommand availableCommand = new AvailableCommand(newsPrefs);
        BotCommand newsCommand = new NewsCommand(newsPrefs, feedGenerator);
        BotCommand setCategoriesCommand = new SetCategoriesCommand(newsPrefs);
        BotCommand changeUserCommand = new ChangeProfileCommand();
        BotCommand whoAmICommand = new WhoAmICommand();

        // 4. Резолвер
        CommandResolver commandResolver = new CommandResolver(setCategoriesCommand);
        commandResolver.register(helpCommand);
        commandResolver.register(availableCommand);
        commandResolver.register(newsCommand);
        commandResolver.register(changeUserCommand);
        commandResolver.register(whoAmICommand);

        // 5. Сервис
        svc = new DialogueService(engine, sessionRepo, commandResolver);
    }

    @Test
    void showsOnboardingAtFirstEmptyInput() {
        BotResponse r = svc.handle("u", "");
        assertTrue(r.getMessage().startsWith("Начнём!"));
        assertFalse(r.getNewActiveUser().isPresent()); // Проверяем, что нет смены юзера
    }

    @Test
    void parsesCommaAndSemicolonSeparatedCategories() {
        svc.handle("u", ""); // start
        BotResponse r = svc.handle("u", "спорт; экономика, технологии");
        assertTrue(r.getMessage().contains("Запомнил категории"));

        BotResponse list = svc.handle("u", "\\news list");
        String listMsg = list.getMessage().toLowerCase();
        assertTrue(listMsg.contains("спорт"));
        assertTrue(listMsg.contains("экономика"));
        assertTrue(listMsg.contains("технологии"));
    }

    @Test
    void helpAndAvailableCommandsWork() {
        BotResponse help = svc.handle("u", "\\help");
        assertTrue(help.getMessage().toLowerCase().contains("новостной"));

        BotResponse avail = svc.handle("u", "\\available");
        String availMsg = avail.getMessage().toLowerCase();
        assertTrue(availMsg.contains("доступные категории"));
        assertTrue(availMsg.contains("спорт"));
    }

    @Test
    void newsAddAndDelCommands() {
        svc.handle("u", ""); // start
        assertTrue(svc.handle("u", "\\news list").getMessage().contains("ни одной"));
        assertTrue(svc.handle("u", "\\news add спорт").getMessage().contains("Добавил"));
        assertTrue(svc.handle("u", "\\news list").getMessage().contains("спорт"));
        assertTrue(svc.handle("u", "\\news del спорт").getMessage().contains("Удалил"));
        assertTrue(svc.handle("u", "\\news list").getMessage().contains("ни одной"));
    }

    @Test
    void multiUserProfilesAreIndependent() {
        // user1
        svc.handle("u1", "");
        svc.handle("u1", "\\news add спорт");

        // user2
        svc.handle("u2", "");
        BotResponse list2 = svc.handle("u2", "\\news list");
        assertEquals("Ваши категории: пока не выбрано ни одной категории!", list2.getMessage());

        // user1 still has category
        BotResponse list1 = svc.handle("u1", "\\news list");
        assertTrue(list1.getMessage().contains("спорт"));
    }

    @Test
    void emptyInputAfterStartRepeatsOnboardingTip() {
        svc.handle("u", ""); // onboarding
        BotResponse r = svc.handle("u", ""); // empty again
        assertTrue(r.getMessage().toLowerCase().contains("новости о чём вас интересуют"));
    }


    @Test
    void whoAmICommandReturnsCurrentUserId() {
        BotResponse r = svc.handle("user-123", "\\whoami");
        assertEquals("Текущий профиль: user-123", r.getMessage());
        assertFalse(r.getNewActiveUser().isPresent());
    }

    @Test
    void changeUserCommandReturnsSpecialResponse() {
        BotResponse r = svc.handle("user-123", "\\changeuser admin");

        // 1. Проверяем сообщение
        assertEquals("Текущий профиль: admin", r.getMessage());

        // 2. Проверяем *инструкцию* для адаптера
        assertTrue(r.getNewActiveUser().isPresent());
        assertEquals("admin", r.getNewActiveUser().get());
    }

    @Test
    void changeUserCommandRequiresArgument() {
        BotResponse r = svc.handle("u", "\\changeuser ");
        assertEquals("Использование: \\changeuser <userId>", r.getMessage());
        assertFalse(r.getNewActiveUser().isPresent());
    }
}