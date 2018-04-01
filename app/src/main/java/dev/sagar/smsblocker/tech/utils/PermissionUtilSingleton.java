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

import java.util.Arrays;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class PermissionUtilSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Core
    private static PermissionUtilSingleton instance = null;
    public static final int RESULT_CODE_APP_DEFAULT = 1588;
    public static final int RESULT_CODE_SMS_PERMISSION = 1299;
    public static final int RESULT_CODE_CONTACT_PERMISSION = 120;


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
     * @return if app has permission with index in same order
     */
    public boolean[] hasPermissions(Context context, String... permissions) {
        final String methodName = "hasPermissions()";
        log.justEntered(methodName);

        boolean result[] = new boolean[permissions.length];

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null ) {
            for(int i=0; i<permissions.length; i++){
                String permission = permissions[i];
                boolean hasPermission = ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
                result[i] = hasPermission;
            }
        }
        else{
            Arrays.fill(result, true);
        }

        log.returning(methodName);
        return result;
    }


    /**
     * This method checks if app has list of passed permission
     * @param context Context of APp
     * @param permission Permission
     * @return if app has permission with index in same order
     */
    public boolean hasPermission(Context context, String permission) {
        final String methodName = "hasPermissions()";
        log.justEntered(methodName);

        boolean hasPermission = false;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null ) {
            hasPermission = ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        }
        else{
            hasPermission = true;
        }

        log.returning(methodName);
        return hasPermission;
    }


    /**
     * This Method Checks if your app is Default SMS App or Not
     * @param context
     * @return
     */
    public boolean isAppDefault(Context context){
        final String methodName = "isAppDefault()";
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
    public void askToMakeAppDefault(Activity context){
        final String methodName = "askToMakeAppDefault()";
        log.justEntered(methodName);

        String myPackageName = context.getPackageName();
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
        context.startActivityForResult(intent, RESULT_CODE_APP_DEFAULT);

        log.returning(methodName);
    }


    public void ask(Activity activity, String permission, int requestCode){
        final String methodName = "askPermission()";
        log.justEntered(methodName);

        String[] permissions = {permission};
        ActivityCompat.requestPermissions(activity, permissions, requestCode);

        log.returning(methodName);
    }

    public void ask(Activity activity, String[] permissions, int requestCode){
        final String methodName = "askPermissions()";
        log.justEntered(methodName);

        ActivityCompat.requestPermissions(activity, permissions, requestCode);

        log.returning(methodName);
    }
}
