package newsbot.news;

import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;

import java.util.Set;

public class NewsPreference {
    private final UserProfileRepository repo;

    public NewsPreference(UserProfileRepository repo) {
        this.repo = repo;
    }

    public String list(UserId user) {
        Set<NewsCategory> set = repo.getCategories(user);
        String lst = "";
        for (NewsCategory cat : set) {
            lst += cat.name();
        }
        return lst;
    }

    public void add(UserId user, String cat) {
        repo.addCategory(user, new NewsCategory(cat));
    }

    public void remove(UserId user, String cat) {
        repo.removeCategory(user, new NewsCategory(cat));
    }
}
