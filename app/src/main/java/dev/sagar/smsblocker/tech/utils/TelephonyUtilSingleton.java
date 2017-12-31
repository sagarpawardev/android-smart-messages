package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dev.sagar.smsblocker.tech.beans.SIM;
import dev.sagar.smsblocker.tech.beans.SMS;

/**
 * Created by sagarpawar on 01/01/18.
 */

public class TelephonyUtilSingleton{
    private static final TelephonyUtilSingleton instance = new TelephonyUtilSingleton();

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    public static TelephonyUtilSingleton getInstance() {
        return instance;
    }

    private TelephonyUtilSingleton() {}

    public String getCurrentOperator(Context context){
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = tManager.getSimOperatorName();
        return carrierName;
    }

    public int getSimCount(Context context){
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int count = tManager.getPhoneCount();
        return count;
    }

    public List<SIM> getAvailableSims(Context context) {


        List<SIM> sims = new ArrayList<>();
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = tManager.getSimOperatorName();
        int simCount = getSimCount(context);


        SubscriptionManager subscriptionManager=(SubscriptionManager)context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        List<SubscriptionInfo> subscriptionInfoList=subscriptionManager.getActiveSubscriptionInfoList();

        if(subscriptionInfoList!=null && subscriptionInfoList.size()>0){
            for(SubscriptionInfo info:subscriptionInfoList){
                SIM sim = new SIM();
                String name = info.getDisplayName().toString();
                int id = info.getSubscriptionId();
                int slot = info.getSimSlotIndex();

                sim.setOperator(name);
                sim.setId(id);
                sim.setSlotNo(slot);

                sims.add(sim);
            }
        }

        return sims;
    }

}
