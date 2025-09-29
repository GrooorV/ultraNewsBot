package newsbot.console;

import newsbot.engine.DialogueService;

public class ConsoleAdapter {
    private DialogueService dialog;

    public ConsoleAdapter(DialogueService dialog) {
        this.dialog = dialog;
    }

    private void printIntro(String currentUser) {
        System.out.println("""
        Привет! Я новостной бот.
        
        Как пользоваться:
        - Укажи активного пользователя: \\changeuser ИМЯ
        
        Доступные команды:
           - \\help — показать эту справку
           - \\available — список доступных категорий
           - \\news list | add <категория> | del <категория> — управлять предпочтениями
           Можно просто перечислить категории через запятую, и я их запомню.
        """);
        }

    public void run() {
        String currentUser = "guest";
        printIntro(currentUser);
    }
}
