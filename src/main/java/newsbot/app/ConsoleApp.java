package newsbot.app;

import newsbot.console.ConsoleAdapter;
import newsbot.engine.DialogueService;
import newsbot.engine.DialogueEngine;
import newsbot.news.NewsPreferenceService;
import newsbot.repository.SessionRepository;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;

// Импорты команд
import newsbot.command.*;
import newsbot.command.resolver.CommandResolver;


public class ConsoleApp {
    public static void main(String[] args) {
        UserProfileRepository userProfileRepo = new InMemoryUserProfileRepository();
        SessionRepository sessionRepo = new InMemorySessionRepository();

        DialogueEngine engine = new DialogueEngine();
        NewsPreferenceService newsPrefs = new NewsPreferenceService(userProfileRepo);

        BotCommand helpCommand = new HelpCommand();
        BotCommand availableCommand = new AvailableCommand(newsPrefs);
        BotCommand newsCommand = new NewsCommand(newsPrefs);
        BotCommand setCategoriesCommand = new SetCategoriesCommand(newsPrefs);
        BotCommand changeUserCommand = new ChangeProfileCommand();
        BotCommand whoAmICommand = new WhoAmICommand();

        CommandResolver commandResolver = new CommandResolver(setCategoriesCommand);

        commandResolver.register("\\help", helpCommand);
        commandResolver.register("\\available", availableCommand);
        commandResolver.register("\\news", newsCommand);
        commandResolver.register("\\changeuser", changeUserCommand);
        commandResolver.register("\\whoami", whoAmICommand);

        DialogueService dialogueService = new DialogueService(engine, sessionRepo, commandResolver);

        ConsoleAdapter console = new ConsoleAdapter(dialogueService);
        console.run();
    }
}