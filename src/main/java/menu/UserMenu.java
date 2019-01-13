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

public class UserMenu {
    static void displayMenu(User user) {
        ConsoleHelper.clear();

        Game lastGame = UserMenu.getLastGame(user);

        System.out.println("MENU:");
        System.out.println("1 - Nowa gra");
        if (lastGame != null) {
            System.out.println("2 - Kontynuuj grę");
        }
        System.out.println("4 - Obejrzyj ponownie gry");
        System.out.println("5 - Profil użytkownika");
        System.out.println("0 - Wyjdź z gry");
        System.out.println();
        System.out.println();

        while (true) {
            System.out.print("Podaj opcję: ");

            Scanner in = new Scanner(System.in);
            int opt = ConsoleHelper.parseToInt(in.nextLine());
            if (opt == 1) {
                UserMenu.startNewGame(user);
            } else if (opt == 2 && lastGame != null) {
                Map.displayMaps(lastGame.getUserMap(), lastGame.getOpponentMap());
                UserMenu.playGame(lastGame, user);
            } else if (opt == 4) {
                UserMenu.gameListMenu(user);
                break;
            } else if (opt == 5) {
                ProfileMenu.displayMenu(user);
                break;
            } else if (opt == 0) {
                System.exit(0);
            }

            System.out.println("Podana opcja jest nieprawidłowa!");
        }
    }

    private static Game getLastGame(User user) {
        if (user.getGames().size() > 0) {
            String last = user.getGames().get(user.getGames().size() - 1);
            Game game;
            try {
                game = GameDataManager.getInstance().getGame(last);
            } catch (Exception e) {
                return null;
            }

            UserMenu.parseSteps(game);
            if (!game.isEnded()) {
                return game;
            }
        }

        return null;
    }

    private static void startNewGame(User user) {
        Map map = Creator.createUserMap();

        System.out.println("Kto rozpocznie grę?");
        System.out.println("1 - Ja");
        System.out.println("2 - Przeciwnik");
        System.out.println();

        boolean userStart;
        while (true) {
            System.out.print("Podaj opcję: ");

            Scanner in = new Scanner(System.in);
            int opt = ConsoleHelper.parseToInt(in.nextLine());
            if (opt == 1) {
                userStart = true;
                break;
            } else if (opt == 2) {
                userStart = false;
                break;
            }

            System.out.println("Podana opcja jest nieprawidłowa!");
        }

        data.model.Game game = GameDataManager.getInstance().createGame(map, user, userStart);
        Map.displayMaps(game.getUserMap(), game.getOpponentMap());
        UserMenu.playGame(game, user);
    }

    private static void parseSteps(Game game) {
        for (Step step : game.getSteps()) {
            if (step.isUserStep()) {
                game.getOpponentMap().shot(step.getField(), step.isHit(), step.isSunk());
                if (game.getOpponentMap().isEndGame()) {
                    game.setEnded(true);
                    return;
                }
            } else {
                game.getUserMap().shot(step.getField(), step.isHit(), step.isSunk());
                if (game.getUserMap().isEndGame()) {
                    game.setEnded(true);
                    return;
                }
            }
        }
    }

    private static void playGame(Game game, User user) {
        if (game.isUserNext()) {
            System.out.println("Teraz twoja kolej!");
            System.out.print("Podaj pole na mapie przeciwnika: ");
            String field;
            while (true) {
                Scanner in = new Scanner(System.in);
                field = in.nextLine();
                if (Map.isValidField(field) && game.getOpponentMap().canBeShot(field)) {
                    break;
                }
                System.out.print("Nie możesz celować w to pole! Podaj inne współrzędne: ");
            }

            boolean isHit = ConsoleHelper.scanBooleanValue("Czy oktręt został trafiony?");
            boolean isSunk = ConsoleHelper.scanBooleanValue("Czy oktręt został zatopiony?");

            game.getOpponentMap().shot(field, isHit, isSunk);
            GameDataManager.getInstance().addStep(game, user, field, isHit, isSunk);

            if (!isHit) {
                game.setUserNext(!game.isUserNext());
            }

            Map.displayMaps(game.getUserMap(), game.getOpponentMap());
        } else {
            System.out.println("Teraz kolej przeciwnika!");
            System.out.print("Które pole wybrał przeciwnik?: ");
            String field;
            while (true) {
                Scanner in = new Scanner(System.in);
                field = in.nextLine();
                if (Map.isValidField(field) && game.getUserMap().canBeShot(field)) {
                    break;
                }
                System.out.print("Przeciwnik nie może celować w to pole! Podaj inne współrzędne: ");
            }

            Character state = game.getUserMap().shotToUserMap(field);

            boolean isHit = state.equals(Map.HIT) || state.equals(Map.SUNK);
            boolean isSunk = false;
            if (isHit) {
                isSunk = state.equals(Map.SUNK);
            }

            Map.displayMaps(game.getUserMap(), game.getOpponentMap());

            if (isHit && !isSunk) {
                System.out.println("Przeciwnik trafił w twój okręt!");
            }

            if (isSunk) {
                System.out.println("Przeciwnik zatopił w twój okręt!");
            }

            if (!isHit) {
                System.out.println("Przeciwnik nie trafił w żaden okręt!");
            }

            GameDataManager.getInstance().addStep(game, user, field, isHit, isSunk);

            if (!isHit) {
                game.setUserNext(!game.isUserNext());
            }
        }

        if (game.getUserMap().isEndGame() || game.getOpponentMap().isEndGame()) {

            System.out.println("Gra została zakończona!");

            if (game.getOpponentMap().isEndGame()) {
                System.out.println("Wygrałeś, gratulacje!");
                user.setWinnedGames(user.getWinnedGames() + 1);
                UserDataManager.getInstance().save(user);
            } else {
                System.out.println("Niestety, przegrałeś tą rozgrywkę...");
                user.setLostedGames(user.getLostedGames() + 1);
                UserDataManager.getInstance().save(user);
            }

            game.setEnded(true);

            ConsoleHelper.waitForEnter("Naciśnij enter, aby przejść do menu użytkownika!");
            UserMenu.displayMenu(user);
        } else {
            UserMenu.playGame(game, user);
        }
    }

