package data.model;

import java.util.ArrayList;

public class Game {

    private int id;
    private String date;
    private Map userMap;
    private Map opponentMap;
    private ArrayList<Step> steps;
    private boolean isEnded;
    private boolean userNext;

    public Game() {
        this.isEnded = false;
        this.steps = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map getUserMap() {
        return userMap;
    }

    public void setUserMap(Map userMap) {
        this.userMap = userMap;
    }

    public Map getOpponentMap() {
        return opponentMap;
    }

    public void setOpponentMap(Map opponentMap) {
        this.opponentMap = opponentMap;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public void addStep(Step step) {
        this.steps.add(step);
    }

    public boolean isUserNext() {
        return userNext;
    }

    public void setUserNext(boolean userNext) {
        this.userNext = userNext;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }
}
