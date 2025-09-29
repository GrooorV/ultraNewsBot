package newsbot.engine;

import newsbot.news.NewsPreference;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;

public class DialogueService {
    private DialogueEngine engine;
    private SessionRepository sessions;
    private NewsPreference newsPrefs;


    public  DialogueService(DialogueEngine engine, SessionRepository sessions, NewsPreference newsPrefs) {
        this.engine = engine;
    }

    public String handle(String rawUserId, String input) {
        UserId userId = new UserId(rawUserId);
        UserSession session = sessions.getOrCreate(userId);

        return engine.onboardText();
    }
}
