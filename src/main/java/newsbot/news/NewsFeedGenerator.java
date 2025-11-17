package newsbot.news;
import newsbot.engine.UserSession;
import newsbot.shared.UserId;
import newsbot.repository.UserProfileRepository;
import newsbot.repository.SessionRepository;
import newsbot.repository.NewsRepository;

import java.time.Instant;
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

    public String getOneStory(UserId userId) {
        UserSession userSession = sessionRepo.getOrCreate(userId);
        if (userProfileRepo.categoriesAreEmpty(userId)) {
            return "Вы не выбрали ни одной категории. Выберите хотя бы одну, к примеру, экономика или спорт";
        }
        Set<NewsCategory> categories = userProfileRepo.getCategories(userId);
        if (!userSession.hasPendingNews()) {
            userSession.setPendingNews(newsRepo.getNewsByCategory(categories, Instant.now()));
            userSession.setLastNewsCheckTime(Instant.now());
        }

        List<String> links = userSession.getNextChunkIds(1);
        List<NewsStory> newsStories = newsRepo.getNewsByLinks(links);

        return BuildResponse(newsStories);
    }

    private String BuildResponse(List<NewsStory> newsStories) {
        StringBuilder response = new StringBuilder();
        for (NewsStory newsStory : newsStories) {
            response.append(newsStory.title())
                    .append("\nАвтор: ")
                    .append(newsStory.author())
                    .append("\n")
                    .append(newsStory.link())
                    .append("\n")
                    .append(newsStory.description())
                    .append("\n");
        }
        return response.toString();
    }
}
