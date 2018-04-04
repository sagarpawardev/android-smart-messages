package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import dev.sagar.smsblocker.tech.EventCode;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.ActionCode;
import dev.sagar.smsblocker.tech.utils.BroadcastUtilSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;

public class SMSSentReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android
    private final Gson gson = new Gson();

    public final static String KEY_PART_INDEX = "part_index";
    public final static String KEY_TOTAL_PARTS = "total_parts";
    public final static String KEY_SMS = "sms";

    public final static String KEY_SMS_SENT = "sms_sent_flag";
    public final static String KEY_GENERIC_FAILURE = "generic_failure_flag";
    public final static String KEY_NO_SERVICE = "no_service_flag";
    public final static String KEY_NULL_PDU_FLAG = "null_pdu_flag";
    public final static String KEY_RADIO_OFF = "radio_off";

    //private final static String LOCAL_SMS_SENT = EventCode.LOCAL_SMS_SENT;

    private final static String type = Telephony.Sms.TYPE;
    private final static String _id = Telephony.Sms._ID;

    private static final Uri SMS_URI = Telephony.Sms.Inbox.CONTENT_URI;
    private BroadcastUtilSingleton broadcastUtil = BroadcastUtilSingleton.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String methodName = "onReceive()";
        log.justEntered(methodName);


        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT)
                        .show();
                resultOk(context, intent);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure",
                        Toast.LENGTH_SHORT).show();
                resultFailure(context, intent, KEY_GENERIC_FAILURE);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service",
                        Toast.LENGTH_SHORT).show();
                resultFailure(context, intent, KEY_NO_SERVICE);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT)
                        .show();
                resultFailure(context, intent, KEY_NULL_PDU_FLAG);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off",
                        Toast.LENGTH_SHORT).show();
                resultFailure(context, intent, KEY_RADIO_OFF);
                break;
        }
    }


    private void resultOk(Context context, Intent intent){
        final String methodName = "resultOk(Context, Intent)";
        log.justEntered(methodName);

        try {
            Bundle basket = intent.getExtras();

            log.debug(methodName, "Sgr Receiving key: "+KEY_SMS);

            String strSMS = basket.getString(KEY_SMS);
            SMS sms = gson.fromJson(strSMS, SMS.class);
            String id = sms.getId();

            String selection = _id + " = ?";
            String[] selectionArgs = {id};
            ContentValues values = new ContentValues();
            values.put(type, SMS.TYPE_SENT);

            int updateCount = context
                    .getContentResolver()
                    .update(SMS_URI, values, selection, selectionArgs);
            log.info(methodName, "Update Count: " + updateCount);

            log.info(methodName, "Broadcasting Locally");
            broadcastLocalSMS(context, sms, KEY_SMS_SENT);

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        log.returning(methodName);
    }

    private void resultFailure(Context context, Intent intent, String resultCode){
        final String methodName = "resultOk(Context, Intent)";
        log.justEntered(methodName);

        try {
            Bundle basket = intent.getExtras();

            log.debug(methodName, "Sgr Receiving key: "+KEY_SMS);

            String strSMS = basket.getString(KEY_SMS);
            SMS sms = gson.fromJson(strSMS, SMS.class);
            String id = sms.getId();

            String selection = _id + " = ?";
            String[] selectionArgs = {id};
            ContentValues values = new ContentValues();
            values.put(type, SMS.TYPE_FAILED);

            int updateCount = context
                    .getContentResolver()
                    .update(SMS_URI, values, selection, selectionArgs);
            log.info(methodName, "Update Count: " + updateCount);

            log.info(methodName, "Broadcasting Locally");
            broadcastLocalSMS(context, sms, resultCode);

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        log.returning(methodName);
    }


    /**
     * This method broadcast SMS SENT Locally
     * @param context Context
     */
    private void broadcastLocalSMS(Context context, SMS sms, String resultCode){
        final String methodName = "broadcastLocalSMS(Context, SMS)";
        log.justEntered(methodName);

        Bundle basket = new Bundle();
        basket.putSerializable(LocalSMSSentReceiver.KEY_SMS, sms);
        broadcastUtil.broadcast(context, resultCode, basket);

        log.returning(methodName);
    }
}
