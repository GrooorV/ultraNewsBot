package newsbot.repository.memory;

import newsbot.repository.SessionRepository;
import newsbot.shared.UserId;
import newsbot.engine.Session;


import java.util.concurrent.ConcurrentHashMap;


public class InMemorySessionRepository implements SessionRepository {
    private final ConcurrentHashMap<String, Session> map = new ConcurrentHashMap<>();

    @Override
    public Session getOrCreate(UserId userId) {
        return map.computeIfAbsent(userId.getValue(), _ -> new Session());
    }

    @Override
    public void save(UserId userId, Session session) {
        map.put(userId.getValue(), session);
    }
}
