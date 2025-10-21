package newsbot.engine;

public class DialogueEngine {
    public String onboardText() {
        return "Я новостной бот. Скажите, новости о чём вас интересуют? Например: спорт, экономика";
    }

    public boolean isHelp(String input) {
        return "\\help".equalsIgnoreCase(input);
    }

    public boolean isLikeCategoryInput(String input) {
        return input != null && !input.isBlank() && !input.startsWith("\\");
    }
}
