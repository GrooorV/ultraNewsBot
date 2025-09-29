package newsbot.repository.memory;

import newsbot.news.NewsCategory;
import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserProfileRepository implements UserProfileRepository {
    private final Map<String, Set<NewsCategory>> cats = new ConcurrentHashMap<>();

    @Override
    public Set<NewsCategory> getCategories(UserId userId) {
        return Collections.unmodifiableSet(cats.getOrDefault(userId.getValue(), new HashSet<>()));
    }

    @Override
    public void addCategory(UserId userId, NewsCategory category) {
        cats.computeIfAbsent(userId.getValue(), k -> new HashSet<>()).add(category);
    }

    @Override
    public void removeCategory(UserId userId, NewsCategory category) {
        cats.computeIfPresent(userId.getValue(), (k, v) -> { v.remove(category); return v; });
    }
}
