package newsbot.news;

import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NewsPreferenceService {
    private final UserProfileRepository repo;

    // фиксированный список доступных категорий
    private final List<NewsCategory> availableCategories = List.of(
            new NewsCategory("спорт"),
            new NewsCategory("экономика"),
            new NewsCategory("технологии"),
            new NewsCategory("политика"),
            new NewsCategory("культура")
    );

    public NewsPreferenceService(UserProfileRepository repo) {
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

    public String available() {
        return availableCategories.stream()
                .map(NewsCategory::toString)
                .collect(Collectors.toList())
                .toString();
    }
}
