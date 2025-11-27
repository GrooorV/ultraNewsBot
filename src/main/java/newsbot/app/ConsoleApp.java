package newsbot.app;

import newsbot.console.ConsoleAdapter;
import newsbot.engine.DialogueService;
import newsbot.engine.DialogueEngine;
import newsbot.network.DataFetcher;
import newsbot.network.HttpDataFetcher;
import newsbot.news.*;
import newsbot.repository.NewsRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.memory.InMemoryNewsRepository;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;

// Импорты команд
import newsbot.command.*;
import newsbot.command.resolver.CommandResolver;


public class ConsoleApp {
    public static void run() {
        DataFetcher fetcher = new HttpDataFetcher();
        LentaRssParser  parser = new LentaRssParser(fetcher);

        NewsProvider newsProvider = new LentaNewsProvider(fetcher, parser);

        UserProfileRepository userProfileRepo = new InMemoryUserProfileRepository();
        SessionRepository sessionRepo = new InMemorySessionRepository();
        NewsRepository newsRepo = new InMemoryNewsRepository(newsProvider);

        NewsFeedGenerator feedGenerator = new NewsFeedGenerator(userProfileRepo, sessionRepo, newsRepo);

        DialogueEngine engine = new DialogueEngine();
        NewsPreferenceService newsPrefs = new NewsPreferenceService(userProfileRepo);

        BotCommand helpCommand = new HelpCommand();
        BotCommand availableCommand = new AvailableCommand(newsPrefs);
        BotCommand newsCommand = new NewsCommand(feedGenerator);
        BotCommand categoryCommand = new CategoryCommand(newsPrefs, sessionRepo);
        BotCommand setCategoriesCommand = new SetCategoriesCommand(newsPrefs, sessionRepo);
        BotCommand changeUserCommand = new ChangeProfileCommand();
        BotCommand whoAmICommand = new WhoAmICommand();

        CommandResolver commandResolver = new CommandResolver(setCategoriesCommand);

        commandResolver.register(helpCommand);
        commandResolver.register(availableCommand);
        commandResolver.register(newsCommand);
        commandResolver.register(categoryCommand);
        commandResolver.register(changeUserCommand);
        commandResolver.register(whoAmICommand);

        DialogueService dialogueService = new DialogueService(engine, sessionRepo, commandResolver);

        ConsoleAdapter console = new ConsoleAdapter(dialogueService);
        console.run();
    }
}