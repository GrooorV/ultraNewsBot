package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.shared.UserId;

public class HelpCommand implements BotCommand {

    @Override
    public BotResponse execute(UserId userId, String args) {
        return BotResponse.say(
                """
            Я новостной бот.
            Команды:
            - \\help — показать эту справку
            - \\available — список доступных категорий
            - \\news list | add <категория> | del <категория> — управлять предпочтениями
            - \\whoami — показать текущий профиль
            - \\changeuser <имя> — сменить текущий профиль
            Можно просто перечислить категории через запятую, и я их запомню.
            """
        );
    }
}
