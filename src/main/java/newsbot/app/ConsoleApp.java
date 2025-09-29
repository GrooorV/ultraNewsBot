package newsbot.app;

import newsbot.console.ConsoleAdapter;
import  newsbot.engine.DialogueService;
import newsbot.engine.DialogueEngine;
import newsbot.news.NewsPreference;
import newsbot.repository.memory.InMemorySessionRepository;
import newsbot.repository.memory.InMemoryUserProfileRepository;


public class ConsoleApp {
    public static void main(String[] args) {
        DialogueEngine engine = new DialogueEngine();
        NewsPreference newsPrefs = new NewsPreference(new InMemoryUserProfileRepository());

        DialogueService dialogueService = new DialogueService(engine, new InMemorySessionRepository(), newsPrefs);

        ConsoleAdapter console = new ConsoleAdapter(dialogueService);

        console.run();
    }
}
