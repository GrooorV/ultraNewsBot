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

            Instant since = userSession.getLastNewsCheckTime();
            if (since == null) {
                since = Instant.now().minus(24, java.time.temporal.ChronoUnit.HOURS);
            }

            List<NewsStory> freshNews = newsRepo.getNewsByCategory(categories, since);

            userSession.setLastNewsCheckTime(Instant.now());

            if (freshNews.isEmpty()) {
                sessionRepo.save(userId, userSession); // Сохраняем обновленное время
                return "Новых новостей по вашим категориям нет.";
            }
            userSession.setPendingNews(freshNews);
        }

        List<String> links = userSession.getNextChunkIds(1);
        List<NewsStory> newsStories = newsRepo.getNewsByLinks(links);

        sessionRepo.save(userId, userSession);

        return BuildResponse(newsStories);
    }

    private String BuildResponse(List<NewsStory> newsStories) {
        StringBuilder response = new StringBuilder();
        for (NewsStory newsStory : newsStories) {
            response.append(newsStory.title())
                    .append("\n\n")
                    .append(newsStory.description())
                    .append("\n\n")
                    .append("Автор: ")
                    .append(newsStory.author())
                    .append("\n")
                    .append(newsStory.link())
                    .append("\n");
        }
        return response.toString();
    }
}
