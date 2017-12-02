package dev.sagar.smsblocker.tech.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import dev.sagar.smsblocker.tech.beans.SMS;

/**
 * Created by sagarpawar on 02/12/17.
 */

public class SystemUtilSingleton {
    private static final SystemUtilSingleton ourInstance = new SystemUtilSingleton();

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Constants
    private static final String SIMPLE_TEXT = "simple text";
    private static final String CLIPBOARD_SERVICE = Context.CLIPBOARD_SERVICE;

    public static SystemUtilSingleton getInstance() {
        return ourInstance;
    }

    private SystemUtilSingleton() {
    }

    public void copy(Context context, SMS sms){
        String methodName = "copy()";
        log.info(methodName, "Just Entered..");

        String textToCopy = sms.getBody();

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(SIMPLE_TEXT, textToCopy);
        clipboard.setPrimaryClip(clip);

        log.info(methodName, "Returned..");
    }
}
