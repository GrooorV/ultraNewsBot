package newsbot.repository.database;

import newsbot.news.NewsCategory;
import newsbot.news.NewsStory;
import newsbot.repository.NewsRepository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class DatabaseNewsRepository implements NewsRepository {
    @Override
    public List<NewsStory> getNewsByCategory(Set<NewsCategory> categories, Instant since) {
        return List.of();
    }

    @Override
    public List<NewsStory> getNewsByLinks(List<String> links) {
        return List.of();
    }
}
