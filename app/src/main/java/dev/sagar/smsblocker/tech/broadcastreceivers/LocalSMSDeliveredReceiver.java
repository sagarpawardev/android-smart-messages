package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class LocalSMSDeliveredReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil(this.getClass().getName());

    //Constant
    public static final String EVENT_DELIVERED = LocalSMSDeliveredReceiver.class.getName();
    public static final String KEY_SMS = "sms";

    //Java Android
    private Gson gson = new Gson();

    //Java Core
    Callback callback = null;

    public boolean isRegistered = false;


    public LocalSMSDeliveredReceiver(Callback callback){
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

        Bundle basket = intent.getExtras();
        String jsonSMS = basket.getString(KEY_SMS);
        SMS sms = gson.fromJson(jsonSMS, SMS.class);

        String action = intent.getAction();
        String msg = "";
        switch (action){
            case SMSDeliveredReceiver.KEY_SMS_DELIVERED:
                msg = context.getString(R.string.txt_delivered);
                callback.onSMSDelivered(sms);
                break;
            case SMSDeliveredReceiver.KEY_DELIVERY_CANCELLED:
                msg = context.getString(R.string.err_delivery_cancelled);
                break;
            default:
                msg = context.getString(R.string.err_unknown_delivery);
                log.error(methodName, "Some Unknown Error occurred action code: "+action);
        }

        log.info(methodName, "Toasting text: "+msg);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        log.info(methodName, "Calling Callback");
        callback.onSMSDelivered(sms);

        log.returning(methodName);
    }


    public interface Callback{
        void onSMSDelivered(SMS sms);
    }
}
