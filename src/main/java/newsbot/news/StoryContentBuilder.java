package newsbot.news;

import newsbot.command.resolver.FormatResolver;
import java.util.List;

public class StoryContentBuilder {
    public static String getStory(List<NewsStory> newsStories, FormatResolver format) {
        if (newsStories.isEmpty()) {
            return "Упс, видимо нет свежих новостей или же вы не выбрали категорий.";
        }

        StringBuilder response = new StringBuilder();
        for (NewsStory newsStory : newsStories) {
            response.append(format.imageLinkWarn(newsStory.pictureLink()))
                    .append('\n')
                    .append(format.bold(newsStory.title()))
                    .append("\n\n")
                    .append(newsStory.description())
                    .append("\n\n")
                    .append("Автор: ")
                    .append(format.italic(newsStory.author()))
                    .append("\n")
                    .append(newsStory.link())
                    .append("\n");
        }
        return response.toString();
    }
}
