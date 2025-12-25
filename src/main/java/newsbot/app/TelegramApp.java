package newsbot.app;

import newsbot.command.resolver.FormatResolver;
import newsbot.ui.GeneralResponseButtons;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import newsbot.adapter.TelegramAdapter;

import newsbot.engine.DialogueService;
import newsbot.engine.DialogueEngine;
import newsbot.network.DataFetcher;
import newsbot.network.HttpDataFetcher;
import newsbot.news.*;
import newsbot.repository.NewsRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.database.*;


// Импорты команд
import newsbot.command.*;
import newsbot.command.resolver.CommandResolver;

public class TelegramApp {
    public static void run(String BOT_TOKEN) throws TelegramApiException {
        DataFetcher fetcher = new HttpDataFetcher();
        LentaRssParser  parser = new LentaRssParser(fetcher);

        NewsProvider newsProvider = new LentaNewsProvider(fetcher, parser);


        DatabaseInitializer dbInitializer = new DatabaseInitializer();
        dbInitializer.init();

        UserProfileRepository userProfileRepo = new DatabaseUserProfileRepository();
        SessionRepository sessionRepo = new DatabaseSessionRepository();
        NewsRepository newsRepo = new DatabaseNewsRepository(newsProvider);

        NewsFeedGenerator feedGenerator = new NewsFeedGenerator(userProfileRepo, sessionRepo, newsRepo);

        DialogueEngine engine = new DialogueEngine();
        NewsPreferenceService newsPrefs = new NewsPreferenceService(userProfileRepo);

        BotCommand helpCommand = new HelpCommand();
        BotCommand availableCommand = new AvailableCommand(newsPrefs);
        BotCommand newsCommand = new NewsCommand(feedGenerator, new FormatResolver(FormatResolver.OutputMode.TELEGRAM));
        BotCommand categoryCommand = new CategoryCommand(newsPrefs, sessionRepo);
        BotCommand setCategoriesCommand = new SetCategoriesCommand(newsPrefs, sessionRepo);

        CommandResolver commandResolver = new CommandResolver(setCategoriesCommand);

        commandResolver.register(helpCommand);
        commandResolver.register(availableCommand);
        commandResolver.register(newsCommand);
        commandResolver.register(categoryCommand);

        DialogueService dialogueService = new DialogueService(engine, sessionRepo, commandResolver);
        GeneralResponseButtons generalResponseButtons = new GeneralResponseButtons();
        TelegramAdapter telegramAdapter = new TelegramAdapter(BOT_TOKEN,
                dialogueService,
                generalResponseButtons,
                userProfileRepo);
        TelegramBotsLongPollingApplication app = new TelegramBotsLongPollingApplication();
        app.registerBot(BOT_TOKEN, telegramAdapter);
    }
}
