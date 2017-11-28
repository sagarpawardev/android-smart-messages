package dev.sagar.smsblocker.tech.exceptions;

/**
 * Created by sagarpawar on 28/11/17.
 */

public class NoSuchContactException extends Exception {
    private String contactName;

    public NoSuchContactException(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String getMessage() {
        String message = contactName + " Contact Does not exist";
        return super.getMessage();
    }
}
