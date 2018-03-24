package dev.sagar.smsblocker.ux.utils;

import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.ux.analytics.AnalyticsApplication;

/**
 * Created by sagarpawar on 25/03/18.
 */

public class AnalyticsUtilSingleton {

    private static AnalyticsApplication application;
    private static Tracker tracker;

    public static Tracker getTracker(Context context) {
        if(application==null)
            application = new AnalyticsApplication();

        if(tracker==null)
            tracker = application.getDefaultTracker();

        return tracker;
    }
}
