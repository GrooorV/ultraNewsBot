package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.shared.UserId;

public class ChangeProfileCommand implements BotCommand {

    @Override
    public BotResponse execute(UserId userId, String args) {
        String newUserId = args.trim();

        if (newUserId.isBlank()) {
            return BotResponse.say("Использование: \\changeuser <userId>");
        }

        return BotResponse.switchUser(
                "Текущий профиль: " + newUserId,
                newUserId
        );
    }
}