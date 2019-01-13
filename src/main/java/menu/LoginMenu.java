package menu;

import console.ConsoleHelper;
import data.manager.UserDataManager;
import data.model.User;

import java.util.Scanner;

public class LoginMenu {
    public static void displayMenu() {
        System.out.println("Witaj w grze Battleship!");
        System.out.println();
        System.out.println("1 - Logowanie");
        System.out.println("2 - Rejestracja");
        System.out.println("0 - Wyjdź z gry");

        while (true) {
            System.out.print("Podaj opcję: ");

            Scanner in = new Scanner(System.in);
            int opt = ConsoleHelper.parseToInt(in.nextLine());
            if (opt == 1) {
                LoginMenu.login();
                break;
            } else if (opt == 2) {
                LoginMenu.register();
                break;
            } else if (opt == 0) {
                System.exit(0);
            }

            System.out.println("Podana opcja jest nieprawidłowa!");
        }
    }

    private static void register() {
        Scanner in = new Scanner(System.in);
        ConsoleHelper.clear();
        System.out.println("Panel rejestracji nowego konta");

        String login;
        String password;
            while (true) {
                System.out.print("Podaj login: ");
                login = in.next();

                if (login.length() < 3) {
                    System.out.println("Podany login jest za krótki. Wprowadź inny login!");
                    continue;
                }

                if (login.contains(":::")) {
                    System.out.println("Login nie może zawierać ciągu znaków \":::\"!");
                    continue;
                }

                if (UserDataManager.getInstance().isLoginInUse(login)) {
                    System.out.println("Podany login jest już zajęty. Wprowadź inny login!");
                    continue;
                }
                break;
            }
            while (true) {
                password = ConsoleHelper.readPassword("Podaj hasło");

                if (password.length() < 3) {
                    System.out.println("Podane hasło jest za krótkie. Wprowadź inne hasło!");
                    continue;
                }

                String password2 = ConsoleHelper.readPassword("Potwierdź hasło");
                if (!password.equals(password2)) {
                    System.out.println("Podane hasła są różne! Wprowadź hasła ponownie!");
                    continue;
                }
                break;
            }

            UserDataManager.getInstance().register(login, password);
            System.out.println("Twoje konto zostało zarejestrowane. Naciśnij [Enter] aby wrócić do menu gry.");
            in.nextLine();
            LoginMenu.displayMenu();
    }

    private static void login() {
        Scanner in = new Scanner(System.in);
        ConsoleHelper.clear();
        System.out.println("Panel logowania");

        String login;
        String password;
        int count = 0;
        while (true) {
            System.out.print("Podaj login: ");
            login = in.next();
            password = ConsoleHelper.readPassword("Podaj hasło");



            User user = UserDataManager.getInstance().login(login, password);

            if (user != null) {
                System.out.println("Zostałeś zalogowany. Naciśnij [Enter] aby przejść do menu użytkownika.");
                in.nextLine();
                UserMenu.displayMenu(user);
                break;
            } else {
                count++;
                if (count >= 3) {
                    LoginMenu.displayMenu();
                }
            }
        }
    }
}
