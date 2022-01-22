package dev.sagar.smsblocker.tech.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import java.util.HashSet;
import java.util.Set;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.broadcastreceivers.NotificationBroadcastReceiver;
import dev.sagar.smsblocker.ux.activities.ChatActivity;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class NotificationUtilSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android

    //Java Core
    private static NotificationUtilSingleton instance = null;
    private static int sNotificationId = 0;

    //Constants
    public static final String KEY_REPLY = "reply";
    private static final int SUMMARY_ID = 0;
    private static final String NOTIFICATION_GROUP = "NOTIFICATION_GROUP";

    //Notification Delay Problem
    private static boolean notifGroupCreated = false;

    /**
     * This is part of Singleton Design pattern
     * @return
     */
    private NotificationUtilSingleton(){}


    /**
     * This method is part of Singleton Design pattern
     * @return
     */
    public synchronized static NotificationUtilSingleton getInstance(){
        if(instance == null)
            instance = new NotificationUtilSingleton();
        return instance;
    }

    /**
     * This Method Creates a Notification
     * @param context
     * @param sms
     */
    public void createSMSNotification(Context context, SMS sms){
        final String methodName = "createSMSNotification()";
        log.justEntered(methodName);

        /*String fromNo = sms.getAddress();
        String fromName = fromNo;
        try {
            fromName = ContactUtilSingleton.getInstance().getContactName(context, fromNo);
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        String text = sms.getBody();

        String groupKey = fromNo;
        int notificationId = getNotificationId();

        log.info(methodName, "Creating Notification...");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(fromName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(text)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setGroup(groupKey)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(sms.getDateTime())
                .setShowWhen(true);

        Intent resultIntent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.KEY_THREAD_ID, fromNo);
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
        mNotifyMgr.notify(notificationId, mBuilder.build());
        log.info(methodName, "Notification Created with ID: "+notificationId);*/

        /*generateSingleNotification(context, sms);
        setSummaryNotification(context, sms);*/



        long startTime = System.currentTimeMillis();

        int notifId = getNotificationId();
        String threadId = sms.getThreadId();
        String address = sms.getAddress();
        Contact contact = ContactUtilSingleton.getInstance().getContactOrDefault(context, address);;


        //-- Open Activity onClick Ends
        Intent resultIntent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ChatActivity.KEY_THREAD_ID, threadId);
        bundle.putString(ChatActivity.KEY_ADDRESS, address);
        resultIntent.putExtras(bundle);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //-- Open Activity onClick Ends


        //Get Notification Builder
        NotificationCompat.Builder mBuilder = getNotifBuilder(context, contact, sms);
        NotificationCompat.Builder mGroupBuilder = getNotifBuilder(context, contact, sms).setGroupSummary(true);

        NotificationCompat.Action action = getDirectReplyAction(context, sms, notifId);
        mBuilder.addAction(action); // reply action from step b above
        mGroupBuilder.addAction(action);


        //Set Priority
        setPriority(context, mBuilder);
        setPriority(context, mGroupBuilder);

        //Group Notification
        setGroup(context, mBuilder, contact);
        setGroup(context,  mGroupBuilder, contact);

        //Setting on click envent
        mBuilder.setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        mGroupBuilder.setContentIntent(resultPendingIntent)
                .setAutoCancel(true);


        log.info(methodName, "Creating Notification..");
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        Notification notification = mBuilder.build();
        Notification notifGroupSummary = mGroupBuilder.build();



        if(notifGroupCreated)
            mNotificationManager.notify(notifId, notification);

        mNotificationManager.notify(SUMMARY_ID, notifGroupSummary);

        if(!notifGroupCreated) {
            mNotificationManager.notify(getNotificationId(), notification);
            notifGroupCreated = true;
        }


        /*String CHANNEL_ID = "12345";
        NotificationCompat.MessagingStyle.Message message1 =
                new NotificationCompat.MessagingStyle.Message(sms.getBody(),
                        sms.getDateTime(),
                        sms.getAddress());
        NotificationCompat.MessagingStyle.Message message2 =
                new NotificationCompat.MessagingStyle.Message("Hello Billo: "+sms.getBody(),
                        sms.getDateTime(),
                        "Sagar Pawar");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notif_24dp)
                .setStyle(new NotificationCompat.MessagingStyle(context.getString(R.string.text_conversation))
                        .addMessage(message1)
                        .addMessage(message2));

        Notification notification = mBuilder.build();
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);


        Contact contact = ContactUtilSingleton.getInstance().getContactOrDefault(context, sms.getAddress());;
        NotificationCompat.Builder mGroupBuilder = getNotifBuilder(context, contact, sms).setGroupSummary(true);
        setGroup(context,  mGroupBuilder, contact);
        Notification notifGroupSummary = mGroupBuilder.build();

        setPriority(context, mGroupBuilder);
        setPriority(context, mBuilder);

        mNotificationManager.notify(getNotificationId(), notification);
        mNotificationManager.notify(SUMMARY_ID, notifGroupSummary);*/

        log.returning(methodName);
    }

    private NotificationCompat.Builder getNotifBuilder(Context context, Contact contact, SMS sms){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "12345");
        String text = sms.getBody();
        String fromName = contact.getDisplayName();
        int notifColor = context.getResources().getColor(R.color.colorPrimaryDark, null);

        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notif)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentTitle(fromName)
