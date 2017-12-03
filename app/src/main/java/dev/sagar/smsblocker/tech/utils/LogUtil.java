package dev.sagar.smsblocker.tech.utils;

import android.util.Log;

/**
 * Created by sagarpawar on 28/10/17.
 */

public class LogUtil {
    private String className;

    public LogUtil(String className) {
        this.className = className;
    }

    /**
     * This method logs an Error
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void error(String method, String msg){
        Log.e(className, method+" ==> "+msg);
    }


    /**
     * This method logs an Information
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void info(String method, String msg){
        Log.i(className, method+" ==> "+msg);
    }


    /**
     * This method logs a Debug
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void debug(String method, String msg){
        Log.d(className, method+" ==> "+msg);
    }


    /**
     * This method logs a Verbose
     * @param method Current Method Name
     * @param msg Message to Log
     */
    public void verbose(String method, String msg){
        Log.v(className, method+" ==> "+msg);
    }


    /**
     * This method print Returning.. in log
     * @param method Current Method Name
     */
    public void returning(String method){
        debug(method, "Returning..");
    }


    /**
     * This method print Just Entered.. in log
     * @param method Current Method Name
     */
    public void justEntered(String method){
        debug(method, "Just Entered..");
    }
}
