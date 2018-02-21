package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.NotificationUtilSingleton;
import dev.sagar.smsblocker.tech.utils.SMSUtil;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    public static final String KEY_ADDRESS = "address";
    public static String KEY_NOTIF_ID = "notif_id"; //This is needed to close a particular notification
    public static String REPLY_ACTION = "dev.sagar.action.REPLY_ACTION";

    private static String KEY_REPLY = NotificationUtilSingleton.KEY_REPLY;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (REPLY_ACTION.equals(intent.getAction())) {
            // do whatever you want with the message. Send to the server or add to the db.
            // for this tutorial, we'll just show it in a toast;
            CharSequence message = getReplyMessage(intent);
            Bundle bundle = intent.getExtras();
            String lAddress = bundle.getString(KEY_ADDRESS);
            int notifId = bundle.getInt(KEY_NOTIF_ID);

            SMSUtil smsUtil = new SMSUtil(context);
            SMS sms = smsUtil.sendSMS(lAddress, message.toString());
            if(sms != null){
                NotificationUtilSingleton.getInstance().notifySMSSent(context, notifId);
            }
            else{
                NotificationUtilSingleton.getInstance().notifySendingFailed(context, notifId);
            }

        }
    }

    private CharSequence getReplyMessage(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_REPLY);
        }
        return null;
    }
}
