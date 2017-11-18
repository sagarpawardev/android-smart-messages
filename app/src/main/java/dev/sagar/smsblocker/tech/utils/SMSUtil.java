package dev.sagar.smsblocker.tech.utils;


import android.content.Context;
import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by sagarpawar on 22/10/17.
 */

public class SMSUtil {

    //Log Initiate
    private static LogUtil log = new LogUtil( "ContactUtilSingleton" );

    //Java Android
    private Context context;

    //Java Core


    public SMSUtil(Context context) {
        this.context = context;
    }


    /**
     * This method sends sms to specified contact number
     * @param phoneNo
     * @param msg
     */
    public void sendSMS(String phoneNo, String msg) {
        String methodName = "sendSMS()";
        log.info(methodName, "Just Entered...");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            log.info(methodName, "==> SMS Sent");
        } catch (Exception e) {
            log.error(methodName, "==> SMS Sending Failed");
            e.printStackTrace();
        }
        log.info(methodName, "Returning...");
    }

}
