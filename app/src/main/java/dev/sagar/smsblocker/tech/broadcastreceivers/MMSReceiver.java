package dev.sagar.smsblocker.tech.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;

import dev.sagar.smsblocker.tech.utils.AnalyticsUtil;
import io.fabric.sdk.android.Fabric;

/**
 * Created by sagarpawar on 18/11/17.
 */

public class MMSReceiver extends BroadcastReceiver {

    /**
     * This method is called when an MMS is received
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        AnalyticsUtil.start(context);
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
