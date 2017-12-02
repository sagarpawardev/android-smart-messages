package dev.sagar.smsblocker.tech.utils;


import android.app.PendingIntent;
import android.content.Context;
import android.telephony.SmsManager;

import java.util.ArrayList;

import dev.sagar.smsblocker.tech.beans.SMS;

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
    public SMS sendSMS(String phoneNo, String msg) {
        String methodName = "sendSMS()";
        log.info(methodName, "Just Entered...");

        final int REQUEST_CODE = 0;
        SMS sms = null;
        try {
            //Send SMS
            log.info(methodName,"Trying to Send SMS");
            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts =smsManager.divideMessage(msg);
            int numParts = parts.size();

            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();

            /*for (int i = 0; i < numParts; i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, REQUEST_CODE, null, 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, REQUEST_CODE, null, 0));
            }*/

            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);


            //smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            log.info(methodName, "==> SMS Sent");

            boolean isAppDefault = PermissionUtilSingleton.getInstance().isAppDefaultSMSApp(context);
            if(isAppDefault) {
                //Save in DataProvider
                sms = new SMS();
                sms.setRead(true);
                sms.setType(SMS.TYPE_SENT);
                sms.setDateTime(System.currentTimeMillis());
                sms.setBody(msg);
                sms.setFrom(phoneNo);
                InboxUtil inboxUtil = new InboxUtil(context);
                String id = inboxUtil.saveSMS(sms, InboxUtil.TYPE_SENT);
                sms.setId(id);
            }
        } catch (Exception e) {
            log.error(methodName, "==> SMS Sending Failed");
            e.printStackTrace();
        }

        log.info(methodName, "Returning..."+sms);
        return sms;
    }

}
