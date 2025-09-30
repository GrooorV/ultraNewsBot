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
        StringBuilder lst = new StringBuilder();
        for (NewsCategory cat : set) {
            lst.append(cat.name());
        }
        return lst.toString();
    }

    public void add(UserId user, String cat) {
        repo.addCategory(user, new NewsCategory(cat));
    }

    public void remove(UserId user, String cat) {
        repo.removeCategory(user, new NewsCategory(cat));
    }
}
