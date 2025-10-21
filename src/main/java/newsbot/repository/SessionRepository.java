package newsbot.repository;

import newsbot.engine.Session;
import newsbot.shared.UserId;

public interface SessionRepository {
    Session getOrCreate(UserId userId);
    void save(UserId userId, Session session);
}
