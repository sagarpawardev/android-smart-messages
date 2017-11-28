package dev.sagar.smsblocker.tech.exceptions;

/**
 * Created by sagarpawar on 28/11/17.
 */

public class NoContactPictureException extends Exception {
    private String contactName;

    public NoContactPictureException(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public String getMessage() {
        String message = contactName+" does not have any Profile picture";
        return message;
    }
}
