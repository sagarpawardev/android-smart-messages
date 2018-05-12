package dev.sagar.smsblocker.tech.utils;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import dev.sagar.smsblocker.tech.RequestCode;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.broadcastreceivers.SMSDeliveredReceiver;
import dev.sagar.smsblocker.tech.broadcastreceivers.SMSSentReceiver;

/**
 * Created by sagarpawar on 22/10/17.
 */

public class SMSUtil {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android
    private Context context;
    private final Gson gson = new Gson();

    //Java Core

    public SMSUtil(Context context) {
        this.context = context;
    }

    /**
     * This method sends sms to specified contact number
     * @param phoneNo
     * @param msg
     */
    public SMS sendSMS(String phoneNo, String msg) {
        String methodName = "sendSMS()";
        log.justEntered(methodName);

        SMS sms = null;
        try {
            SmsManager smsManager = SmsManager.getDefault();
            int subsId = smsManager.getSubscriptionId();

            boolean isAppDefault = PermissionUtilSingleton.getInstance().isAppDefault(context);
            sms = new SMS();
            sms.setRead(true);
            sms.setType(SMS.TYPE_QUEUED);
            sms.setDateTime(System.currentTimeMillis());
            sms.setBody(msg);
            sms.setAddress(phoneNo);
            sms.setSubscription(subsId);

            ArrayList<String> parts =smsManager.divideMessage(msg);

            ArrayList<PendingIntent> sentPIntents = null;
            ArrayList<PendingIntent> deliverPIntents = null;
            if(isAppDefault) {
                log.info(methodName, "Saving SMS in DB");
                //Save in DataProvider
                InboxUtil inboxUtil = new InboxUtil(context);
                String id = inboxUtil.insertSMS(sms);
                sms.setId(id);
                log.info(methodName, "SMS Saved with id: "+id);


                log.info(methodName, "Creating Delivery and Sent Receivers");
                int numParts = parts.size();

                sentPIntents = new ArrayList<>();
                deliverPIntents = new ArrayList<>();
                for(int i=0; i<numParts; i++){
                    Intent sentIntent = new Intent(context, SMSSentReceiver.class);
                    Bundle sendBasket = new Bundle();
                    Intent deliverIntent = new Intent(context, SMSDeliveredReceiver.class);
                    Bundle deliverBasket = new Bundle();


                    sendBasket.putInt(SMSSentReceiver.KEY_PART_INDEX, i);
                    deliverBasket.putSerializable(SMSDeliveredReceiver.KEY_PART_INDEX, i);

                    String strSms = gson.toJson(sms);
                    sendBasket.putString(SMSSentReceiver.KEY_SMS, strSms);
                    deliverBasket.putString(SMSDeliveredReceiver.KEY_SMS, strSms);

                    sentIntent.putExtras(sendBasket);
                    deliverIntent.putExtras(deliverBasket);

                    sendBasket.putInt(SMSSentReceiver.KEY_TOTAL_PARTS, numParts);
                    deliverBasket.putSerializable(SMSDeliveredReceiver.KEY_TOTAL_PARTS, numParts);

                    log.info(methodName, "putting sent action in pendingIntent: "+ActionCode.SMS_SENT);

                    sentIntent.setAction(ActionCode.SMS_SENT);
                    deliverIntent.setAction(ActionCode.SMS_DELIVERED);

                    PendingIntent sentPIntent = PendingIntent.getBroadcast(context, RequestCode.SMS_SENT, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT );
                    PendingIntent deliverPIntent = PendingIntent.getBroadcast(context, RequestCode.SMS_DELIVERED, deliverIntent, PendingIntent.FLAG_UPDATE_CURRENT );


                    sentPIntents.add(sentPIntent);
                    deliverPIntents.add(deliverPIntent);
                }
                log.info(methodName, "Delivery and Sent Receivers Created");
            }


            //Send SMS
            log.info(methodName,"Trying to Send SMS");

            smsManager.sendMultipartTextMessage(phoneNo, null, parts, sentPIntents, deliverPIntents);


            //smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            log.info(methodName, "SMS Queued");



        } catch (Exception e) {
            log.error(methodName, "Sending Failed");
            Toast.makeText(context, "Give File permissions manually. (Debug)", Toast.LENGTH_LONG).show();
            log.error(methodName, e);
            e.printStackTrace();
        }

        log.returning(methodName);
        return sms;
    }

    /**
     * Returns SMSManager for specified sim
     * @param simIndex Index of SIM 0 for Sim1 and 1 for Sim2
     * @return SMSManager for specified index or Default if SDK < 22
     */
    private SmsManager getSMSManagerFor(int simIndex){
        String methodName = "getSMSManagerFor()";
        log.justEntered(methodName);

        SmsManager smsManager = null;

        if (Build.VERSION.SDK_INT >= 22) {
            SubscriptionManager subscriptionManager = context.getSystemService(SubscriptionManager.class);
            log.info(methodName, "Looking for SMS manager for simIndex: "+simIndex);
            SubscriptionInfo subscriptionInfo=subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(simIndex);
            int subscriptionId = subscriptionInfo.getSubscriptionId();
            smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
            log.info(methodName, "SIM found ");
        }
        else{
            log.info(methodName, "SDK Lower than 22 so returning default SMSManager");
            smsManager = SmsManager.getDefault();
        }

        log.justEntered(methodName);
        return smsManager;
    }


}
