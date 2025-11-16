package newsbot.engine;

import java.util.Optional;

public class BotResponse {

    private final String message;
    private final Optional<String> newActiveUser;

    private BotResponse(String message, Optional<String> newActiveUser) {
        this.message = message;
        this.newActiveUser = newActiveUser;
    }

    public String getMessage() {
        return message;
    }

    public Optional<String> getNewActiveUser() {
        return newActiveUser;
    }

    public static BotResponse say(String message) {
        return new BotResponse(message, Optional.empty());
    }

    public static BotResponse switchUser(String message, String newUserId) {
        return new BotResponse(message, Optional.of(newUserId));
    }
}