    private static void gameListMenu(User user) {
        System.out.println("Lista ostatnich gier:");

        HashMap<String, String> games = new HashMap<>();

        for (String game : user.getGames()) {
            String[] info = game.split("_");
            games.put(info[3], game);
            System.out.println(info[3] + ". Gra z dnia: " + info[1] + " godz. " + info[2]);
        }

        System.out.println();
        System.out.println("Wybierz grę, którą chcesz obejrzeć (podaj numer, lub 0 aby powrócić do menu): ");

        String g;
        while (true) {
            Scanner in = new Scanner(System.in);
            g = in.nextLine();
            if (games.containsKey(g) || g.equals("0")) {
                break;
            }
            System.out.print("Podaj poprawny numer gry (lub 0 aby wrócić do menu): ");
        }

        if (g.equals("0")) {
            UserMenu.displayMenu(user);
        } else {
            try {
                UserMenu.showGame(games.get(g), user);
            } catch (Exception e) {
                // To się nie wydarzy!
            }
        }
    }

    public static void showGame(String gameName, User user) throws Exception {

        Game game;
        try {
            game = GameDataManager.getInstance().getGame(gameName);
        } catch (Exception e) {
            if (user != null) {
                ConsoleHelper.waitForEnter("Błąd.. Taki plik nie istnieje! Naciśnij enter...");
                UserMenu.displayMenu(user);
                return;
            } else {
                throw e;
            }
        }

        Map.displayMaps(game.getUserMap(), game.getOpponentMap());
        ConsoleHelper.waitForEnter("Oto mapa początkowa. Naciśnij enter, aby zobaczyć następny krok...");

        for (Step step : game.getSteps()) {
            if (step.isUserStep()) {
                game.getOpponentMap().shot(step.getField(), step.isHit(), step.isSunk());
                Map.displayMaps(game.getUserMap(), game.getOpponentMap());
                System.out.println("Użytkownik celował w pole " + step.getField());
                if (step.isHit() && !step.isSunk()) {
                    System.out.println("Statek został trafiony!");
                } else if (step.isHit() && step.isSunk()) {
                    System.out.println("Trafiony zatopiony!");
                } else {
                    System.out.println("Pudło...");
                }
            } else {
                game.getUserMap().shot(step.getField(), step.isHit(), step.isSunk());
                Map.displayMaps(game.getUserMap(), game.getOpponentMap());
                System.out.println("Przeciwnik celował w pole " + step.getField());
                if (step.isHit() && !step.isSunk()) {
                    System.out.println("Statek został trafiony!");
                } else if (step.isHit() && step.isSunk()) {
                    System.out.println("Trafiony zatopiony!");
                } else {
                    System.out.println("Pudło...");
                }
            }

            if (!game.getUserMap().isEndGame() && !game.getOpponentMap().isEndGame()) {
                ConsoleHelper.waitForEnter("Naciśnij enter, aby zobaczyć następny krok...");
            }
        }

        if (game.getUserMap().isEndGame() || game.getOpponentMap().isEndGame()) {
            game.setEnded(true);

            if (game.getUserMap().isEndGame()) {
                System.out.println("Przeciwnik wygrał grę!");
            } else {
                System.out.println("Użytkownik wygrał grę!");
            }
        }

        System.out.println();
        System.out.println();

        if (user == null) {
            ConsoleHelper.waitForEnter("Naciśnij enter, aby zakończyć...");
        } else {
            if (!game.isEnded()) {
                System.out.println("Ta rozgrywka nie została zakończona. Co chcesz teraz zrobić?");
                System.out.println("1 - Wróć do menu");
                System.out.println("2 - Dokończ grę");
                System.out.println();

                while (true) {
                    System.out.print("Podaj opcję: ");

                    Scanner in = new Scanner(System.in);
                    int opt = ConsoleHelper.parseToInt(in.nextLine());
                    if (opt == 1) {
                        UserMenu.displayMenu(user);
                        break;
                    } else if (opt == 2) {
                        Map.displayMaps(game.getUserMap(), game.getOpponentMap());
                        UserMenu.playGame(game, user);
                        break;
                    }

                    System.out.println("Podana opcja jest nieprawidłowa!");
                }
            } else {
                ConsoleHelper.waitForEnter("Naciśnij enter, aby przejść do menu...");
                UserMenu.displayMenu(user);
            }
        }
    }
}
