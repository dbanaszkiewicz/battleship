package menu;


import console.ConsoleHelper;
import creator.Creator;
import data.manager.GameDataManager;
import data.manager.UserDataManager;
import data.model.Game;
import data.model.Map;
import data.model.Step;
import data.model.User;

import java.util.HashMap;
import java.util.Scanner;

public class ProfileMenu {
    static void displayMenu(User user) {
        ConsoleHelper.clear();

        System.out.println("Profil użytkownika:");
        System.out.println("1 - Statystyki użytkownika");
        System.out.println("4 - Zmień login");
        System.out.println("5 - Zmień hasło");
        System.out.println("0 - Powrót do menu");
        System.out.println();
        System.out.println();

        while (true) {
            System.out.print("Podaj opcję: ");

            Scanner in = new Scanner(System.in);
            int opt = ConsoleHelper.parseToInt(in.nextLine());
            if (opt == 1) {
                ProfileMenu.displayUserStats(user);
            } else if (opt == 4) {
                ProfileMenu.changeLogin(user);
                break;
            } else if (opt == 5) {
                ProfileMenu.changePassword(user);
                break;
            } else if (opt == 0) {
                UserMenu.displayMenu(user);
                break;
            }

            System.out.println("Podana opcja jest nieprawidłowa!");
        }
    }

    private static void displayUserStats(User user) {
        ConsoleHelper.clear();
        System.out.println("Login: " + user.getLogin());
        System.out.println("Wygrane gry: " + user.getWinnedGames());
        System.out.println("Przegrane gry: " + user.getLostedGames());
        System.out.println("Zakończone gry: " + (user.getWinnedGames() + user.getLostedGames()));

        System.out.println();
        System.out.println("Naciśnij [Enter] aby wrócić do menu użytkownika.");
        new Scanner(System.in).nextLine();
        ProfileMenu.displayMenu(user);
    }

    private static void changePassword(User user) {
        Scanner in = new Scanner(System.in);
        ConsoleHelper.clear();
        System.out.println("Zmiana hasła użytkownika");
        System.out.println();
        System.out.print("Podaj aktualne hasło: ");
        String oldPassword = in.next();
        String newPassword;
        while (true) {
            System.out.print("Podaj nowe hasło: ");
            newPassword = in.next();

            if (newPassword.length() < 3) {
                System.out.println("Podane hasło jest za krótkie. Wprowadź inne hasło!");
                continue;
            }

            System.out.print("Potwierdź nowe hasło: ");
            String password2 = in.next();
            if (!newPassword.equals(password2)) {
                System.out.println("Podane hasła są różne! Wprowadź hasła ponownie!");
                continue;
            }
            break;
        }

        if (UserDataManager.getInstance().isValidPassrowd(user, oldPassword)) {
            user.setPassword(UserDataManager.getInstance().getHash(newPassword));
            UserDataManager.getInstance().save(user);

            ConsoleHelper.waitForEnter("Hasło zostało zmienione. Naciśnij enter, aby wrócić do menu...");
            ProfileMenu.displayMenu(user);
        } else {
            ConsoleHelper.waitForEnter("Podane hasło jest niepoprawne. Naciśnij enter, aby wrócić do menu...");
            ProfileMenu.displayMenu(user);
        }
    }

    private static void changeLogin(User user) {
        Scanner in = new Scanner(System.in);
        ConsoleHelper.clear();
        System.out.println("Zmiana hasła użytkownika");
        System.out.println();
        System.out.print("Podaj aktualne hasło: ");
        String oldPassword = in.next();
        String login;
        while (true) {
            System.out.print("Podaj nowy login: ");
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

            System.out.print("Potwierdź nowy login: ");
            String login2 = in.next();
            if (!login.equals(login2)) {
                System.out.println("Podane loginy są różne! Spróbuj ponownie!");
                continue;
            }
            break;
        }

        if (UserDataManager.getInstance().isValidPassrowd(user, oldPassword)) {
            UserDataManager.getInstance().changeLogin(user, login);

            ConsoleHelper.waitForEnter("Login został zmieniony. Naciśnij enter, aby wrócić do menu...");
            ProfileMenu.displayMenu(user);
        } else {
            ConsoleHelper.waitForEnter("Podane hasło jest niepoprawne. Naciśnij enter, aby wrócić do menu...");
            ProfileMenu.displayMenu(user);
        }
    }
}
