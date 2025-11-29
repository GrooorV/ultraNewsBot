package newsbot.console;
import newsbot.news.NewsCategory;
import newsbot.repository.UserProfileRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneralResponseButtons {

    private InlineKeyboardButton createCategoryButton(NewsCategory category, Set<NewsCategory> categorySet) {
        boolean isSelected = categorySet.contains(category);
        String categoryName = "";

        switch (category) {
            case POLITICS:
                categoryName = "Политика";
                break;
            case SPORT:
                categoryName = "Спорт";
                break;
            case ECONOMY:
                categoryName = "Экономика";
                break;
            case CULTURE:
                categoryName = "Культура";
                break;
            case TECHNOLOGY:
                categoryName = "Технологии";
                break;
            case OTHER:
                categoryName = "Прочее";
                break;
        }

        String text = isSelected ? categoryName + " ✅" : categoryName + " ❌";
        String callbackData = isSelected ? "\\category del " + categoryName : "\\category add " + categoryName;

        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackData)
                .build();
    }

    public InlineKeyboardMarkup getMessageWithGeneralButtons(Set<NewsCategory> CategorySet) {
        // создаем кнопки
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Получить новости")
                .callbackData("\\news get")
                .build();

        //InlineKeyboardButton button2 = InlineKeyboardButton.builder() - пока без помощи
                //.text("Помощь")
                //.callbackData("\\help")
                //.build();

        InlineKeyboardButton button3 = createCategoryButton(NewsCategory.POLITICS, CategorySet);
        InlineKeyboardButton button4 = createCategoryButton(NewsCategory.SPORT, CategorySet);
        InlineKeyboardButton button5 = createCategoryButton(NewsCategory.ECONOMY, CategorySet);
        InlineKeyboardButton button6 = createCategoryButton(NewsCategory.CULTURE, CategorySet);
        InlineKeyboardButton button7 = createCategoryButton(NewsCategory.TECHNOLOGY, CategorySet);
        InlineKeyboardButton button8 = createCategoryButton(NewsCategory.OTHER, CategorySet);

        // строка для кнопок
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(button1);
        //row1.add(button2); - пока без помощи

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(button3);
        row2.add(button4);

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(button5);
        row3.add(button6);

        InlineKeyboardRow row4 = new InlineKeyboardRow();
        row4.add(button7);
        row4.add(button8);

        // список строк
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        // Создаем клавиатуру кнопок
        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }
}
