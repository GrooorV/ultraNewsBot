package newsbot.repository.database;

import newsbot.news.NewsCategory;
import newsbot.repository.UserProfileRepository;
import newsbot.shared.UserId;

import java.util.Set;

public class DatabaseUserProfileRepository  implements UserProfileRepository {
    @Override
    public boolean categoriesAreEmpty(UserId userId) {
        return false;
    }

    @Override
    public Set<NewsCategory> getCategories(UserId userId) {
        return Set.of();
    }

    @Override
    public void addCategory(UserId userId, NewsCategory newsCategory) {

    }

    @Override
    public void removeCategory(UserId userId, NewsCategory newsCategory) {

    }
}
