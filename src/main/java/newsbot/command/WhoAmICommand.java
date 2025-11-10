package newsbot.command;

import newsbot.engine.BotResponse;
import newsbot.shared.UserId;

public class WhoAmICommand implements BotCommand {

    @Override
    public BotResponse execute(UserId userId, String args) {
        return BotResponse.say("Текущий профиль: " + userId.getValue());
    }
}