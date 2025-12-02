package newsbot.news;
import newsbot.engine.UserSession;
import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.NewsRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NewsFeedGenerator {
    private final UserProfileRepository userProfileRepo;
    private final SessionRepository sessionRepo;
    private final NewsRepository newsRepo;

    public NewsFeedGenerator(UserProfileRepository userProfileRepo,
                             SessionRepository sessionRepo,
                             NewsRepository newsRepo)
    {
        this.userProfileRepo = userProfileRepo;
        this.sessionRepo = sessionRepo;
        this.newsRepo = newsRepo;
    }

    public List<NewsStory> getOneStory(UserId userId) {
        UserSession userSession = sessionRepo.getOrCreate(userId);
        if (userProfileRepo.categoriesAreEmpty(userId)) {
            return new ArrayList<>();
        }
        Set<NewsCategory> categories = userProfileRepo.getCategories(userId);

        if (!userSession.hasPendingNews()) {

            Instant since = userSession.getLastNewsCheckTime();
            if (since == null) {
                since = Instant.now().minus(24, java.time.temporal.ChronoUnit.HOURS);
            }

            List<NewsStory> freshNews = newsRepo.getNewsByCategory(categories, since);

            userSession.setLastNewsCheckTime(Instant.now());

            if (freshNews.isEmpty()) {
                sessionRepo.save(userId, userSession); // Сохраняем обновленное время
                return new ArrayList<NewsStory>();
            }
            userSession.setPendingNews(freshNews);
        }

        List<String> links = userSession.getNextChunkIds(1);
        List<NewsStory> newsStories = newsRepo.getNewsByLinks(links);

        sessionRepo.save(userId, userSession);

        return newsStories;
    }
}
