package dev.sagar.smsblocker.tech.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.service.DBConstants;
import dev.sagar.smsblocker.tech.service.DBServiceSingleton;
import dev.sagar.smsblocker.tech.handlers.MarkSMSReadHandler;
import dev.sagar.smsblocker.tech.service.helper.DBHelper;
import dev.sagar.smsblocker.tech.service.helper.savedsms.SavedSMSDBAttributes;

/**
 * Created by sagarpawar on 15/10/17.
 */

public class InboxUtil {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android References
    private Context context;
    private DBServiceSingleton dbService = DBServiceSingleton.getInstance();
    private PhoneUtilsSingleton phoneUtils = PhoneUtilsSingleton.getInstance();

    //Java Core References
    private Callback callback;

    //Java Constants
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

    //Reference for Starred_SMS
    private final String starredsms_id = SavedSMSDBAttributes.SavedSMS.COLUMN_NAME_ID;
    private final String starredsms_address = SavedSMSDBAttributes.SavedSMS.COLUMN_NAME_ADDRESS;
    private final String starredsms_dateadded = SavedSMSDBAttributes.SavedSMS.COLUMN_DATE_ADDED;

    public InboxUtil(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

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
            sms.setAddress(from);
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
    public void getAllSMSFromTo(final String contactNo, final int sortingOrder){
        final String methodName =  "getAllSMSFromTo()";
        log.justEntered(methodName);

        if(callback != null) {
            AsyncTask<Void, Void, List<SMS>> asyncTask = new AsyncTask<Void, Void, List<SMS>>() {
                @Override
                protected List<SMS> doInBackground(Void... voids) {
                    List<SMS> smses = bgGetAllSMSFromTo(contactNo, sortingOrder);
                    return smses;
                }

                @Override
                protected void onPostExecute(List<SMS> smses) {
                    callback.onAllSMSFromToResult(smses);
                    super.onPostExecute(smses);
                }
            };
            asyncTask.execute();
        }
        else{
            throw new UnsupportedOperationException("SGR. This Operation is not supported. Because callback is null. because of wrong constructor call");
        }

        log.returning(methodName);
        //return smses;
    }


    /**
     * This method returns all Received and Sent from a specified contact number by default in Descending order
     * @param contactNo Phone Number to get SMS
     * @return List of SMS from a contract number
     */
    public void getAllSMSFromTo(String contactNo){
        final String methodName =  "getAllSMSFromTo()";
        log.justEntered(methodName);

        getAllSMSFromTo(contactNo, SORT_DESC);

        log.returning(methodName);
        //return smses;
    }


    /**
     * This method marks read of all SMS from a given phone number
     * @param fromNumber Phone Number to set Read status as true
     * @return Number of Fields updated
     */
    public void markSMSRead(String fromNumber){
        final String methodName = "markSMSRead()";
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
        new MarkSMSReadHandler(context).execute(fromNumber);

        log.returning(methodName);
    }

    public void markSMSRead(Set<String> fromNumber){
        final String methodName = "markSMSRead(Set<String>)";
        log.justEntered(methodName);

        //Mark SMS read
        for(String conversationId: fromNumber) {
            new MarkSMSReadHandler(context).execute(conversationId);
        }

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

        String from = sms.getAddress();
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
        List<SMS> smses = bgGetAllSMSFromTo(phoneNo, SORT_DESC);
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


    /**
     * This method will mark star in DB
     * @param sms SMS to Star
     * @return true if starred else false
     */
    public boolean starSMS(SMS sms){
        final String methodName =  "starSMS(SMS)";
        log.justEntered(methodName);

        String id = sms.getId();
        long currtime = System.currentTimeMillis();
        //String address = PhoneUtilsSingleton.getInstance().formatNumber(context, sms.getAddress());
        String address = sms.getAddress();
        ContentValues values = new ContentValues();
        values.put(this.starredsms_id, id);
        values.put(this.starredsms_address, address);
        values.put(this.starredsms_dateadded, currtime);

        log.info(methodName, "Starring SMS into database Address: "+address+" Id: "+id);

        boolean result= dbService.insert(context, DBConstants.TABLE_SAVEDSMS, values);
        log.info(methodName, "Received insert() result: "+result);

        log.returning(methodName);
        return true;
    }

    /**
     * This method will unstar SMSes in DB
     * @param smses List of SMSes to unstar
     * @return number of smses unstared in DB
     */
    public int unstarSMSes(List<SMS> smses){
        final String methodName =  "unstarSMSes(List<SMS>)";
        log.justEntered(methodName);

        int count = -1;

        if(smses.size()>0) {
            String whereClause = "";
            StringBuilder sbWhereClause = new StringBuilder(whereClause);
            String[] whereArgs = new String[2*smses.size()]; //(address, id) pair

            /*sbWhereClause.append("(");
            sbWhereClause.append(starredsms_address);
            sbWhereClause.append(",");
            sbWhereClause.append(starredsms_id);
            sbWhereClause.append(")");

            sbWhereClause.append(" IN (");*/
            for (int i = 0; i < smses.size(); i++) {
                SMS sms = smses.get(i);
                sbWhereClause.append("(");
                sbWhereClause.append(starredsms_address+" = ? AND ");
                sbWhereClause.append(starredsms_id+" = ? ");
                sbWhereClause.append(")");

                if (i < smses.size() - 1) { //Last ? does not contain ,
                    sbWhereClause.append(" OR ");
                }
                whereArgs[2*i] = sms.getAddress();
                whereArgs[2*i + 1] = sms.getId();
                log.debug(methodName, "Unstarring SMS address: " + whereArgs[2*i] +" id: "+whereArgs[2*i + 1]);
            }
            /*sbWhereClause.append(")");*/

            whereClause = sbWhereClause.toString();
            log.info(methodName, "Query built for deletion: " + whereClause);

            count = dbService.delete(context, DBConstants.TABLE_SAVEDSMS, whereClause, whereArgs);
        }
        else{
            //Nothing
        }

        log.returning(methodName);
        return count;
    }

    /**
     * This method return all the Saves SMS in SAVEDSMS database
     * @param sortOrder SORT_ASC or SORT_DEC
     * @return list of starred sms
     */
    public List<SMS> getSavedSMSes(int sortOrder){
        final String methodName =  "getSavedSMSes(int)";
        log.justEntered(methodName);

        List<SMS> smses = new ArrayList<>();

        dbService = DBServiceSingleton.getInstance();
        SQLiteOpenHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] mProjection = {starredsms_address, starredsms_id};
        String mSelection = "";
        String[] mSelectionArgs = {};
        String mSortOrder = starredsms_dateadded;
        switch (sortOrder){
            case SORT_ASC: mSortOrder = mSortOrder + " ASC"; break;
            case SORT_DESC: mSortOrder = mSortOrder +" DESC"; break;
            default: log.error(methodName, "Unknown Sorting order");
        }

        //Create Query for Saved SMS Database
        Cursor c = dbService.query(db, DBConstants.TABLE_SAVEDSMS, mProjection, mSelection, mSelectionArgs, mSortOrder );
        ArrayList<String> values = new ArrayList<>();
        StringBuilder sbQuery = new StringBuilder();
        log.debug(methodName, "Saved Message Count: "+c.getCount());
        while(c.moveToNext()){
            String address = c.getString(c.getColumnIndexOrThrow(this.starredsms_address));
            String id = c.getString(c.getColumnIndexOrThrow(this.starredsms_id));

            sbQuery.append("(");
            /*sbQuery.append(this.address+"=?");
            sbQuery.append(" AND ");*/
            sbQuery.append(this._id+"=?");
            sbQuery.append(" ) ");
            if(!c.isLast())
                sbQuery.append(" OR ");
            //values.add(address);
            values.add(id);
        }
        c.close();
        db.close();
        dbHelper.close();

        if(values.size()>0) {
            //Querying SMS Content Provider
            mProjection = new String[]{"*"};
            mSelection = sbQuery.toString();
            mSelectionArgs = values.toArray(new String[values.size()]);
            mSortOrder = null;

            ContentResolver resolver = context.getContentResolver();
            c = dbService.query(resolver, DBConstants.URI_INBOX, mProjection, mSelection, mSelectionArgs, mSortOrder);
            log.debug(methodName, "Saved Reading SMS Count: " + c.getCount());
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
                    sms.setAddress(from);
                    sms.setBody(body);
                    sms.setRead(readState);
                    sms.setDateTime(time);
                    sms.setType(type);
                    sms.setSubscription(subscriptionId);
                    sms.setReplySupported(replySupported);
                    sms.setSaved(true);

                    smses.add(sms);

                    log.debug(methodName, "Address: " + from + " ReplySupported: " + sms.isReplySupported());

                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            } finally {
                c.close();
            }
        }
        else{
            log.info(methodName, "There are no Saved SMS in Database :)");
        }

        log.returning(methodName);
        return smses;
    }



