package newsbot.repository.memory;

import newsbot.news.NewsCategory;
import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserProfileRepository implements UserProfileRepository {
    private final ConcurrentHashMap<String, Set<NewsCategory>> categories = new ConcurrentHashMap<>();

    @Override
    public Set<NewsCategory> getCategories(UserId userId) {
        Set<NewsCategory> set = categories.get(userId.getValue());
        return (set == null) ? Set.of() : Set.copyOf(set);
    }

    @Override
    public void addCategory(UserId userId, NewsCategory category) {
        String key = userId.getValue();

        categories.computeIfAbsent(key, _ -> EnumSet.noneOf(NewsCategory.class)).add(category);
    }

    @Override
    public void removeCategory(UserId userId, NewsCategory category) {
         String key = userId.getValue();


        categories.computeIfPresent(key, (ignored, set) -> {
            set.remove(category);
            return set.isEmpty() ? null : set;
        });
    }
}
