package data.manager;

import data.model.Game;
import data.model.Map;
import data.model.Step;
import data.model.User;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * date_id - file name
 * 2 - winned Games Counter
 * 3 - losted Games Counter
 * 4 - last Game Id
 */
public class GameDataManager {

    private static GameDataManager instance = null;

    public static GameDataManager getInstance() {
        if (GameDataManager.instance == null) {
            GameDataManager.instance = new GameDataManager();
        }

        return GameDataManager.instance;
    }

    private GameDataManager() {
    }

    public Game getGame(String name) throws Exception {
        return this.readGame(name);
    }

    public Game createGame(Map userMap, User user, boolean userStart) {
        Game game = new Game();
        game.setUserMap(userMap);
        game.setOpponentMap(new Map());
        game.setId(user.getGames().size() + 1);
        game.setDate(new SimpleDateFormat("yyyy-MM-dd_H-m-s.S").format(new Date()));
        game.setUserNext(userStart);

        user.addGame(user.getLogin() + "_" + game.getDate() + "_" + game.getId());

        UserDataManager.getInstance().save(user);

        this.saveNewGame(game, user);
        return game;
    }

    private Game readGame(String filename) throws Exception {
        Game game = new Game();
        File file = new File(filename + ".txt");

        if (!file.exists()) {
            throw new Exception("FileNotExists");
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            String[] filenameStringArray = filename.split("_");
            game.setDate(filenameStringArray[1] + "_" + filenameStringArray[2]);
            game.setId(Integer.parseInt(filenameStringArray[3]));
            game.setOpponentMap(new Map());

            line = reader.readLine();
            String[] mapString = line.split(":::");
            Map map = new Map();
            for (int i = 10; i < 110; i++) {
                map.setFieldValue(Map.mapIntToString(i), mapString[i - 9].charAt(0));
            }

            if (reader.readLine().equals("next:::true")) {
                game.setUserNext(true);
            } else {
                game.setUserNext(false);
            }

            game.setUserMap(map);

            while ((line = reader.readLine()) != null) {

                String[] stepString = line.split(":::");
                if (stepString[0].equals("step")) {
                    Step step = new Step();
                    step.setField(stepString[1]);
                    step.setHit(stepString[2].equals("1"));
                    step.setSunk(stepString[3].equals("1"));
                    step.setUserStep(game.isUserNext());

                    game.addStep(step);
                    if (!step.isHit()) {
                        game.setUserNext(!game.isUserNext());
                    }
                }
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("Występił błąd podczas pobierania danych gry z pliku...");
            System.exit(1);
        }

        return game;
    }

    public void addStep(Game game, User user, String field, boolean hit, boolean sunk) {
        try {
            File file = new File(user.getLogin() + "_" + game.getDate() + "_" + game.getId() + ".txt");

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            String line = "step:::" + field + ":::" + (hit ? "1" : "0") + ":::" + (sunk ? "1" : "0");
            writer.write(line);
            writer.newLine();

            writer.close();
        } catch (Exception e) {
            System.out.println("Występił błąd podczas zapisywania danych gry...");
            System.exit(1);
        }
    }

    private void saveNewGame(Game game, User user) {
        try {
            // Create file if not exist
            File file = new File(user.getLogin() + "_" + game.getDate() + "_" + game.getId() + ".txt");
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            StringBuilder line = new StringBuilder().append("info:::");

            for (int i = 10; i < 110; i++) {
                line.append(game.getUserMap().getFieldValue(Map.mapIntToString(i)));
                line.append(":::");
            }

            writer.write(line.toString());
            writer.newLine();
            if (game.isUserNext()) {
                writer.write("next:::true");
            } else {
                writer.write("next:::false");
            }
            writer.newLine();

            writer.close();
        } catch (Exception e) {
            System.out.println("Występił błąd podczas zapisywania danych gry...");
            System.exit(1);
        }
    }
}
