package newsbot.news;


public record NewsStory(
        String title,
        String date,
        String author,
        String link,
        NewsCategory category,
        String description
) {
    public void consolePrintNews() {
        System.out.println(title);
        System.out.println(date);
        System.out.println(author);
        System.out.println(link);
        System.out.println(category.name());
        System.out.println(description);
        System.out.println();
    }
}