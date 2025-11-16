package newsbot.news;

public record NewsStory(
        String title,
        String date,
        String author,
        String link,
        String category,
        String description
) {
    public void consolePrintNews() {
        System.out.println(title);
        System.out.println(date);
        System.out.println(author);
        System.out.println(link);
        System.out.println(category);
        System.out.println(description);
        System.out.println();
    }
}