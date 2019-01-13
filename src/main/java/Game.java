import console.ConsoleHelper;
import menu.LoginMenu;
import menu.UserMenu;

public class Game {

    public static void main(String[] args) {

        if (args.length > 0) {
            try {
                UserMenu.showGame(args[0], null);
                return;
            } catch (Exception e) {
                ConsoleHelper.waitForEnter("Nie udało się otworzyć pliku z danymi... Naciśnij enter, aby przejść do menu gry.");
            }
        }

        LoginMenu.displayMenu();

    }
}

