package newsbot.news;
import java.util.List;

public interface NewsProvider {
    List<NewsStory> getNews();
}
