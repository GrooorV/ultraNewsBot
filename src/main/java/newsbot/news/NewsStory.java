package newsbot.news;


public record NewsStory(
        String title,
        String date,
        String author,
        String link,
        NewsCategory category,
        String description
) {
}