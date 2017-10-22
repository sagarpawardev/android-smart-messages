package dev.sagar.smsblocker.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import dev.sagar.smsblocker.beans.SMS;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class InboxReaderUtil {

    private Context context;
    private InboxReaderUtil reader = null;

    public InboxReaderUtil(Context context) {
        this.context = context;
    }

    //Returns Map of Threads
    public Map<String, ArrayList<SMS>> getMsgs(){
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor c = context.getContentResolver().query(uriSMSURI, null, null, null,null);

        LinkedHashMap<String, ArrayList<SMS>> smsMap = new LinkedHashMap<>();

        Log.e("My TAG", "Reading Msg... ");
        try {
            while (c.moveToNext()) {

                String id = c.getString(c.getColumnIndexOrThrow("_id"));
                String from = c.getString(c.getColumnIndexOrThrow("address"));
                String body = c.getString(c.getColumnIndexOrThrow("body"));
                boolean readState = c.getInt(c.getColumnIndex("read")) == 1;
                long time = c.getLong(c.getColumnIndexOrThrow("date"));
                long type = c.getLong(c.getColumnIndexOrThrow("type"));

                SMS sms = new SMS();
                sms.setId(id);
                sms.setFrom(from);
                sms.setBody(body);
                sms.setRead(readState);
                sms.setDateTime(time);
                sms.setType(type);

                ArrayList<SMS> smses;
                if(smsMap.containsKey(from))
                    smses = smsMap.get(from);
                else {
                    smses = new ArrayList<>();
                    smsMap.put(from, smses);
                }

                smses.add(sms);

            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (c!=null) c.close();
        }

        return smsMap;
    }

    //Returns All SMS From/To contactNo
    public ArrayList<SMS> getAllSMSFromTo(String contactNo){
        Uri uriSMSURI = Uri.parse("content://sms/");
        String[] projection = {Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.READ,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE};

        String selection = "address = ?";
        String[] selectionArgs = {contactNo};
        String mSortOrder = "date DESC";

        Cursor c = context.getContentResolver()
                .query(uriSMSURI, projection, selection, selectionArgs, mSortOrder);

        ArrayList<SMS> smses = new ArrayList<>();
        Log.e("My TAG", "Reading Msg... ");

        try {
            while (c.moveToNext()) {

                String from = c.getString(c.getColumnIndexOrThrow("address"));
                String id = c.getString(c.getColumnIndexOrThrow("_id"));
                String body = c.getString(c.getColumnIndexOrThrow("body"));
                boolean readState = c.getInt(c.getColumnIndex("read")) == 1;
                long time = c.getLong(c.getColumnIndexOrThrow("date"));
                long type = c.getLong(c.getColumnIndexOrThrow("type"));

                SMS sms = new SMS();
                sms.setId(id);
                sms.setFrom(from);
                sms.setBody(body);
                sms.setRead(readState);
                sms.setDateTime(time);
                sms.setType(type);

                smses.add(sms);

            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (c!=null) c.close();
        }

        return smses;
    }


}
