package newsbot.console;

import newsbot.engine.BotResponse;
import newsbot.engine.DialogueService;
import java.util.Scanner;



public class ConsoleAdapter {
    private final DialogueService dialog;
    private String currentUser;

    public ConsoleAdapter(DialogueService dialog) {
        this.dialog = dialog;
        this.currentUser = "guest";
    }

    private void printIntro() {
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
        printIntro();

        BotResponse welcome = dialog.handle(this.currentUser, "");
        System.out.println(welcome.getMessage());

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print(this.currentUser + "> ");
            String line = sc.nextLine().strip();

            BotResponse response = dialog.handle(this.currentUser, line);

            System.out.println(response.getMessage());

            response.getNewActiveUser().ifPresent(newUserId -> {
                String oldUser = this.currentUser;
                this.currentUser = newUserId;

                if (!oldUser.equals(newUserId)) {
                    BotResponse userWelcome = dialog.handle(this.currentUser, "");
                    System.out.println(userWelcome.getMessage());
                }
            });
        }
    }
}
