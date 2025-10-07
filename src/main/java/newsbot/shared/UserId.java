package newsbot.shared;

import java.util.Objects;

public class UserId {
    private String value;

    public UserId(String value) {
        this.value = Objects.requireNonNull(value, "userId can't be null");
    }

    public String getValue() { return value;}
}
