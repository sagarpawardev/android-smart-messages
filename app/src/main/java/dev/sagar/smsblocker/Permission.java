package dev.sagar.smsblocker;

import android.Manifest;

/**
 * Created by sagarpawar on 30/12/17.
 */

public class Permission {

    public final static String[] ALL = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };

    public final static String READ_CONTACTS = Manifest.permission.READ_CONTACTS;
    public final static String READ_SMS = Manifest.permission.READ_SMS;
    public final static String SEND_SMS = Manifest.permission.SEND_SMS;
    public final static String RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
}
