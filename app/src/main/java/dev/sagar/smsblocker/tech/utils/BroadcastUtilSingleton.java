package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by sagarpawar on 05/11/17.
 */

public class BroadcastUtilSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Gson gson = new Gson();

    //Java Core
    private static final BroadcastUtilSingleton ourInstance = new BroadcastUtilSingleton();


    public synchronized static BroadcastUtilSingleton getInstance() {
        return ourInstance;
    }


    private BroadcastUtilSingleton() {}

    /***
     * This method broadcasts a message locally
     * @param context
     * @param eventName
     * @param basket
     */
    public void broadcast(Context context, String eventName, Bundle basket){
        Intent intent = new Intent(eventName);
        intent.putExtras(basket);
        context.sendBroadcast(intent);
    }
}
