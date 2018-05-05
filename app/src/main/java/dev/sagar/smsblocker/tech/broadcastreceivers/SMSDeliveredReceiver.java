package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.Toast;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.AnalyticsUtil;
import dev.sagar.smsblocker.tech.utils.BroadcastUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;

public class SMSDeliveredReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil( this.getClass().getName() );

    public final static String EVENT_CODE = "sms_event_delivered";
    public final static String KEY_PART_INDEX = "part_index";
    public final static String KEY_TOTAL_PARTS = "total_parts";
    public final static String KEY_SMS = "sms";

    public final static String KEY_SMS_DELIVERED = "sms_delivered";
    public final static String KEY_DELIVERY_CANCELLED = "sms_delivery_cancelled";

    private final static String type = Telephony.Sms.TYPE;
    private final static String _id = Telephony.Sms._ID;

    private static final Uri SMS_URI = Telephony.Sms.Inbox.CONTENT_URI;
    private BroadcastUtilSingleton broadcastUtil = BroadcastUtilSingleton.getInstance();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        final String methodName = "onReceive()";
        log.justEntered(methodName);
        AnalyticsUtil.start(context);

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "SMS Delivered",
                        Toast.LENGTH_SHORT).show();
                resultOk(context, intent);
                break;

            case Activity.RESULT_CANCELED:
                Toast.makeText(context, "SMS Delivery Canceled",
                        Toast.LENGTH_SHORT).show();
                resultCanceled(context, intent);
                break;
        }

        log.returning(methodName);
    }

    private void resultOk(Context context, Intent intent){
        final String methodName = "resultOk(Context, Intent)";
        log.justEntered(methodName);

        Bundle basket = intent.getExtras();
        SMS sms = (SMS) basket.getSerializable(KEY_SMS);

        log.info(methodName, "Broadcasting Locally");
        broadcastLocalSMS(context, sms, KEY_SMS_DELIVERED);
    }

    private void resultCanceled(Context context, Intent intent){
        final String methodName = "resultCanceled(Context, Intent)";
        log.justEntered(methodName);

        Bundle basket = intent.getExtras();
        SMS sms = (SMS) basket.getSerializable(KEY_SMS);
        broadcastLocalSMS(context, sms, KEY_DELIVERY_CANCELLED);

        log.returning(methodName);
    }

    /**
     * This method broadcast SMS SENT Locally
     * @param context Context
     */
    private void broadcastLocalSMS(Context context, SMS sms, String key){
        final String methodName = "broadcastLocalSMS(Context, SMS)";
        log.justEntered(methodName);

        Bundle basket = new Bundle();
        basket.putSerializable(LocalSMSDeliveredReceiver.KEY_SMS, sms);
        broadcastUtil.broadcast(context, key, basket);

        log.returning(methodName);
    }
}
