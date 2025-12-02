package newsbot.app;

import java.util.Scanner;

public class Main {
    public static void main(String [] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите какую версию бота запускать: ");
        System.out.println("1.ConsoleApp\n2.TelegramApp");
        while (true) {
            try {
                int ch = scanner.nextInt();
                scanner.nextLine();
                switch (ch) {
                    case 1 -> ConsoleApp.run();
                    case 2 -> TelegramApp.run(System.getenv("BOT_TOKEN"));
                    default -> System.out.println("Пожалуйста, введите вариант из предложенных");
                }
            } catch (Exception e) {
                System.out.println("Пожалуйста, введите число");
                scanner.nextLine();
            }
        }
    }
}
