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
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.widget.Toast;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.broadcastreceivers.NotificationBroadcastReceiver;
import dev.sagar.smsblocker.ux.activities.ThreadActivity;

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
    private static final int GROUP_NOTIF_ID = 0;
    private static final String NOTIFICATION_GROUP = "NOTIFICATION_GROUP";

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

        Intent resultIntent = new Intent(context, ThreadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_ADDRESS, fromNo);
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

        int notifId = getNotificationId();
        String address = sms.getAddress();
        Contact contact = ContactUtilSingleton.getInstance().getContactOrDefault(context, address);;

        //Get Notification Builder
        NotificationCompat.Builder mBuilder = getNotifBuilder(context, contact, sms);

        NotificationCompat.Action action = getDirectReplyAction(context, sms, notifId);
        mBuilder.addAction(action); // reply action from step b above

        //Set Priority
        setPriority(context, mBuilder);

        //Group Notification
        setGroup(context, mBuilder, contact);

        // Open Activity onClick Ends
        Intent resultIntent = new Intent(context, ThreadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_ADDRESS, address);
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
        // Open Activity onClick Ends


        log.info(methodName, "Creating Notification..");
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(notifId, notification);

        Notification notifGroupSummary = getGroupSummary(context, contact, sms);
        mNotificationManager.notify(GROUP_NOTIF_ID, notifGroupSummary);

        log.returning(methodName);
    }

    private NotificationCompat.Builder getNotifBuilder(Context context, Contact contact, SMS sms){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
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


        mBuilder.setSmallIcon(R.drawable.ic_notif)
                //build summary info into InboxStyle template
                .setStyle(new NotificationCompat.BigTextStyle()
                        /*.addLine("Alex Faarborg  Check this out")
                        .addLine("Jeff Chang    Launch Party")*/
                        //.setBigContentTitle(text)
                        //.setSummaryText(fromName)
                        .bigText(text)
                );

        mBuilder.setSmallIcon(R.drawable.ic_notif)
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


    private Notification getGroupSummary(Context context, Contact contact, SMS sms){


        //FIXME First Notification is overridden. When 2nd notification comes b

        NotificationCompat.Action action = getDirectReplyAction(context, sms, GROUP_NOTIF_ID);

        //Get Notification Builder
        NotificationCompat.Builder builder = getNotifBuilder(context, contact, sms);
        builder.addAction(action);

        setGroup(context, builder, contact);

        builder.setGroupSummary(true);
        return builder.build();
    }

    private int getNotificationId(){
        return sNotificationId++;
    }

    /*private void setSummaryNotification(Context context, SMS sms, String groupKey) {
        Contact contact = ContactUtilSingleton.getInstance().getContactOrDefault(context, sms.getAddress());
        String fromName = contact.getDisplayName();

        String text = sms.getBody();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(context)
                .setContentText(text)
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_notif))
                .setSmallIcon(R.drawable.ic_notif)
                .setStyle(new NotificationCompat.InboxStyle()
                        //.setBigContentTitle("From "+fromName)
                        //.addLine("Tip 1")
                        //.addLine("Tip 2")
                        //.addLine("Tip 3")
                        //.addLine("Tip 4")
                        .setSummaryText(fromName))
                .setGroup(groupKey)
                .setGroupSummary(true);

        notificationManager.notify(100, summaryNotification.build());
    }*/

    /*private void generateSingleNotification(Context context, SMS sms) {
        String fromNo = sms.getAddress();
        String fromName = fromNo;
        try {
            fromName = ContactUtilSingleton.getInstance().getContactName(context, fromNo);
        } catch (ReadContactPermissionException e) {
            e.printStackTrace();
        }
        String text = sms.getBody();

        String groupKey = fromNo;
        int notificationId = getNotificationId();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        *//*PendingIntent pIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(),
                new Intent(context, NotificationOpenActivity.class), 0);

        // Add to your action, enabling Direct Reply for it
        NotificationCompat.Action replayAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_replay, "Replay", pIntent)
                        .build();*//*


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentText(text)
                .setAutoCancel(false)
                //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notif))
                .setSmallIcon(R.drawable.ic_notif)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text)
                        //.setBigContentTitle("How to create bundle notification? Tip: " + BIG_TEXT_NOTIFICATION_KEY)
                        .setSummaryText(fromName));

        Intent activityIndent = new Intent(context, ThreadActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThreadActivity.KEY_ADDRESS, fromNo);
        activityIndent.putExtras(bundle);
        PendingIntent activityPIndent =
                PendingIntent.getActivity(
                        context,
                        0,
                        activityIndent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        PendingIntent replyPIntent = getReplyPendingIntent(context, sms);
        mBuilder.setContentIntent(activityPIndent)
                .setAutoCancel(true);

        NotificationCompat.Action replyAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_notif,
                        context.getString(R.string.label_reply), replyPIntent)
                        .build();

        mBuilder.setContentIntent(activityPIndent)
                .addAction(replyAction)
                .setGroup(groupKey);

        notificationManager.notify(notificationId, mBuilder.build());
    }*/

    private void showNotification(Context context, Notification notification){
        final String methodName = "showNotification(Context, Notification)";
        log.justEntered(methodName);



        log.returning(methodName);
    }

    private void setGroup(Context context, NotificationCompat.Builder builder, Contact contact){
        final String methodName = "groupNotification(Context, NotificationCompat.Builder)";
        log.justEntered(methodName);

        //TODO Change Group key Here
        String formatedNumber = PhoneUtilsSingleton.getInstance().formatNumber(context, contact.getNumber());
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
        PendingIntent pIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // start a
            // (i)  broadcast receiver which runs on the UI thread or
            // (ii) service for a background task to b executed , but for the purpose of
            // this codelab, will be doing a broadcast receiver
            intent = new Intent(context, NotificationBroadcastReceiver.class);
            intent.setAction(NotificationBroadcastReceiver.REPLY_ACTION);
            Bundle basket = new Bundle();
            basket.putString(NotificationBroadcastReceiver.KEY_ADDRESS, address);
            basket.putInt(NotificationBroadcastReceiver.KEY_NOTIF_ID, notifId);

            intent.putExtras(basket);
            pIntent = PendingIntent.getBroadcast(context, 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            // start your activity for Android M and below
            intent = new Intent(context, ThreadActivity.class);
            intent.setAction(NotificationBroadcastReceiver.REPLY_ACTION);
            Bundle basket = new Bundle();
            basket.putString(ThreadActivity.KEY_ADDRESS, address);
            intent.putExtras(basket);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pIntent = PendingIntent.getActivity(context, 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
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
