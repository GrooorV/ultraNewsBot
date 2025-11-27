package newsbot.console;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import java.util.ArrayList;
import java.util.List;

public class GeneralResponseButtons {
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public GeneralResponseButtons() {
        // создаем кнопки
        InlineKeyboardButton button1 = InlineKeyboardButton.builder()
                .text("Получить новости")
                .callbackData("\\news get")
                .build();

        InlineKeyboardButton button2 = InlineKeyboardButton.builder()
                .text("Доступно")
                .callbackData("\\available")
                .build();

        InlineKeyboardButton button3 = InlineKeyboardButton.builder()
                .text("Добавить категорию: политика")
                .callbackData("\\category add политика")
                .build();

        InlineKeyboardButton button4 = InlineKeyboardButton.builder()
                .text("Добавить категорию: спорт")
                .callbackData("\\category add спорт")
                .build();

        // строка для кнопок
        InlineKeyboardRow row1 = new InlineKeyboardRow();
        row1.add(button1);
        row1.add(button2);

        InlineKeyboardRow row2 = new InlineKeyboardRow();
        row2.add(button3);

        InlineKeyboardRow row3 = new InlineKeyboardRow();
        row3.add(button4);

        // список строк
        List<InlineKeyboardRow> rows = new ArrayList<>();
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        // Создаем клавиатуру кнопок
        this.inlineKeyboardMarkup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }


    public InlineKeyboardMarkup setMessageWithGeneralButtons() {
        return inlineKeyboardMarkup;
    }
}