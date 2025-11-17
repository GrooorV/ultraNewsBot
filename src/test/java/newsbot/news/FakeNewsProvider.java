package newsbot.news;

import java.util.ArrayList;
import java.util.List;

/**
 * "Фейковый" провайдер, который не ходит в сеть.
 * Используется в DialogueServiceTest.
 */
public class FakeNewsProvider implements NewsProvider {

    // --- Тестовые данные определены прямо здесь ---
    private static final NewsStory SPORT_STORY_1 = new NewsStory(
            "Спартак победил", "date", "author", "lenta.ru/sport1",
            NewsCategory.SPORT, "Спартак победил Динамо");

    private static final NewsStory ECON_STORY_1 = new NewsStory(
            "Доллар вырос", "date", "author", "lenta.ru/econ1",
            NewsCategory.ECONOMY, "Доллар снова 100");

    private static final NewsStory TECH_STORY_1 = new NewsStory(
            "Вышел новый Java", "date", "author", "lenta.ru/tech1",
            NewsCategory.TECHNOLOGY, "Java 40");


    private List<NewsStory> storiesToReturn = new ArrayList<>();

    public FakeNewsProvider() {

        this.storiesToReturn.addAll(List.of(
                SPORT_STORY_1,
                ECON_STORY_1,
                TECH_STORY_1
        ));
    }

    public void setStoriesToReturn(List<NewsStory> stories) {
        this.storiesToReturn = stories;
    }

    @Override
    public List<NewsStory> getNews() {
        return this.storiesToReturn;
    }
}