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
        for (NewsCategory cat : set) {
            lst.append(cat.getName()).append(" ");
        }
        return lst.toString();
    }

    public void add(UserId user, String cat) {

        NewsCategory.parse(cat).ifPresent(repoCat -> repo.addCategory(user, repoCat));
    }

    public void remove(UserId user, String cat) {
        NewsCategory.parse(cat).ifPresent(repoCat -> repo.removeCategory(user, repoCat));
    }

    public String available() {
        StringBuilder categories = new StringBuilder();
        for (NewsCategory category : NewsCategory.values()) {
            categories.append(category.getName()).append(" ");
        }
        return categories.toString();
    }


}
