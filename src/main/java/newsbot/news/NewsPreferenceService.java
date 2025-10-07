package newsbot.news;

import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.Set;


public class NewsPreferenceService {
    private final UserProfileRepository repo;

    // фиксированный список доступных категорий
    public NewsPreferenceService(UserProfileRepository repo) {
        this.repo = repo;
    }

    public String list(UserId user) {
        Set<NewsCategory> set = repo.getCategories(user);
        if (set.isEmpty()) {
            return "пока не выбрано ни одной категории!";
        }

        StringBuilder lst = new StringBuilder();
        for (NewsCategory category : set) {
            lst.append(category.getName()).append(" ");
        }
        return lst.toString();
    }

    public void add(UserId user, String categoryName) {

        NewsCategory.parse(categoryName).ifPresent(repoCategory -> repo.addCategory(user, repoCategory));
    }

    public void remove(UserId user, String categoryName) {
        NewsCategory.parse(categoryName).ifPresent(repoCategory -> repo.removeCategory(user, repoCategory));
    }

    public String available() {
        StringBuilder categories = new StringBuilder();
        for (NewsCategory category : NewsCategory.values()) {
            categories.append(category.getName()).append(" ");
        }
        return categories.toString();
    }


}
