package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.AnalyticsUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class LocalSMSReceivedReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Constant
    public static final String EVENT_RECEIVED = SMSReceivedReceiver.EVENT_CODE;
    public static final String KEY_SMS = SMSReceivedReceiver.KEY_SMS;

    //Java Android
    private Gson gson = new Gson();

    //Java Core
    Callback callback = null;

    public boolean isRegistered = false;


    public LocalSMSReceivedReceiver(Callback callback){
        this.callback = callback;
    }


    /**
     * This method is called by Android when SMS is Received
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final String methodName = "onReceive()";
        log.justEntered(methodName);
        AnalyticsUtil.start(context);

        //Receive SMS
        if (intent.getAction().equals(EVENT_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            String jsonSMS = bundle.getString(KEY_SMS);
            SMS sms = gson.fromJson(jsonSMS, SMS.class);

            log.info(methodName, "Calling Callback");
            callback.onSMSReceived(sms);
        }

        log.returning(methodName);
        throw new RuntimeException("Hey! Man my name is Bruno");
    }


    public interface Callback{
        void onSMSReceived(SMS sms);
    }
}
