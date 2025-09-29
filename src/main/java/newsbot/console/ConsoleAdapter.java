package newsbot.console;

import newsbot.engine.DialogueService;
import java.util.Scanner;



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

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(currentUser + "> ");
            String line = sc.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println(dialog.handle(currentUser, ""));
                continue;
            }

            if (line.startsWith("\\changeuser")) {
                String[] parts = line.split("\\s+", 2);
                if (parts.length < 2 || parts[1].isBlank()) {
                    System.out.println("Использование: \\changeuser <userId>");
                    continue;
                }
                currentUser = parts[1].trim();
                System.out.println("Текущий пользователь: " + currentUser);
                System.out.println(dialog.handle(currentUser, ""));
                continue;
            }

            if (line.equalsIgnoreCase("\\whoami")) {
                System.out.println("Текущий пользователь: " + currentUser);
                continue;
            }

            String bot = dialog.handle(currentUser, line);
            System.out.println(bot);
        }
    }
}
