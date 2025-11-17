package newsbot.engine;

import newsbot.command.*;
import newsbot.command.resolver.CommandResolver;
import newsbot.news.*; // Импортируем FakeNewsProvider
import newsbot.repository.NewsRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.memory.InMemoryNewsRepository;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DialogueServiceTest {

    private DialogueService svc;
    private UserProfileRepository userProfileRepo;
    private SessionRepository sessionRepo;
    private NewsRepository newsRepo;
    private FakeNewsProvider newsProvider; // <-- Изменено
    private NewsFeedGenerator feedGenerator;

    @BeforeEach
    void setupFullService() {
        // 1. Провайдер (фейковый)
        newsProvider = new FakeNewsProvider(); // <-- Используем фейк

        // 2. Репозитории
        userProfileRepo = new InMemoryUserProfileRepository();
        sessionRepo = new InMemorySessionRepository();
        newsRepo = new InMemoryNewsRepository(newsProvider); // <-- Передаем фейк

        // 3. Сервисы и движок
        // (Мы следуем ВАШЕЙ архитектуре со "смарт"-генератором)
        feedGenerator = new NewsFeedGenerator(userProfileRepo, sessionRepo, newsRepo);
        DialogueEngine engine = new DialogueEngine();
        NewsPreferenceService newsPrefs = new NewsPreferenceService(userProfileRepo);

        // 4. Команды (согласно ConsoleApp)
        BotCommand helpCommand = new HelpCommand();
        BotCommand availableCommand = new AvailableCommand(newsPrefs);
        BotCommand newsCommand = new NewsCommand(feedGenerator); // "Глупая" команда
        BotCommand categoryCommand = new CategoryCommand(newsPrefs, sessionRepo); // Новая команда
        BotCommand setCategoriesCommand = new SetCategoriesCommand(newsPrefs, sessionRepo);
        BotCommand changeUserCommand = new ChangeProfileCommand();
        BotCommand whoAmICommand = new WhoAmICommand();

        // 5. Резолвер
        CommandResolver commandResolver = new CommandResolver(setCategoriesCommand);
        commandResolver.register(helpCommand);
        commandResolver.register(availableCommand);
        commandResolver.register(newsCommand);
        commandResolver.register(categoryCommand); // <-- Регистрируем \category
        commandResolver.register(changeUserCommand);
        commandResolver.register(whoAmICommand);

        // 6. Сервис
        svc = new DialogueService(engine, sessionRepo, commandResolver);
    }

    @Test
    void showsOnboardingAtFirstEmptyInput() {
        BotResponse r = svc.handle("u", "");
        assertTrue(r.getMessage().startsWith("Начнём!"));
        assertFalse(r.getNewActiveUser().isPresent());
    }

    @Test
    void parsesCommaAndSemicolonSeparatedCategories() {
        svc.handle("u", ""); // start
        BotResponse r = svc.handle("u", "спорт; экономика, технологии");
        assertTrue(r.getMessage().contains("Запомнил категории"));


        BotResponse list = svc.handle("u", "\\category list");
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
    void categoryAddAndDelCommands() {
        svc.handle("u", ""); // start

        assertTrue(svc.handle("u", "\\category list").getMessage().contains("ни одной"));
        assertTrue(svc.handle("u", "\\category add спорт").getMessage().contains("Добавил"));
        assertTrue(svc.handle("u", "\\category list").getMessage().contains("спорт"));
        assertTrue(svc.handle("u", "\\category del спорт").getMessage().contains("Удалил"));
        assertTrue(svc.handle("u", "\\category list").getMessage().contains("ни одной"));
    }

    @Test
    void multiUserProfilesAreIndependent() {

        svc.handle("u1", "");
        svc.handle("u1", "\\category add спорт"); // Используем \category


        svc.handle("u2", "");
        BotResponse list2 = svc.handle("u2", "\\category list"); // Используем \category
        assertEquals("Ваши категории: пока не выбрано ни одной категории!", list2.getMessage());


        BotResponse list1 = svc.handle("u1", "\\category list"); // Используем \category
        assertTrue(list1.getMessage().contains("спорт"));
    }

    @Test
    void emptyInputAfterStartRepeatsOnboardingTip() {
        svc.handle("u", ""); // onboarding
        BotResponse r = svc.handle("u", ""); // empty again
        assertTrue(r.getMessage().toLowerCase().contains("новости о чём вас интересуют"));
    }

    @Test
    void getNewsCommandWorks() {
        // Настраиваем пользователя
        svc.handle("u1", ""); // start
        svc.handle("u1", "\\category add спорт"); // Добавляем категорию




        BotResponse r = svc.handle("u1", "\\news get");


        assertTrue(r.getMessage().contains("Спартак победил"));
        assertTrue(r.getMessage().contains("lenta.ru/sport1"));
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

        assertTrue(r.getMessage().contains("Текущий профиль: admin"));
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