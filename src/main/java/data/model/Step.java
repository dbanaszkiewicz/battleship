package data.model;

public class Step {
    private String field;
    private boolean isHit;
    private boolean isSunk;
    private boolean isUserStep;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk(boolean sunk) {
        isSunk = sunk;
    }

    public boolean isUserStep() {
        return isUserStep;
    }

    public void setUserStep(boolean userStep) {
        isUserStep = userStep;
    }
}
