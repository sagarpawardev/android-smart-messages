package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
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
    private final static String LOCAL_SMS_SENT = EventCode.LOCAL_SMS_SENT;

    private final static String type = Telephony.Sms.TYPE;
    private final static String _id = Telephony.Sms._ID;

    private static final Uri SMS_URI = Telephony.Sms.Inbox.CONTENT_URI;
    private BroadcastUtilSingleton broadcastUtil = BroadcastUtilSingleton.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String methodName = "onReceive()";
        log.justEntered(methodName);

        String action = intent.getAction();
        log.info(methodName, "Event Occured: "+action);
        try {
            if (action.equals(ActionCode.SMS_SENT)) {
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
                broadcastLocalSMS(context, sms);
            }
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
    private void broadcastLocalSMS(Context context, SMS sms){
        Bundle basket = new Bundle();
        basket.putSerializable(LocalSMSSentReceiver.KEY_SMS, sms);
        broadcastUtil.broadcast(context, LOCAL_SMS_SENT, basket);
    }
}
