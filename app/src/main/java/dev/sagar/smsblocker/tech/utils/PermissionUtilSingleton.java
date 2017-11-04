package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class PermissionUtilSingleton {

    //Java Core
    private static PermissionUtilSingleton instance = null;


    private PermissionUtilSingleton(){}


    public synchronized static PermissionUtilSingleton getInstance(){
        if(instance == null)
            instance = new PermissionUtilSingleton();
        return instance;
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
