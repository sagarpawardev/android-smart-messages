package dev.sagar.smsblocker.tech.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.exceptions.NotImplementedException;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class InboxUtil {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android References
    private Context context;
    private InboxUtil reader = null;

    //Java Core References
    private final String _id = Telephony.Sms._ID;
    private final String address = Telephony.Sms.ADDRESS;
    private final String body = Telephony.Sms.BODY;
    private final String read = Telephony.Sms.READ;
    private final String date = Telephony.Sms.DATE;
    private final String type = Telephony.Sms.TYPE;
    private final Uri INBOX_URI = Telephony.Sms.Inbox.CONTENT_URI;
    private final Uri SENT_URI = Telephony.Sms.Sent.CONTENT_URI;
    private final Uri SMS_URI = Telephony.Sms.CONTENT_URI;
    public static final String TYPE_INBOX = "inbox";
    public static final String TYPE_SENT = "sent";
    public static final int SORT_DESC = 0;
    public static final int SORT_ASC = 1;

    public InboxUtil(Context context) {
        this.context = context;
    }


    /**
     * Helps in getting most recent SMSes from all contacts
     * @return key-value pair of contact_number and most_recent_message <Contact Number, Most Recent Message>
     */
    public Map<String, SMS> getMsgs(){
        final String methodName =  "getMsgs()";
        log.debug(methodName, "Just Entered..");

        Uri uriSMSURI = Uri.parse("content://sms/");
        String[] projection = {Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.READ,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE};
        String selection = "";
        String[] selectionArgs = {};
        String sortOrder = Telephony.Sms.DATE +" desc";

        Cursor c = context.getContentResolver()
                .query(uriSMSURI, projection, selection, selectionArgs, sortOrder);
        LinkedHashMap<String, SMS> smsMap = new LinkedHashMap<>();

        log.info(methodName, "Reading Messages..");
        if(c==null){
            log.info(methodName, "Query returned null cursor");
            return smsMap;
        }

        while (c.moveToNext()) {
            String id = c.getString(c.getColumnIndexOrThrow("_id"));
            String from = c.getString(c.getColumnIndexOrThrow("address"));
            String body = c.getString(c.getColumnIndexOrThrow("body"));
            boolean readState = c.getInt(c.getColumnIndex("read")) == 1;
            long time = c.getLong(c.getColumnIndexOrThrow("date"));
            long type = c.getLong(c.getColumnIndexOrThrow("type"));

            if(!smsMap.containsKey(from)) {
                SMS sms = new SMS();
                sms.setId(id);
                sms.setFrom(from);
                sms.setBody(body);
                sms.setRead(readState);
                sms.setDateTime(time);
                sms.setType(type);

                smsMap.put(from, sms);
            }

        }
        c.close();


        log.debug(methodName, "Returning..");
        return smsMap;
    }


    /**
     * This method returns all Received and Sent from a specified contact number with soring order Ascending or Descending
     * @param contactNo Phone Number to get SMS
     * @param sortingOrder Sorting order {valid values: InboxUtil.SORT_DESC, InboxUtil.SORT_ASC}
     * @return Returns list of SMS from contact number
     */
    public ArrayList<SMS> getAllSMSFromTo(String contactNo, int sortingOrder){
        final String methodName =  "getAllSMSFromTo()";
        log.debug(methodName, "Just Entered..");

        Uri uriSMSURI = Uri.parse("content://sms/");
        String[] projection = {_id,
                address,
                body,
                read,
                date,
                type};

        String selection = address+" = ?";
        String[] selectionArgs = {contactNo};
        String mSortOrder;
        switch (sortingOrder) {
            case SORT_DESC: mSortOrder = date+" DESC"; break;
            case SORT_ASC: mSortOrder = date+" ASC"; break;
            default: mSortOrder="";
        }

        Cursor c = context.getContentResolver()
                .query(uriSMSURI, projection, selection, selectionArgs, mSortOrder);

        ArrayList<SMS> smses = new ArrayList<>();
        Log.e("My TAG", "Reading Msg... ");

        try {
            while (c.moveToNext()) {

                String from = c.getString(c.getColumnIndexOrThrow(this.address));
                String id = c.getString(c.getColumnIndexOrThrow(this._id));
                String body = c.getString(c.getColumnIndexOrThrow(this.body));
                boolean readState = c.getInt(c.getColumnIndex(this.read)) == 1;
                long time = c.getLong(c.getColumnIndexOrThrow(this.date));
                long type = c.getLong(c.getColumnIndexOrThrow(this.type));

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

        log.debug(methodName, "Returning..");
        return smses;
    }


    /**
     * This method returns all Received and Sent from a specified contact number by default in Descending order
     * @param contactNo Phone Number to get SMS
     * @return List of SMS from a contract number
     */
    public List<SMS> getAllSMSFromTo(String contactNo){
        final String methodName =  "getAllSMSFromTo()";
        log.debug(methodName, "Just Entered..");

        List<SMS> smses = getAllSMSFromTo(contactNo, SORT_DESC);;

        log.debug(methodName, "Returning..");
        return smses;
    }


    /**
     * This method marks read of all SMS from a given phone number
     * @param fromNumber Phone Number to set Read status as true
     * @return Number of Fields updated
     */
    public int setStatusRead(String fromNumber){
        final String methodName = "setStatusRead()";
        log.debug(methodName, "Just Entered..");

        Uri uriSMSUri = Telephony.Sms.Inbox.CONTENT_URI;
        String selection = address+" = ?";
        String[] selectionArgs = {fromNumber};
        ContentValues values = new ContentValues();
        values.put(read, true);

        int updateCount = context
                .getContentResolver()
                .update(uriSMSUri, values, selection, selectionArgs);
        log.info(methodName, "Update Count: "+updateCount);

        log.debug(methodName, "Returning..");
        return updateCount;
    }


    /**
     * This Method heps in saving a SMS in DataProvider
     * @param sms SMS to save
     * @return ID of newly created SMS and null if not created
     */
    public String saveSMS(SMS sms, String messageType){
        final String methodName = "saveSMS()";
        log.debug(methodName, "Just Entered..");

        String from = sms.getFrom();
        String body = sms.getBody();
        String date = String.valueOf(sms.getDateTime());
        String read = String.valueOf(sms.isRead());


        ContentValues values = new ContentValues();
        values.put(this.address, from);
        values.put(this.body, body);
        values.put(this.read, sms.isRead());
        values.put(this.date, date);
        values.put(this.read, read);

        Uri createdDataUri = null;
        Uri folderUri = null;
        log.info(methodName, "Trying to insert in DataProvider");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            folderUri = SENT_URI;
            log.info(methodName, "Foldername: "+messageType);
            if(messageType.equals(TYPE_INBOX)){
                folderUri = INBOX_URI;
            }
            createdDataUri = context.getContentResolver().insert(folderUri, values);
        }
        else {
            log.info(methodName, "OS in older than Kitkat");
            createdDataUri = context.getContentResolver().insert(Uri.parse("content://sms/" + messageType), values);
        }
        log.info(methodName, "URI After insertion: "+createdDataUri);

        String result = null;
        if (createdDataUri != null)
            result = createdDataUri.toString().replace(SMS_URI.toString()+"/", "");
        log.debug(methodName, "Returning.. :"+result);
        return result;
    }


    /**
     * This method will one delete SMS from Inbox
     * @param sms SMS to delete
     * @return True if SMS deleted else false
     */
    public boolean deleteSMS(SMS sms){
        final String methodName =  "deleteSMS()";
        log.debug(methodName, "Just Entered..");

        String id = sms.getId();
        ContentValues values = new ContentValues();
        values.put(this._id, id);

        log.info(methodName, "Deleting SMS from DataProvider");

        String selection = _id+"= ?";
        String selectionArgs[] = {id};
        int count = context.getContentResolver().delete(SMS_URI, selection, selectionArgs);
        log.info(methodName, "Deleted "+count+" Rows");

        log.debug(methodName, "Returning..");
        return true;
    }


    /**
     * This method will delete all SMS from (or) SMS sent to, phoneNo
     * @param phoneNo Phne Number as Thread Id to deletes
     * @return Number of SMS deleted
     */
    public int deleteThread(String phoneNo){
        final String methodName =  "deleteThread()";
        log.debug(methodName, "Just Entered..");

        try{
            throw new NotImplementedException(this.getClass().getSimpleName(), methodName);
        }
        catch (NotImplementedException e){
            e.printStackTrace();
        }

        log.debug(methodName, "Returning..");
        return 0;
    }
}
