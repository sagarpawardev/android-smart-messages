package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;

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

    public String encode(String str){
        final String methodName =  "encode(String)";
        log.justEntered(methodName);

        log.error(methodName, "This Logic valid only in India");
        //FIXME For following cases
        // 1. SearchStr: 98989898       DBString: +9198989898 or 098989898
        // 2. SearchStr: +9198989898    DBString: 98989898
        StringBuffer sb = new StringBuffer();


        if(str.startsWith("+91")){
            str = str.replaceFirst("\\+91", "");
        }
        else if(str.startsWith("0")){
            str = str.replaceFirst("0","");
        }

        if(str.length()>=10){ //% at starting of string
            sb.append('%');
        }

        for(char c: str.toCharArray()){
            if(c==' ' || c=='(' || c==')'){
                c = '%';
            }
            sb.append(c);
        }

        if(str.length()>=10){ //% at end of string
            sb.append('%');
        }

        log.debug(methodName, "Encoded String: "+sb.toString());
        //String become something like '%9876543%'
        log.returning(methodName);
        return sb.toString();
    }

    public boolean isReplySupported(String address){
        final String methodName =  "isReplySupported(String)";
        log.justEntered(methodName);

        //TODO Change Logic Here
        /*address = address.toUpperCase();
        log.info(methodName, "Checking for address: "+address);
        boolean result = !( (address.charAt(0)<='Z' && address.charAt(0)>='A') && address.length()<10 ); //If Address is VK-Mumbai or 56065
        log.error(methodName, "This Logic is valid only in India");
        log.debug(methodName, "I will return : "+result);
        log.returning(methodName);*/

        boolean result;
        if (TextUtils.isEmpty(address)) {
            log.error(methodName, "Address in Empty...");
            result = false;
        }else {
            final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(address, Locale.getDefault().getCountry());
                PhoneNumberUtil.PhoneNumberType phoneNumberType = phoneNumberUtil.getNumberType(phoneNumber);

                result = (phoneNumberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE) ||
                        (phoneNumberType == PhoneNumberUtil.PhoneNumberType.MOBILE);


                log.info(methodName, "Got result: "+result+" For address: "+address+" Type: "+phoneNumberType);
            } catch (final Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
                result = false;
                log.info(methodName, "Fall in exception...");
            }
        }

        log.returning(methodName);
        return result;
    }

    public String normalizeAddress(String unformattedAddress){
        String normalizedAddress = PhoneNumberUtils.normalizeNumber(unformattedAddress);
        return normalizedAddress;
    }
}
