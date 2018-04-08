package dev.sagar.smsblocker.ux.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.Contact;
import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.customviews.DisplayPictureView;

/**
 * Created by sagarpawar on 24/03/18.
 */

public class ThemeUtil {

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private Context context;

    public ThemeUtil(Context context) {
        this.context = context;
    }

    public Typeface getTypeface(){
        final String methodName =  "getTypeface()";
        log.justEntered(methodName);

        Typeface myFont = Typeface.createFromAsset(context.getAssets(),"fonts/VarelaRound-Regular.ttf");

        log.returning(methodName);
        return myFont;
    }

    /**
     * Style DPView as per theme
     * @param dpView DisplayPictureView to
     * @param uri Uri of Display Picture
     * @param contact Contact for which display picture is to be stylized
     */
    public void styleDPView(DisplayPictureView dpView, Uri uri, Contact contact){
        final String methodName =  "styleDPView(DisplayPictureView, Uri, Contact)";
        log.justEntered(methodName);

        String fromName = contact.getDisplayName();
        if(uri != null) {
            dpView.setPictureSrc(uri);
        }
        else {
            if(fromName!=null && !fromName.startsWith("+")) {
                String c = String.valueOf(fromName.charAt(0));
                dpView.setLetterText(c);
            }
            else{
                String str = context.getResources().getString(R.string.hash);
                dpView.setLetterText(str);
            }
        }

        log.returning(methodName);
    }

    /**
     * Style DPView as per theme
     * @param dpView DisplayPictureView to
     * @param uri Uri of Display Picture
     * @param conversation Conversation for which display picture is to be stylized
     */
    public void styleDPView(DisplayPictureView dpView, Uri uri, Conversation conversation){
        final String methodName =  "styleDPView(DisplayPictureView, Uri, Conversation)";
        log.justEntered(methodName);

        String fromName = conversation.getContactName();
        if(uri != null) {
            dpView.setPictureSrc(uri);
        }
        else {
            if(fromName!=null) {
                String c = String.valueOf(fromName.charAt(0));
                dpView.setLetterText(c);
            }
            else{
                String str = context.getResources().getString(R.string.hash);
                dpView.setLetterText(str);
            }
        }

        log.returning(methodName);
    }
}
