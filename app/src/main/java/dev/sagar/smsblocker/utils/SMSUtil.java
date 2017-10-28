package dev.sagar.smsblocker.utils;


import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;


import dev.sagar.smsblocker.beans.SimOperator;

/**
 * Created by sagarpawar on 22/10/17.
 */

public class SMSUtil {

    private static final String CLASS_NAME = "SMSUtil";
    private Context context;

    public SMSUtil(Context context) {
        this.context = context;
    }

    public void sendSMS(String phoneNo, String msg, String simID) {
        final String METHOD_NAME = "sendSMS()";
        Log.e(CLASS_NAME, "==> Inside "+METHOD_NAME);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Log.e(CLASS_NAME, "SMS Sent...");
        } catch (Exception e) {
            Log.e(CLASS_NAME, "SMS Sending Failed...");
            e.printStackTrace();
        }

        Log.e(CLASS_NAME, "==> Returning From "+METHOD_NAME);
    }

}
