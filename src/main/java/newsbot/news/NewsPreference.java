package newsbot.news;

import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.Set;
import java.util.stream.Collectors;

public class NewsPreference {
    private final UserProfileRepository repo;

    public NewsPreference(UserProfileRepository repo) {
        this.repo = repo;
    }

    public String list(UserId user) {
        Set<NewsCategory> set = repo.getCategories(user);
        return set.stream().map(NewsCategory::toString).collect(Collectors.toList()).toString();
    }

    public void add(UserId user, String cat) {
        repo.addCategory(user, new NewsCategory(cat));
    }

    public void remove(UserId user, String cat) {
        repo.removeCategory(user, new NewsCategory(cat));
    }
}
