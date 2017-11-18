package dev.sagar.smsblocker.tech.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class PermissionUtilSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Core
    private static PermissionUtilSingleton instance = null;


    /**
     * This is part of Singleton Design pattern
     * @return
     */
    private PermissionUtilSingleton(){}


    /**
     * This method is part of Singleton Design pattern
     * @return
     */
    public synchronized static PermissionUtilSingleton getInstance(){
        if(instance == null)
            instance = new PermissionUtilSingleton();
        return instance;
    }


    /**
     * This method checks if app has list of passed permission
     * @param context
     * @param permissions List of Permissions
     * @return
     */
    public boolean hasPermissions(Context context, String... permissions) {
        final String methodName = "hasPermissions()";
        log.info(methodName, "Just Entered...");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }

        log.info(methodName, "Returing...");
        return true;
    }


    /**
     * This Method Checks if your app is Default SMS App or Not
     * @param context
     * @return
     */
    public boolean isAppDefaultSMSApp(Context context){
        final String methodName = "isAppDefaultSMSApp()";
        log.info(methodName, "Just Entered...");

        final String myPackageName = context.getPackageName();
        boolean result = Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName);

        log.info(methodName, "Returing...");
        return result;
    }

    /**
     * This Method opens dialog to make app default
     * @param context
     * @return
     */
    public void askToMakeAppDefault(Context context){
        final String methodName = "askToMakeAppDefault()";
        log.info(methodName, "Just Entered...");

        String myPackageName = context.getPackageName();
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
        context.startActivity(intent);

        log.info(methodName, "Returing...");
    }
}
