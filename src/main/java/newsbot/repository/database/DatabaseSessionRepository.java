package newsbot.repository.database;

import newsbot.engine.UserSession;
import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;

public class DatabaseSessionRepository implements SessionRepository {
    @Override
    public UserSession getOrCreate(UserId userId) {
        return null;
    }

    @Override
    public void save(UserId userId, UserSession session) {

    }
}
