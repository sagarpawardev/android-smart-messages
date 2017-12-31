package dev.sagar.smsblocker.tech.utils;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

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
        log.justEntered(methodName);

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

            log.error(methodName,"Need to add sentIntent and deliveryIntent Here");

            smsManager.sendMultipartTextMessage(phoneNo, null, parts, null, null);


            //smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            log.info(methodName, "==> SMS Sent");

            boolean isAppDefault = PermissionUtilSingleton.getInstance().isAppDefault(context);
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

        log.returning(methodName);
        return sms;
    }

    /**
     * Returns SMSManager for specified sim
     * @param simIndex Index of SIM 0 for Sim1 and 1 for Sim2
     * @return SMSManager for specified index or Default if SDK < 22
     */
    private SmsManager getSMSManagerFor(int simIndex){
        String methodName = "sendSMS()";
        log.justEntered(methodName);

        SmsManager smsManager = null;

        if (Build.VERSION.SDK_INT >= 22) {
            SubscriptionManager subscriptionManager = context.getSystemService(SubscriptionManager.class);
            log.info(methodName, "Looking for SMS manager for simIndex: "+simIndex);
            SubscriptionInfo subscriptionInfo=subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simIndex);
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
            log.info(methodName, "SIM found ");
        }
        else{
            log.info(methodName, "SDK Lower than 22 so returning default SMSManager");
            smsManager = SmsManager.getDefault();
        }

        log.justEntered(methodName);
        return smsManager;
    }

}
