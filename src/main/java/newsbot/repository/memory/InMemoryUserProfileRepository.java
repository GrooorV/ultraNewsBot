package newsbot.repository.memory;

import newsbot.news.NewsCategory;
import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserProfileRepository implements UserProfileRepository {
    private final ConcurrentHashMap<String, Set<NewsCategory>> cats = new ConcurrentHashMap<>();

    @Override
    public Set<NewsCategory> getCategories(UserId userId) {
        Set<NewsCategory> set = cats.get(userId.getValue());
        return (set == null) ? Set.of() : Set.copyOf(set);
    }

    @Override
    public void addCategory(UserId userId, NewsCategory category) {
        String key = userId.getValue();

        cats.compute(key, (k, set) -> {
            if (set == null) set = EnumSet.noneOf(NewsCategory.class);
            set.add(category);
            return set;});
    }

    @Override
    public void removeCategory(UserId userId, NewsCategory category) {
         String key = userId.getValue();

        cats.computeIfPresent(key, (k, set) -> {
            set.remove(category);
            return set.isEmpty() ? null : set;
        });
    }
}
