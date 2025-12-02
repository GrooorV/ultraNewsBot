package newsbot.repository;

import newsbot.news.NewsCategory;
import newsbot.news.NewsStory;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface NewsRepository {
    List<NewsStory> getNewsByCategory(Set<NewsCategory> categories, Instant since);

    List<NewsStory> getNewsByLinks(List<String> links);
}
