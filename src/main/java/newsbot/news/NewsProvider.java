package newsbot.news;
import java.util.List;

public interface NewsProvider {
    List<NewsStory> getLentaRuNews();
    //List<NewsStory> getRiaNews(); - на будущее
    //List<NewsStory> getTassNews(); - на будущее
}
