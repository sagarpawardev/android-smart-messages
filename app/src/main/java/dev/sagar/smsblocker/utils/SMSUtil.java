package dev.sagar.smsblocker.utils;


import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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

        /*final String sSMSManagerIntentSENT = "package.DeliveryReport.SMS_SENT";
        ArrayList<String> parts = new ArrayList<>();
        parts.add(msg);
        int numParts = parts.size();
        ArrayList<PendingIntent> pendingIntents = new ArrayList<>();

        for (int i = 0; i < numParts; i++) {

            Intent pendingIntent = new Intent(sSMSManagerIntentSENT);
            //optional if you want to keep info about what action has been done for feedback or analysis later when message is sent
            pendingIntent.putExtra("package.DeliveryReport.phoneNumber", phoneNo); // receiver phoneNo
            pendingIntent.putExtra("package.DeliveryReport.textSMS", msg);// msg body
            pendingIntent.putExtra("SIM", simID); // which sim is sending this message

            pendingIntents.add(PendingIntent.getBroadcast(context, 0, pendingIntent,PendingIntent.FLAG_ONE_SHOT));
        }*/

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
