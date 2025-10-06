package newsbot.repository;

import newsbot.engine.UserSession;
import newsbot.shared.UserId;

public interface SessionRepository {
    UserSession getOrCreate(UserId userId);
    void save(UserId userId, UserSession session);
}
