package newsbot.news;

import java.util.Objects;
import java.util.Optional;

public enum NewsCategory {
    SPORT("спорт"),
    ECONOMY("экономика"),
    TECHNOLOGY("технологии"),
    POLITICS("политика"),
    CULTURE("культура");


    private final String name;

    NewsCategory(String name) {
        this.name = Objects.requireNonNull(name);
    }
    public String getName() {return name;}

    public static Optional<NewsCategory> parse(String s) {
        if (s == null) return Optional.empty();
        String norm = normalize(s);
        for (NewsCategory category : values()) {
            if (normalize(category.name).equals(norm)) return Optional.of(category);
        }
        return Optional.empty();
    }


    private static String normalize(String s) {
        return s.trim().toLowerCase();
    }
}
