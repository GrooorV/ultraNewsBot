package newsbot.command;


import newsbot.engine.BotResponse;
import newsbot.shared.UserId;

/**
 * Интерфейс для всех команд бота, принимает ID и строку от пользователя, возвращает ответ
 */
public interface BotCommand {

    BotResponse execute(UserId userId, String args);
}
