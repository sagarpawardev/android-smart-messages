package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

/**
 * Created by sagarpawar on 22/02/18.
 */

public class PhoneUtilsSingleton {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    private static PhoneUtilsSingleton instance = new PhoneUtilsSingleton();

    private PhoneUtilsSingleton(){

    }
    public static PhoneUtilsSingleton getInstance(){
        return instance;
    }


    public String formatNumber(Context context, String unformattedNumber){
        final String methodName =  "formatNumber(Context, String)";
        log.justEntered(methodName);
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();

        String formattedNumber;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formattedNumber = PhoneNumberUtils.formatNumberToE164(unformattedNumber, countryCode);
        } else {
            formattedNumber = PhoneNumberUtils.formatNumber(unformattedNumber, countryCode);
        }
        if(formattedNumber == null){
            //formattedNumber = unformattedNumber.replaceAll("[-,+]","");
            formattedNumber = unformattedNumber.replaceAll("[+]","");
        }

        log.returning(methodName);
        return formattedNumber;
    }

    public boolean isReplySupported(String address){
        final String methodName =  "isReplySupported(String)";
        log.justEntered(methodName);

        //TODO Change Logic Here
        address = address.toUpperCase();
        boolean result = !( (address.charAt(0)<='Z' && address.charAt(0)>='A') && address.length()<10 ); //If Address is VK-Mumbai or 56065
        log.error(methodName, "This Logic is valid only in India");

        log.returning(methodName);
        return result;
    }
}