    private List<SMS>  bgGetAllSMSFromTo(String contactNo, int sortingOrder){
        final String methodName =  "bgGetAllSMSFromTo(String, int)";
        log.justEntered(methodName);

        //Reading Saved SMSes
        String selection = address+" LIKE ?";
        String[] projection = {starredsms_id};
        String tableName = DBConstants.TABLE_SAVEDSMS;
        String encodedAddress = phoneUtils.encode(contactNo);
        String[] selectionArgs = {encodedAddress};

        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        DBServiceSingleton dbService = DBServiceSingleton.getInstance();
        Cursor cursor = dbService.query(db, tableName, projection, selection, selectionArgs, null);
        log.debug(methodName, "Saved SMS Returned Row Count: "+cursor.getCount()+" Selection: "+selection+" Args: "+selectionArgs[0]);
        HashSet<String> set = new HashSet<>();
        try{
            while(cursor.moveToNext()){
                String id = cursor.getString(cursor.getColumnIndexOrThrow(this.starredsms_id));
                set.add(id);
            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        finally {
            if (cursor!=null) cursor.close();
            db.close();
            helper.close();
        }
        log.debug(methodName, "Saved SMS Set size: "+set.size());


        //Reading SMSes from database
        Uri uriSmsURI = Telephony.Sms.CONTENT_URI;

        selection = address+" = ?";
        projection = new String[]{"*"};
        selectionArgs = new String[]{contactNo};
        String mSortOrder = null;
        switch (sortingOrder) {
            case SORT_DESC: mSortOrder = date+" DESC"; break;
            case SORT_ASC: mSortOrder = date+" ASC"; break;
            default: mSortOrder="";
        }

        ContentResolver mContentResolver = context.getContentResolver();
        Cursor c = dbService
                .query(mContentResolver, uriSmsURI, projection, selection, selectionArgs, mSortOrder);

        ArrayList<SMS> smses = new ArrayList<>();
        log.info(methodName, "Reading SMSes... ");

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
                sms.setAddress(from);
                sms.setBody(body);
                sms.setRead(readState);
                sms.setDateTime(time);
                sms.setType(type);
                sms.setSubscription(subscriptionId);
                sms.setReplySupported(replySupported);

                if(set.contains(id))
                    sms.setSaved(true);

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
        return smses;
    }
    public interface Callback{
        void onAllSMSFromToResult(List<SMS> updateList);
    }

}
