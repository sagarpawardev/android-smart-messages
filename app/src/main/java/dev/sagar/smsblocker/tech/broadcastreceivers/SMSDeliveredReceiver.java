package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.Toast;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.ActionCode;
import dev.sagar.smsblocker.tech.utils.BroadcastUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;

public class SMSDeliveredReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil( this.getClass().getName() );

    public final static String KEY_PART_INDEX = "part_index";
    public final static String KEY_TOTAL_PARTS = "total_parts";
    public final static String KEY_SMS = "sms";

    private final static String type = Telephony.Sms.TYPE;
    private final static String _id = Telephony.Sms._ID;

    private static final Uri SMS_URI = Telephony.Sms.Inbox.CONTENT_URI;
    private BroadcastUtilSingleton broadcastUtil = BroadcastUtilSingleton.getInstance();
    private final static String LOCAL_SMS_RECEIVED = LocalSMSSentReceiver.class.getName();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        final String methodName = "onReceive()";
        log.justEntered(methodName);

        String action = intent.getAction();
        if(action.equals(ActionCode.SMS_DELIVERED)) {
            Bundle basket = intent.getExtras();
            SMS sms = (SMS) basket.getSerializable(KEY_SMS);

            log.info(methodName, "Broadcasting Locally");
            broadcastLocalSMS(context, sms);
        }

        log.returning(methodName);
    }

    /**
     * This method broadcast SMS SENT Locally
     * @param context Context
     */
    private void broadcastLocalSMS(Context context, SMS sms){
        Bundle basket = new Bundle();
        basket.putSerializable(LocalSMSDeliveredReceiver.KEY_SMS, sms);
        broadcastUtil.broadcast(context, LOCAL_SMS_RECEIVED, basket);
    }
}
