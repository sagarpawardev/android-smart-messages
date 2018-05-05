package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.AnalyticsUtil;
import dev.sagar.smsblocker.tech.utils.BroadcastUtilSingleton;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.NotificationUtilSingleton;
import dev.sagar.smsblocker.tech.utils.PermissionUtilSingleton;
import dev.sagar.smsblocker.tech.utils.PhoneUtilsSingleton;
import io.fabric.sdk.android.Fabric;

public class SMSReceivedReceiver extends BroadcastReceiver {

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
    public static final String EVENT_CODE = "even_sms_received";
    public static final String KEY_SMS = "sms";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String SMS_DELIVERED = "android.provider.Telephony.SMS_DELIVERED";
    private static final String SMS_SENT = "android.provider.Telephony.SMS_SENT";
    private static final String MSG_FORMAT = "3gpp";

    //public static final String KEY_SMS_RECEIVED = "key_sms_received";


    /**
     * This Method is Called When SMS is Received. Note:- This will be used by internal architecture
     * Instead use LocalSMSReceivedReceiver
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Start Log
        final String methodName = "onReceive()";
        log.justEntered(methodName);
        log.info(methodName, "Action Name: "+intent.getAction());
        AnalyticsUtil.start(context);


        String event = intent.getAction();
        switch (event) {
            case SMS_RECEIVED: smsReceived(context, intent); break;
            case SMS_DELIVERED: smsDelivered(context, intent); break;
            case SMS_SENT: smsSent(context, intent); break;
        }

        log.returning(methodName);
    }

    /**
     * This method is called if event is SMS_RECEIVED
     * @param context
     * @param intent
     */
    private void smsReceived(Context context, Intent intent){
        //Start Log
        final String methodName = "smsReceived()";
        log.justEntered(methodName);


        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            int slot = bundle.getInt("slot", -1);
            int subscription = bundle.getInt("subscription", -1);

            log.info(methodName, "Slot: "+slot);
            log.info(methodName, "Subscription: "+subscription);

            final SmsMessage[] messages = new SmsMessage[pdus.length];
            log.debug(methodName, String.format("message count = %s", messages.length));

            //Join Multi parts
            StringBuilder sbMessage = new StringBuilder();
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i], MSG_FORMAT);
                String msg = messages[i].getMessageBody();
                sbMessage.append(msg);
            }

            //Create an SMS body
            String msgBody = sbMessage.toString();
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])pdus[0], MSG_FORMAT);
            String address  = smsMessage.getOriginatingAddress();
            boolean isReplySupported = PhoneUtilsSingleton.getInstance().isReplySupported(address);

            SMS sms = new SMS();
            sms.setAddress(address);
            sms.setDateTime(smsMessage.getTimestampMillis());
            sms.setBody(msgBody);
            sms.setRead(false);
            sms.setType(SMS.TYPE_RECEIVED);
            sms.setSubscription(subscription);
            sms.setReplySupported(isReplySupported);

            log.debug(methodName, "Service Center: "+smsMessage.getServiceCenterAddress());

            log.info(methodName, "Received Message From: "+smsMessage.getDisplayOriginatingAddress());

            boolean isAppDefault = PermissionUtilSingleton.getInstance().isAppDefault(context);
            if(!isAppDefault) log.error(methodName, "App is not default");
            if(isAppDefault) {
                //Save SMS in DataProvider
                try {
                    log.info(methodName, "Saving SMS in DataProvider");
                    InboxUtil inboxUtil = new InboxUtil(context);
                    String id = inboxUtil.insertSMS(sms);
                    log.info(methodName, "Received Created id: " + id);
                    sms.setId(id);
                }
                catch (Exception e){
                    log.info(methodName, "Logging Error..");
                    log.error(methodName, e);
                    Toast.makeText(context, "Failed in Saving", Toast.LENGTH_SHORT).show();
                }
                

                try {
                    //Broadcast SMS Locally
                    log.info(methodName, "Broadcasting SMS");
                    broadcastLocalSMS(context, sms);
                }
                catch (Exception e){
                        log.info(methodName, "Logging Error..");
                        log.error(methodName, e);
                    Toast.makeText(context, "Failed in Broadcasting", Toast.LENGTH_SHORT).show();
                }


                //Create Notification
                try{
                    notifUtil.createSMSNotification(context, sms);
                }
                catch (Exception e){
                        log.info(methodName, "Logging Error..");
                        log.error(methodName, e);
                    Toast.makeText(context, "Failed in creating notification", Toast.LENGTH_SHORT).show();
                }
            }

        }

        log.returning(methodName);
    }


    /**
     * This method is called if event is SMS_DELIVERED
     * @param context
     * @param intent
     */
    private void smsDelivered(Context context, Intent intent){
        //Start Log
        final String methodName = "smsDelivered(Context, Intent)";
        log.justEntered(methodName);

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Toast.makeText(context, "Message Delivered.", Toast.LENGTH_SHORT);
        }
        log.returning(methodName);
    }

    private void smsSent(Context context, Intent intent){
        final String methodName = "smsDelivered(Context, Intent)";
        log.justEntered(methodName);
        //TODO SMS Sent Listener handler

        Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show();
        log.returning(methodName);
    }

    /**
     * This method broadcast SMS Locally
     * @param context
     */
    private void broadcastLocalSMS(Context context, SMS sms){
        Bundle basket = new Bundle();
        String jsonSMS = gson.toJson(sms);
        basket.putString(LocalSMSReceivedReceiver.KEY_SMS, jsonSMS);
        broadcastUtil.broadcast(context, EVENT_CODE, basket);
    }
}
