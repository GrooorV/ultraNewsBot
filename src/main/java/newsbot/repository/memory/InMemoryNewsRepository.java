package newsbot.repository.memory;

import newsbot.news.NewsCategory;
import newsbot.news.NewsProvider;
import newsbot.news.NewsStory;
import newsbot.repository.NewsRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class InMemoryNewsRepository implements NewsRepository {

    private final NewsProvider provider;
    private final Duration cacheDuration;

    private record CachedStory(
            NewsStory story,
            Instant fetchedAt
    ) {}



    private List<CachedStory> cacheList;

    private Map<String, NewsStory> cacheMap;

    private Instant lastFetchTime;
    private final ReentrantLock lock = new ReentrantLock(); // Для потокобезопасности

    public InMemoryNewsRepository(NewsProvider provider, Duration cacheDuration) {
        this.provider = provider;
        this.cacheDuration = cacheDuration;
        this.cacheList = new ArrayList<>();
        this.cacheMap = new HashMap<>();
        this.lastFetchTime = null;
    }

    public InMemoryNewsRepository(NewsProvider provider) {
        this(provider, Duration.ofMinutes(10));
    }


    private void refreshCacheIfNeeded() {
        if (lastFetchTime != null && Instant.now().isBefore(lastFetchTime.plus(cacheDuration))) {
            return;
        }

        // Проверка: другой поток уже обновляет?
        if (lock.tryLock()) {
            try {

                if (lastFetchTime != null && Instant.now().isBefore(lastFetchTime.plus(cacheDuration))) {
                    return;
                }

                //System.out.println("[NewsRepository] Кэш устарел. Запрос к NewsProvider...");


                List<NewsStory> cleanNews = provider.getNews();
                Instant fetchTime = Instant.now();


                this.cacheList = cleanNews.stream()
                        .map(story -> new CachedStory(story, fetchTime))
                        .toList();


                this.cacheMap = cleanNews.stream()
                        .collect(Collectors.toMap(
                                NewsStory::link,   // Ключ - ссылка
                                story -> story,    // Значение - сам объект
                                (existing, replacement) -> existing // На случай дубликатов ссылок
                        ));

                this.lastFetchTime = fetchTime;
                System.out.println("[NewsRepository] Кэш обновлен. " + this.cacheList.size() + " записей.");

            } catch (Exception e) {
                System.err.println("Не удалось обновить кэш: " + e.getMessage());
                this.lastFetchTime = Instant.now().minus(cacheDuration).plus(Duration.ofMinutes(1));
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public List<NewsStory> getNewsByCategory(Set<NewsCategory> categories, Instant since) {
        refreshCacheIfNeeded();


        List<CachedStory> currentCache = this.cacheList;

        return currentCache.stream()
                .filter(cachedStory -> categories.contains(cachedStory.story().category()))
                .filter(cachedStory -> cachedStory.fetchedAt().isAfter(since))
                .map(CachedStory::story)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsStory> getNewsByLinks(List<String> links) {

        List<NewsStory> results = new ArrayList<>();


        for (String link : links) {
            NewsStory story = this.cacheMap.get(link);
            if (story != null) {
                results.add(story);
            } else {
                // Логируем, если ссылка из сессии не найдена в кэше
                //System.err.println("Новость из сессии не найдена в кэше: " + link);
            }
        }
        return results;
    }
}