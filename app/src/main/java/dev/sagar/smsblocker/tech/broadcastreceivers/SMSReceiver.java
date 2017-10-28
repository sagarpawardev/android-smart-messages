package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.ContactUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.activities.activities.ThreadActivity;

public class SMSReceiver extends BroadcastReceiver {

    //Log Initiate
    LogUtil log = new LogUtil( this.getClass().getName() );

    //Internal References
    final SmsManager sms = SmsManager.getDefault();
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String MSG_FORMAT = "3gpp";


    /**
     * This Method is Called When SMS is Received
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //Start Log
        final String methodName = "onReceive()";
        log.verbose(methodName, "Just Entered");

        //Receive SMS
        if (intent.getAction() == SMS_RECEIVED) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                for (Object pdu: pdus) {
                    SmsMessage msg = SmsMessage.createFromPdu((byte[])pdu, MSG_FORMAT);
                    SMS sms = new SMS();
                    sms.setBody(msg.getMessageBody());
                    sms.setFrom(msg.getOriginatingAddress());

                    log.info(methodName, "Received Message From: "+msg.getDisplayOriginatingAddress());
                    createNotification(context, sms);
                }
            }
        }
    }

    /**
     * This Method Creates a Notification
     * @param context
     * @param sms
     */
    public void createNotification(Context context, SMS sms){
        final int NOTIFICATION_ID = 123;

        String from = sms.getFrom();
        String fromName = ContactUtil.getContactName(context, from);
        String text = sms.getBody();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(fromName)
                        .setContentText(text);

        Intent resultIntent = new Intent(context, ThreadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_THREAD_ID, from);
        resultIntent.putExtras(bundle);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent)
        .setAutoCancel(true);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
