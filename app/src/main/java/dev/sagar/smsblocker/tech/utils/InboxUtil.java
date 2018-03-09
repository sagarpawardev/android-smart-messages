package dev.sagar.smsblocker.tech.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.List;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.service.DBServiceSingleton;
import dev.sagar.smsblocker.tech.handlers.ReadStatusHandler;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class InboxUtil {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android References
    private Context context;
    private InboxUtil reader = null;
    private DBServiceSingleton dbService = DBServiceSingleton.getInstance();

    //Java Core References
    private final String _id = Telephony.Sms._ID;
    private final String address = Telephony.Sms.ADDRESS;
    private final String threadId = Telephony.Sms.THREAD_ID;
    private final String body = Telephony.Sms.BODY;
    private final String subscriptionId = Telephony.Sms.SUBSCRIPTION_ID;
    private final String read = Telephony.Sms.READ;
    private final String date = Telephony.Sms.DATE;
    private final String type = Telephony.Sms.TYPE;
    private final String replySupported = Telephony.Sms.REPLY_PATH_PRESENT;
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
    public IndexedHashMap<String, SMS> getLatestMsgs(){
        final String methodName =  "getLatestMsgs()";
        log.justEntered(methodName);

        final String m_threadid = Telephony.Sms.Conversations.THREAD_ID;
        final String mSnippet = Telephony.Sms.Conversations.SNIPPET;
        final String mMessageCount = Telephony.Sms.Conversations.MESSAGE_COUNT;
        final String mDate = Telephony.Sms.Conversations.DATE;

        ContentResolver mContentResolver = context.getContentResolver();
        Uri mConversationUri = Telephony.Sms.Conversations.CONTENT_URI;
        IndexedHashMap<String, SMS> smsMap=new IndexedHashMap<>();


        String[] mProjection = {m_threadid, mSnippet, mMessageCount};
        String mSelection = "";
        String[] mSelectionArgs = {};
        String mSortOrder = mDate +" DESC";

        Cursor mConversation = dbService.query(mContentResolver,
                mConversationUri, mProjection, mSelection, mSelectionArgs, mSortOrder);

        log.info(methodName, "Reading Messages..");
        if (mConversation == null) {
            log.info(methodName, "Conversation Query returned null cursor");
            return smsMap;
        }

        log.info(methodName, "Conversation Count: "+mConversation.getCount());

        //Collecting ThreadId from Conversation Starts
        String[] conversations = new String[mConversation.getCount()];

        int count = 0;
        while (mConversation.moveToNext()){
            conversations[count++] = mConversation.getString(mConversation.getColumnIndexOrThrow(m_threadid));
        }

        Uri mLatestSmsUri = Telephony.Sms.CONTENT_URI;
        String[] mLatestSmsProjection = {_id, address, body,
                read, date, subscriptionId, type};

        StringBuilder sb = new StringBuilder(threadId+" IN (");
        for(int i=0; i<conversations.length; i++){
            sb.append("?");
            if(i!=conversations.length-1) { //If not last index
                sb.append(",");
            }
        }
        sb.append(")");

        String mLatestSmsSelection = sb.toString();
        String[] mLatestSmsSelectionArgs = conversations;
        String mLatestSmsSortOrder = this.date +" desc";
        //-- Collecting ThreadId from Conversation Ends

        Cursor mLatestSmsCursor = mContentResolver
                .query(mLatestSmsUri, mLatestSmsProjection, mLatestSmsSelection, mLatestSmsSelectionArgs, mLatestSmsSortOrder);
        if (mLatestSmsCursor == null) {
            log.info(methodName, "Latest SMS Query returned null cursor");
            return smsMap;
        }
        while (mLatestSmsCursor.moveToNext()){

            String id = mLatestSmsCursor.getString(mLatestSmsCursor.getColumnIndexOrThrow(this._id));
            String from = mLatestSmsCursor.getString(mLatestSmsCursor.getColumnIndexOrThrow(this.address));
            String body = mLatestSmsCursor.getString(mLatestSmsCursor.getColumnIndexOrThrow(this.body));
            int serviceCenter = mLatestSmsCursor.getInt(mLatestSmsCursor.getColumnIndexOrThrow(this.subscriptionId));
            boolean readState = mLatestSmsCursor.getInt(mLatestSmsCursor.getColumnIndex(this.read)) == 1;
            long time = mLatestSmsCursor.getLong(mLatestSmsCursor.getColumnIndexOrThrow(this.date));
            long type = mLatestSmsCursor.getLong(mLatestSmsCursor.getColumnIndexOrThrow(this.type));

            if(smsMap.containsKey(from)){
                continue;
            }

            SMS sms = new SMS();
            sms.setId(id);
            sms.setFrom(from);
            sms.setBody(body);
            sms.setRead(readState);
            sms.setDateTime(time);
            sms.setType(type);
            sms.setSubscription(serviceCenter);

            log.debug(methodName, from+"\t"+body+"\t"+id);

            smsMap.put(from, sms);
        }

        mLatestSmsCursor.close();
        mConversation.close();

        log.returning(methodName);
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
        log.justEntered(methodName);

        Uri uriSmsURI = Telephony.Sms.CONTENT_URI;
        String[] projection = {"*"};

        String selection = address+" = ?";
        String[] selectionArgs = {contactNo};
        String mSortOrder;
        switch (sortingOrder) {
            case SORT_DESC: mSortOrder = date+" DESC"; break;
            case SORT_ASC: mSortOrder = date+" ASC"; break;
            default: mSortOrder="";
        }

        ContentResolver mContentResolver = context.getContentResolver();
        Cursor c = dbService
                .query(mContentResolver, uriSmsURI, projection, selection, selectionArgs, mSortOrder);

        ArrayList<SMS> smses = new ArrayList<>();
        log.info(methodName, "Reading Msg... ");

        try {
            while (c.moveToNext()) {

                String from = c.getString(c.getColumnIndexOrThrow(this.address));
                String id = c.getString(c.getColumnIndexOrThrow(this._id));
                String body = c.getString(c.getColumnIndexOrThrow(this.body));
                int subscriptionId = c.getInt(c.getColumnIndexOrThrow(this.subscriptionId));
                boolean readState = c.getInt(c.getColumnIndex(this.read)) == 1;
                long time = c.getLong(c.getColumnIndexOrThrow(this.date));
                long type = c.getLong(c.getColumnIndexOrThrow(this.type));
                boolean replySupported = PhoneUtilsSingleton.getInstance().isReplySupported(from);

                SMS sms = new SMS();
                sms.setId(id);
                sms.setFrom(from);
                sms.setBody(body);
                sms.setRead(readState);
                sms.setDateTime(time);
                sms.setType(type);
                sms.setSubscription(subscriptionId);
                sms.setReplySupported(replySupported);

                smses.add(sms);

                log.debug(methodName, "Address: "+from+" ReplySupported: "+c.getString(c.getColumnIndex(this.replySupported)));

            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (c!=null) c.close();
        }

        log.returning(methodName);
        return smses;
    }


    /**
     * This method returns all Received and Sent from a specified contact number by default in Descending order
     * @param contactNo Phone Number to get SMS
     * @return List of SMS from a contract number
     */
    public List<SMS> getAllSMSFromTo(String contactNo){
        final String methodName =  "getAllSMSFromTo()";
        log.justEntered(methodName);

        List<SMS> smses = getAllSMSFromTo(contactNo, SORT_DESC);;

        log.returning(methodName);
        return smses;
    }


    /**
     * This method marks read of all SMS from a given phone number
     * @param fromNumber Phone Number to set Read status as true
     * @return Number of Fields updated
     */
    public void setStatusRead(String fromNumber){
        final String methodName = "setStatusRead()";
        log.justEntered(methodName);

        /*log.error(methodName, "Can be improved Here");

        Uri uriSMSUri = Telephony.Sms.Inbox.CONTENT_URI;
        String selection = address+" = ?";
        String[] selectionArgs = {fromNumber};
        ContentValues values = new ContentValues();
        values.put(read, true);

        int updateCount = context
                .getContentResolver()
                .update(uriSMSUri, values, selection, selectionArgs);
        log.info(methodName, "Update Count: "+updateCount);*/

        new ReadStatusHandler(context).execute(fromNumber);

        log.returning(methodName);
    }


    /**
     * This Method heps in saving a SMS in DataProvider
     * @param sms SMS to save
     * @return ID of newly created SMS and null if not created
     */
    public String insertSMS(SMS sms){
        final String methodName = "insertSMS()";
        log.justEntered(methodName);

        String from = sms.getFrom();
        String body = sms.getBody();
        String date = String.valueOf(sms.getDateTime());
        String read = String.valueOf(sms.isRead());
        long type = sms.getType();
        int subscription = sms.getSubscription();

        ContentValues values = new ContentValues();
        values.put(this.address, from);
        values.put(this.body, body);
        values.put(this.read, sms.isRead());
        values.put(this.date, date);
        values.put(this.read, read);
        values.put(this.type, type);
        values.put(this.subscriptionId, subscription);

        Uri createdDataUri = null;
        log.info(methodName, "Trying to insert in DataProvider");

        createdDataUri =  dbService.insert(context, INBOX_URI, values);

        log.info(methodName, "URI After insertion: "+createdDataUri);

        String result = null;
        if (createdDataUri != null)
            result = createdDataUri.toString().replace(SMS_URI.toString()+"/", "");
        log.info(methodName, "Result: "+result);

        log.returning(methodName);
        return result;
    }


    /**
     * This method will one delete SMS from Inbox
     * @param sms SMS to delete
     * @return True if SMS deleted else false
     */
    public boolean deleteSMS(SMS sms){
        final String methodName =  "deleteSMS()";
        log.justEntered(methodName);

        String id = sms.getId();
        ContentValues values = new ContentValues();
        values.put(this._id, id);

        log.info(methodName, "Deleting SMS from DataProvider");

        String selection = _id+"= ?";
        String selectionArgs[] = {id};
        int count = dbService.delete(context, SMS_URI, selection, selectionArgs);
        log.info(methodName, "Deleted "+count+" Rows");

        log.returning(methodName);
        return true;
    }


    /**
     * This method will delete all SMS from (or) SMS sent to, phoneNo
     * @param phoneNo Phne Number as Thread Id to deletes
     * @return Number of SMS deleted
     */
    public int deleteThread(String phoneNo){
        final String methodName =  "deleteThread()";
        log.justEntered(methodName);

        //TODO: Instead od delete messages one by one delete all message at once. Also provide methods to delete from multipleThreads
        log.error(methodName, "Performance issues Temporarily adding. Need to remove later");
        ContentResolver contentResolver = context.getContentResolver();
        int count=0;
        List<SMS> smses = getAllSMSFromTo(phoneNo);
        for(SMS sms: smses){
            String id = sms.getId();
            Uri tempUri = Uri.withAppendedPath(SMS_URI, Uri.encode(id));
            int tempCount = contentResolver.delete(tempUri, null, null);
            log.debug(methodName,"Delete Count: "+tempCount);
            count += tempCount;
        }

        /*String selection = address+" = ?";
        String[] selectionArgs = {phoneNo};
        log.debug(methodName, "Deleting message from: "+phoneNo);
        int deleteCount = context.getContentResolver()
                .delete(uriSmsURI, selection, selectionArgs);
        log.debug(methodName, "Delete Count: "+deleteCount);

        try{
            throw new NotImplementedException(this.getClass().getSimpleName(), methodName);
        }
        catch (NotImplementedException e){
            e.printStackTrace();
        }*/

        log.returning(methodName);
        return count;
    }

}
