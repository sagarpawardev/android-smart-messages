package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.BroadcastUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.tech.utils.NotificationUtilSingleton;

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
    private static final String MSG_FORMAT = "3gpp";
    public static final String LOCAL_SMS_RECEIVED = "smsblocker.event.LOCAL_SMS_RECEIVED";
    public static final String KEY_SMS_RECEIVED = "key_sms_received";


    /**
     * This Method is Called When SMS is Received
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Start Log
        final String methodName = "onReceive()";
        log.verbose(methodName, "Just Entered");

        //Receive SMS
        if (intent.getAction() == SMS_RECEIVED) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                for (Object pdu: pdus) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[])pdu, MSG_FORMAT);
                    SMS sms = new SMS();
                    sms.setBody(msg.getMessageBody());
                    sms.setFrom(msg.getOriginatingAddress());
                    sms.setDateTime(msg.getTimestampMillis());

                    log.info(methodName, "Received Message From: "+msg.getDisplayOriginatingAddress());

                    broadcastLocalSMS(context, sms);
                    notifUtil.createSMSNotification(context, sms);
                }
            }
        }
    }


    private void broadcastLocalSMS(Context context, SMS sms){
        Bundle basket = new Bundle();
        String jsonSMS = gson.toJson(sms);
        basket.putString(KEY_SMS_RECEIVED, jsonSMS);
        broadcastUtil.broadcast(context, LOCAL_SMS_RECEIVED, basket);
    }
}
