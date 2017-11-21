package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.google.gson.Gson;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.BroadcastUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.NotificationUtilSingleton;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;

public class SMSReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil( this.getClass().getName() );

    //Internal References
    private final SmsManager sms = SmsManager.getDefault();
    private final NotificationUtilSingleton notifUtil = NotificationUtilSingleton.getInstance();

    //Java Android
    private final Gson gson = new Gson();

    //Java Core
    private final BroadcastUtilSingleton broadcastUtil = BroadcastUtilSingleton.getInstance();

    //Constants
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVERED = "android.provider.Telephony.SMS_DELIVER";
    private static final String MSG_FORMAT = "3gpp";
    public static final String LOCAL_SMS_RECEIVED = "smsblocker.event.LOCAL_SMS_RECEIVED";
    public static final String KEY_SMS_RECEIVED = "key_sms_received";


    /**
     * This Method is Called When SMS is Received. Note:- This will be used by internal architecture
     * Instead use LocalSMSReceiver
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Start Log
        final String methodName = "onReceive()";
        log.info(methodName, "Just Entered..");
        log.info(methodName, "Action Name: "+intent.getAction());

        String event = intent.getAction();
        switch (event) {
            case SMS_RECEIVED: smsReceived(context, intent); break;
            case SMS_DELIVERED: smsDelivered(context, intent); break;
        }

        log.info(methodName, "Returning..");
    }

    /**
     * This method is called if event is SMS_RECEIVED
     * @param context
     * @param intent
     */
    private void smsReceived(Context context, Intent intent){
        //Start Log
        final String methodName = "smsReceived()";
        log.verbose(methodName, "Just Entered..");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[])bundle.get("pdus");
            for (Object pdu: pdus) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[])pdu, MSG_FORMAT);
                SMS sms = new SMS();
                sms.setBody(msg.getMessageBody());
                sms.setFrom(msg.getOriginatingAddress());
                sms.setDateTime(msg.getTimestampMillis());
                sms.setRead(false);
                sms.setType(SMS.TYPE_RECEIVED);

                log.info(methodName, "Received Message From: "+msg.getDisplayOriginatingAddress());

                boolean isAppDefault = PermissionUtilSingleton.getInstance().isAppDefaultSMSApp(context);
                if(!isAppDefault) log.error(methodName, "App is not default");
                if(isAppDefault) {
                    //Save SMS in DataProvider
                    log.info(methodName, "Saving SMS in DataProvider");
                    InboxUtil inboxUtil = new InboxUtil(context);
                    String id = inboxUtil.saveSMS(sms, InboxUtil.TYPE_INBOX);
                    log.info(methodName, "Received Created id: " + id);
                    sms.setId(id);

                    //Broadcast SMS Locally
                    log.info(methodName, "Broadcasting SMS");
                    broadcastLocalSMS(context, sms);

                    //Create Notification
                    notifUtil.createSMSNotification(context, sms);
                }
            }
        }
    }


    /**
     * This method is called if event is SMS_DELIVERED
     * @param context
     * @param intent
     */
    private void smsDelivered(Context context, Intent intent){
        //Start Log
        final String methodName = "smsDelivered()";
        log.verbose(methodName, "Just Entered..");

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Toast.makeText(context, "Message Delivered.", Toast.LENGTH_SHORT);
        }
    }

    /**
     * This method broadcast SMS Locally
     * @param context
     */
    private void broadcastLocalSMS(Context context, SMS sms){
        Bundle basket = new Bundle();
        String jsonSMS = gson.toJson(sms);
        basket.putString(KEY_SMS_RECEIVED, jsonSMS);
        broadcastUtil.broadcast(context, LOCAL_SMS_RECEIVED, basket);
    }
}
