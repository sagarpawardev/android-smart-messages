package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class LocalSMSReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Constant
    private static final String SMS_RECEIVED = "smsblocker.event.LOCAL_SMS_RECEIVED";

    //Java Android
    private Gson gson = new Gson();

    //Java Core
    Callback callback = null;


    public LocalSMSReceiver(Callback callback){
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
        log.verbose(methodName, "Just Entered..");

        //Receive SMS
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            String jsonSMS = bundle.getString(SMSReceiver.KEY_SMS_RECEIVED);
            SMS sms = gson.fromJson(jsonSMS, SMS.class);

            log.info(methodName, "Calling Callback");
            callback.onSMSReceived(sms);
        }

        log.info(methodName, "Returned..");
    }


    public interface Callback{
        void onSMSReceived(SMS sms);
    }
}