//                .setWhen(sms.getDateTime())
                .setColor(notifColor)
                .setContentText(text)
                .setShowWhen(true);*/


        /*mBuilder.setSmallIcon(R.drawable.ic_notif_24dp)
                //build summary info into InboxStyle template
                .setStyle(new NotificationCompat.BigTextStyle()
                        *//*.addLine("Alex Faarborg  Check this out")
                        .addLine("Jeff Chang    Launch Party")*//*
                        //.setBigContentTitle(text)
                        //.setSummaryText(fromName)
                        .bigText(text)
                );*/

        NotificationCompat.MessagingStyle.Message message1 = new NotificationCompat.MessagingStyle.Message(sms.getBody(), sms.getDateTime(), contact.getDisplayName());

        mBuilder.setStyle(new NotificationCompat.MessagingStyle(context.getString(R.string.text_conversation)).addMessage(message1));

        mBuilder.setSmallIcon(R.drawable.ic_notif_24dp)
                .setContentTitle(fromName)
                .setColor(notifColor)
                //.setSubText(fromName)
                .setContentText(text)
                .setShowWhen(true)
                /*.setStyle(new NotificationCompat.InboxStyle()
                        .addLine(text)
                        .setBigContentTitle(fromName)
                        .setSummaryText(fromName)
                )*/
        ;


        return  mBuilder;
    }


    private int getNotificationId(){
        return sNotificationId++;
    }


    private void setGroup(Context context, NotificationCompat.Builder builder, Contact contact){
        final String methodName = "groupNotification(Context, NotificationCompat.Builder)";
        log.justEntered(methodName);

        //TODO Change Group key Here
        //String formatedNumber = PhoneUtilsSingleton.getInstance().formatNumber(context, contact.getNumber());
        //String groupKey = formatedNumber;
        String groupKey = NOTIFICATION_GROUP;
        builder.setGroup(groupKey);

        log.returning(methodName);
    }

    /**
     * Sets priority of notification
     *
     * If Screen is on
     *      - Sound only
     * If Screen is of
     *      - Sound and Vibrate
     * @param context Context of application
     * @param builder Contact for which chanel is to be made
     */
    private void setPriority(Context context, NotificationCompat.Builder builder){
        final String methodName = "setPriority(Context, NotificationCompat.Builder)";
        log.justEntered(methodName);


        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isInteractive = powerManager.isInteractive();
        if(isInteractive){
            log.info(methodName, "Setting Importance Default");

            Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setSound(soundUri)
                    .setLights(context.getColor(R.color.colorPrimary), 50, 10)
                    //.setVibrate(new long[]{500, 500, 500, 500})
            ;
        }
        else{
            log.info(methodName, "Setting Importance High");
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        }

        log.returning(methodName);
    }

    private PendingIntent getReplyPendingIntent(Context context, SMS sms, int notifId){
        final String methodName = "getReplyPendingIntent(Context, SMS, int)";
        log.justEntered(methodName);

        String address = sms.getAddress();

        Intent intent;
        PendingIntent pIntent = null;

        if(sms.isReplySupported()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // start a
                // (i)  broadcast receiver which runs on the UI thread or
                // (ii) service for a background task to b executed , but for the purpose of
                // this codelab, will be doing a broadcast receiver
                intent = new Intent(context, NotificationBroadcastReceiver.class);

                if (sms.isReplySupported()) {
                    intent.setAction(NotificationBroadcastReceiver.REPLY_ACTION);
                }

                Bundle basket = new Bundle();
                basket.putString(NotificationBroadcastReceiver.KEY_ADDRESS, address);
                basket.putInt(NotificationBroadcastReceiver.KEY_NOTIF_ID, notifId);

                intent.putExtras(basket);
                pIntent = PendingIntent.getBroadcast(context, 100, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                // start your activity for Android M and below
                intent = new Intent(context, ChatActivity.class);

                intent.setAction(NotificationBroadcastReceiver.REPLY_ACTION);
                Bundle basket = new Bundle();
                basket.putString(ChatActivity.KEY_THREAD_ID, address);
                intent.putExtras(basket);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pIntent = PendingIntent.getActivity(context, 100, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }

        log.returning(methodName);
        return pIntent;
    }

    private NotificationCompat.Action getDirectReplyAction(Context context, SMS sms, int notifId){
        final String methodName = "getDirectReplyAction()";
        log.justEntered(methodName);

        String replyLabel = context.getString(R.string.label_reply);
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel(replyLabel)
                .build();

        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher, replyLabel, getReplyPendingIntent(context, sms, notifId))
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        log.returning(methodName);
        return replyAction;
    }


    public void notifySMSSent(Context context, int notifId){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notifId);
        Toast.makeText(context, context.getString(R.string.notif_content_sent), Toast.LENGTH_LONG).show();
    }

    public void notifySendingFailed(Context context, int notifId){
        Toast.makeText(context, context.getString(R.string.notif_content_sent), Toast.LENGTH_LONG).show();
    }
}
