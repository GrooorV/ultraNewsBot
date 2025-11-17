package newsbot.shared;

import newsbot.engine.UserSession;

import java.util.Objects;

public class UserId {
    private String value;
    private UserSession userSession;

    public UserId(String value) {
        this.value = Objects.requireNonNull(value, "userId can't be null");
    }

    public UserSession getSession() { return userSession; };
    public String getValue() { return value;}
}
