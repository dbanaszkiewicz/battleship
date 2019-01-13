package data.model;

import java.util.ArrayList;

public class User {
    private String login;
    private String password;
    private int winnedGames;
    private int lostedGames;
    private ArrayList<String> games;


    public User() {
        this.games = new ArrayList<>();
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWinnedGames() {
        return winnedGames;
    }

    public void setWinnedGames(int winnedGames) {
        this.winnedGames = winnedGames;
    }

    public int getLostedGames() {
        return lostedGames;
    }

    public void setLostedGames(int lostedGames) {
        this.lostedGames = lostedGames;
    }

    public ArrayList<String> getGames() {
        return games;
    }

    public void addGame(String gameName) {
        this.games.add(gameName);
    }
}
