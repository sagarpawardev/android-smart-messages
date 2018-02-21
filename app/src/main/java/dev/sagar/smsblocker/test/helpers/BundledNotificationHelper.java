package dev.sagar.smsblocker.test.helpers;

/**
 * Created by sagarpawar on 17/02/18.
 */

    import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.BitmapFactory;
        import android.support.v4.app.NotificationCompat;

    import dev.sagar.smsblocker.R;

/**
 * Created by multidots on 6/23/2016.
 */
public class BundledNotificationHelper {
    private static NotificationManager notificationManager;
    public static final String NOTIFICATION_GROUP_KEY = "group_key";
    private static int BIG_TEXT_NOTIFICATION_KEY = 0;

    public BundledNotificationHelper(Context context) {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void generateBundle(Context context) {
        for (int i = 0; i < 4; i++) {
            generateSingleNotification(context);
        }

        //create summary notification
        setSummaryNotification(context);
    }

    private void setSummaryNotification(Context context) {
        NotificationCompat.Builder summaryNotification = new NotificationCompat.Builder(context)
                .setContentText("This is test notification.")
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle("Bundled notification tips")
                        .addLine("Tip 1")
                        .addLine("Tip 2")
                        .addLine("Tip 3")
                        .addLine("Tip 4")
                        .setSummaryText("Total 4 tips"))
                .setGroup(NOTIFICATION_GROUP_KEY)
                .setGroupSummary(true);

        notificationManager.notify(100, summaryNotification.build());
    }

    private void generateSingleNotification(Context context) {
        BIG_TEXT_NOTIFICATION_KEY++;

        /*PendingIntent pIntent = PendingIntent.getActivity(context,
                (int) System.currentTimeMillis(),
                new Intent(context, NotificationOpenActivity.class), 0);

        // Add to your action, enabling Direct Reply for it
        NotificationCompat.Action replayAction =
                new NotificationCompat.Action.Builder(R.drawable.ic_replay, "Replay", pIntent)
                        .build();*/

        NotificationCompat.Builder noti1 = new NotificationCompat.Builder(context)
                .setContentText("This is test notification.")
                .setAutoCancel(false)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("It's important that you still provide a summary notification that appears on handheld devices. So in addition to adding each unique notification to the same stack group, also add a summary notification and call setGroupSummary() on the summary notification.")
                        .setBigContentTitle("How to create bundle notification? Tip: " + BIG_TEXT_NOTIFICATION_KEY)
                        .setSummaryText("Tip to build notification"))
                //.setContentIntent(pIntent)
                //.addAction(replayAction)
                .setGroup(NOTIFICATION_GROUP_KEY);

        notificationManager.notify(BIG_TEXT_NOTIFICATION_KEY, noti1.build());
    }
}