package newsbot.repository.memory;

import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;
import newsbot.engine.UserSession;


import java.util.concurrent.ConcurrentHashMap;


public class InMemorySessionRepository implements SessionRepository {
    private final ConcurrentHashMap<String, UserSession> map = new ConcurrentHashMap<>();

    @Override
    public UserSession getOrCreate(UserId userId) {
        return map.computeIfAbsent(userId.getValue(), k -> new UserSession());
    }

    @Override
    public void save(UserId userId, UserSession userSession) {
        map.put(userId.getValue(), userSession);
    }
}
