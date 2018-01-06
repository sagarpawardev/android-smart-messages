package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.gson.Gson;

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

        int resultCode = getResultCode();
        Bundle basket = intent.getExtras();
        String jsonSMS = basket.getString(KEY_SMS);
        SMS sms = gson.fromJson(jsonSMS, SMS.class);

        log.info(methodName, "Calling Callback");
        switch (resultCode) {
            case Activity.RESULT_OK: callback.onSMSSent(sms);
                break;
            default: callback.onSMSSentFailure(sms); break;
        }

        log.returning(methodName);
    }


    public interface Callback{
        void onSMSSent(SMS sms);
        void onSMSSentFailure(SMS sms);
    }
}
