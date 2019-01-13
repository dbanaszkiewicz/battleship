package data.manager;

import at.favre.lib.crypto.bcrypt.BCrypt;
import data.model.User;

import java.io.*;
import java.util.HashMap;

/**
 * 0 - login
 * 1 - hash
 * 2 - winned Games Counter
 * 3 - losted Games Counter
 * 4 - games
 */
public class UserDataManager {

    private static UserDataManager instance = null;

    private HashMap<String, User> allUsers;

    public static UserDataManager getInstance() {
        if (UserDataManager.instance == null) {
            UserDataManager.instance = new UserDataManager();
        }

        return UserDataManager.instance;
    }

    private UserDataManager() {
        this.readFile();
    }

    public void save(User user) {
        allUsers.replace(user.getLogin(), user);
        this.saveFile();
    }

    public void changeLogin(User user, String newLogin) {
        String oldlogin = user.getLogin();
        user.setLogin(newLogin);
        allUsers.replace(oldlogin, user);
        this.saveFile();
    }

    public User login(String login, String password) {
        if (this.allUsers.get(login) == null) {
            System.out.println("Użytkownik o takim loginie nie istnieje!");
            return null;
        } else {
            if (this.isValidPassrowd(this.allUsers.get(login), password)) {
                return this.allUsers.get(login);
            } else {
                System.out.println("Podane dane logowania są niepoprawne!");
                return null;
            }
        }
    }

    public boolean isValidPassrowd(User user, String password) {
        return BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
    }

    public String getHash(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public void register(String login, String password) {
        if (login.contains(":::")) {
            return;
        }
        if (this.allUsers.get(login) == null) {
            User user = new User();
            user.setLogin(login);
            user.setPassword(this.getHash(password));
            user.setLostedGames(0);
            user.setWinnedGames(0);
            this.allUsers.put(user.getLogin(), user);
            this.saveFile();
        }
    }

    public boolean isLoginInUse(String login) {
        return this.allUsers.containsKey(login);
    }

    private void readFile() {
        try {
            File file = new File("users.txt");
            // create file if not exists
            file.createNewFile();

            this.allUsers = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] userString = line.split(":::");
                User user = new User();
                user.setLogin(userString[0]);
                user.setPassword(userString[1]);
                user.setWinnedGames(Integer.parseInt(userString[2]));
                user.setLostedGames(Integer.parseInt(userString[3]));
                if (!userString[4].equals("0")) {
                    for (String game : userString[4].split(";")) {
                        user.addGame(game);
                    }
                }

                this.allUsers.put(user.getLogin(), user);
            }

            reader.close();
        } catch (Exception e) {
            System.out.println("Występił błąd podczas pobierania użytkowników z pliku...");
            System.exit(1);
        }
    }

    private void saveFile() {
        try {
            // Create file if not exist
            File file = new File("users.txt");
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            StringBuilder line = new StringBuilder();

            for (User user : allUsers.values()) {
                line.append(user.getLogin());
                line.append(":::");
                line.append(user.getPassword());
                line.append(":::");
                line.append(user.getWinnedGames());
                line.append(":::");
                line.append(user.getLostedGames());
                line.append(":::");
                for (String game : user.getGames()) {
                    line.append(game);
                    line.append(";");
                }
                if (user.getGames().size() == 0) {
                    line.append("0");
                }
                if (line.charAt(line.length()-1) == ';') {
                    writer.write(line.substring(0, line.length() - 1));
                } else {
                    writer.write(line.toString());
                }
                writer.newLine();
            }

            writer.close();
        } catch (Exception e) {
            System.out.println("Występił błąd podczas zapisywania użytkowników do pliku...");
            System.exit(1);
        }
    }
}
