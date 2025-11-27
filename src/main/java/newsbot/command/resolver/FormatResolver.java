package newsbot.command.resolver;

//format class for better output
public class FormatResolver {
    private final OutputMode mode;

    public FormatResolver(OutputMode mode) {
        this.mode = mode;
    }

    public String bold(String message) {
        return switch (mode) {
            case TELEGRAM -> "<b>" + message + "</b>";
            case CONSOLE -> "\u001B[1m" + message + "\u001B[0m";
        };
    }

    public String italic(String message) {
        return switch (mode) {
            case CONSOLE -> "\u001B[3m" + message + "\u001B[0m";
            case TELEGRAM -> "<i>" + message + "</i>";
        };
    }

    public enum OutputMode {
        TELEGRAM,
        CONSOLE
    }

}
