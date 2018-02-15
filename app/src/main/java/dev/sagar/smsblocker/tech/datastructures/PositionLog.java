package dev.sagar.smsblocker.tech.datastructures;

/**
 * Created by sagarpawar on 07/02/18.
 */

public class PositionLog {
    private int oldPosition;
    private int newPosition;

    public PositionLog(int oldPosition, int newPosition){
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public int getNewPosition() {
        return newPosition;
    }
}
