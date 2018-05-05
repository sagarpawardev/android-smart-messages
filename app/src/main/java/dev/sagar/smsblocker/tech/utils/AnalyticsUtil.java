package dev.sagar.smsblocker.tech.utils;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sagarpawar on 05/05/18.
 */

public class AnalyticsUtil {

    public static void start(Context context) {
        Fabric.with(context, new Crashlytics());
    }

}
