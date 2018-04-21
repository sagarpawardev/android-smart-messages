package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import dev.sagar.smsblocker.tech.beans.SIM;

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

    private TelephonyUtilSingleton() {

    }

    public String getCurrentOperator(Context context){
        TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = tManager.getSimOperatorName();
        return carrierName;
    }

    public SIM getDefaultSmsSIM(Context context){

        SmsManager smsManager = SmsManager.getDefault();
        int subsId = smsManager.getSubscriptionId();
        SIM defaultSIM = getSim(context, subsId);

        return defaultSIM;
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
                sim.setSubscriptionId(id);
                sim.setSlotNo(slot);

                sims.add(sim);
            }
        }

        return sims;
    }

    public SIM getSim(Context context, int subscriptionId){
        SIM sim = new SIM();
        SubscriptionManager subscriptionManager=(SubscriptionManager)context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        //TODO Check android.permission.READ_PHONE_STATE here
        List<SubscriptionInfo> subscriptionInfoList=subscriptionManager.getActiveSubscriptionInfoList();

        if(subscriptionInfoList!=null && subscriptionInfoList.size()>0){
            for(SubscriptionInfo info:subscriptionInfoList){
                int slot = info.getSimSlotIndex();
                String name = info.getDisplayName().toString();
                int subsId = info.getSubscriptionId();
                if( subsId == subscriptionId){
                    sim.setOperator(name);
                    sim.setSubscriptionId(subsId);
                    sim.setSlotNo(slot);
                    break;
                }
            }
        }

        return sim;
    }


    public SIM getSIMAtSlot(Context context, int slotIndex){
        SIM sim = new SIM();
        SubscriptionManager subscriptionManager=(SubscriptionManager)context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);

        List<SubscriptionInfo> subscriptionInfoList=subscriptionManager.getActiveSubscriptionInfoList();

        if(subscriptionInfoList!=null && subscriptionInfoList.size()>0){
            for(SubscriptionInfo info:subscriptionInfoList){
                int slot = info.getSimSlotIndex();
                String name = info.getDisplayName().toString();
                int subsId = info.getSubscriptionId();
                if( slot == slotIndex){
                    sim.setOperator(name);
                    sim.setSubscriptionId(subsId);
                    sim.setSlotNo(slot);
                    break;
                }
            }
        }

        return sim;
    }

}
