package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.gson.Gson;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.EventCode;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class LocalSMSSentReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Constant
    public static final String KEY_SMS = "sms";
    public static final String EVENT_SENT = LocalSMSSentReceiver.class.getName();

    //Java Android
    private Gson gson = new Gson();

    //Java Core
    Callback callback = null;

    public boolean isRegistered = false;


    public LocalSMSSentReceiver(Callback callback){
        this.callback = callback;
    }


    /**
     * This method is called by Android when SMS is Received
     * @param context Context
     * @param intent SMSReceived Intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String methodName = "onReceive()";
        log.justEntered(methodName);


        String action = intent.getAction();
        Bundle basket = intent.getExtras();
        String jsonSMS = basket.getString(KEY_SMS);
        SMS sms = gson.fromJson(jsonSMS, SMS.class);


        String msg = null;
        switch (action){
            case SMSSentReceiver.KEY_SMS_SENT:
                sms.setType(SMS.TYPE_SENT);
                msg = context.getString(R.string.txt_sms_sent);
                callback.onSMSSent(sms); break;
            case SMSSentReceiver.KEY_GENERIC_FAILURE:
                sms.setType(SMS.TYPE_FAILED);
                msg = context.getString(R.string.err_generic_failure);
                callback.onSMSSentFailure(sms); break;
            case SMSSentReceiver.KEY_NO_SERVICE:
                sms.setType(SMS.TYPE_FAILED);
                msg = context.getString(R.string.err_no_service);
                callback.onSMSSentFailure(sms); break;
            case SMSSentReceiver.KEY_RADIO_OFF:
                msg = context.getString(R.string.err_radio_off);
                sms.setType(SMS.TYPE_FAILED);
                callback.onSMSSentFailure(sms); break;
            case SMSSentReceiver.KEY_NULL_PDU_FLAG:
                msg = context.getString(R.string.err_null_pdu);
                sms.setType(SMS.TYPE_FAILED);
                callback.onSMSSentFailure(sms); break;
            /*default:
                log.error(methodName, "Some different error: "+action);
                msg = context.getString(R.string.err_unknown_sending);*/
        }

        if(msg!=null) {
            log.info(methodName, "Toasting text: " + msg);
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        log.returning(methodName);
    }


    public interface Callback{
        void onSMSSent(SMS sms);
        void onSMSSentFailure(SMS sms);
    }
}
