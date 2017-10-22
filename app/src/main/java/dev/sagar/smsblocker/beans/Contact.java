package dev.sagar.smsblocker.beans;

/**
 * Created by sagarpawar on 22/10/17.
 */

public class Contact {

    private String id,
    displayName,
    number;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
