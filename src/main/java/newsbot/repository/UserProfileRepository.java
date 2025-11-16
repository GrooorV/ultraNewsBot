package newsbot.repository;

import newsbot.news.NewsCategory;
import newsbot.shared.UserId;

import  java.util.Set;

public interface UserProfileRepository {
    Set<NewsCategory>  getCategories(UserId userId);
    void addCategory(UserId userId, NewsCategory newsCategory);
    void removeCategory(UserId userId, NewsCategory newsCategory);
}
