package newsbot.news;

import java.util.Objects;

public class NewsCategory {
    private String name;

    public NewsCategory(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String name() {
        return name;
    }
}
