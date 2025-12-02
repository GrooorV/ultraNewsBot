package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.shared.UserId;

public class HelpCommand implements BotCommand {

    @Override
    public String getName() {
        return "\\help";
    }

    @Override
    public BotResponse execute(UserId userId, String args) {
        return BotResponse.say(
                """
            Я новостной бот.
            Команды:
            - \\help — показать эту справку
            - \\available — список доступных категорий
            - \\category [list | add <имя> | del <имя>] — управление категориями
            - \\news [get] — получить новость по вашим категориям
            - \\whoami — показать текущий профиль
            - \\changeuser <имя> — сменить текущий профиль
            Можно просто перечислить категории через запятую (спорт, экономика), и я их запомню.
            """
        );
    }
}
