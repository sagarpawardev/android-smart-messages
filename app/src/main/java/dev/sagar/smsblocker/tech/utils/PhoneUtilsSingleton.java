package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

/**
 * Created by sagarpawar on 22/02/18.
 */

public class PhoneUtilsSingleton {

    private static PhoneUtilsSingleton instance = new PhoneUtilsSingleton();

    private PhoneUtilsSingleton(){

    }
    public static PhoneUtilsSingleton getInstance(){
        return instance;
    }


    public String formatNumber(Context context, String unformattedNumber){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();

        String formattedNumber;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            formattedNumber = PhoneNumberUtils.formatNumberToE164(unformattedNumber, countryCode);
        } else {
            formattedNumber = PhoneNumberUtils.formatNumber(unformattedNumber, countryCode);
        }
        if(formattedNumber == null){
            formattedNumber = unformattedNumber.replaceAll("[-,+]","");
        }
        return formattedNumber;
    }
}
