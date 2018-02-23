package dev.sagar.smsblocker.tech.handlers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;

import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.service.helper.SMSLocalContract.SMSLocal;
import dev.sagar.smsblocker.tech.service.helper.SMSLocalDBHelper;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 16/02/18.
 */

public class LatestMsgHandler extends AsyncTask<Context, Void, IndexedHashMap<String, Conversation>> {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Constants
    private final static String _id = SMSLocal.COLUMN_NAME_ID;
    private final static String address = SMSLocal.COLUMN_NAME_ADDRESS;
    private final static String threadId = SMSLocal.COLUMN_NAME_THREAD_ID;
    private final static String body = SMSLocal.COLUMN_NAME_BODY;
    private final static String subscriptionId = SMSLocal.COLUMN_NAME_SUBSCRIPTION_ID;
    private final static String read = SMSLocal.COLUMN_NAME_READ;
    private final static String date = SMSLocal.COLUMN_NAME_DATE;
    private final static String type = SMSLocal.COLUMN_NAME_TYPE;
    private final static String photo = SMSLocal.COLUMN_NAME_PHOTO_URI;
    private final static String photothumb = SMSLocal.COLUMN_NAME_PHOTO_THUMBNAIL;
    private final static String contactName = SMSLocal.COLUMN_NAME_CONTACT_NAME;
    private final static String unreadCount = SMSLocal.COLUMN_NAME_UNREAD_COUNT;

    public static final String TYPE_INBOX = "inbox";
    public static final String TYPE_SENT = "sent";
    public static final int SORT_DESC = 0;
    public static final int SORT_ASC = 1;


    private Callback callback;

    public LatestMsgHandler(Callback callback) {
        this.callback = callback;
    }

    @Override
    protected IndexedHashMap<String, Conversation> doInBackground(Context... contexts) {
        final String methodName =  "doInBackground()";
        log.justEntered(methodName);

        Context context = contexts[0];
        IndexedHashMap<String, Conversation> convMap = new IndexedHashMap<>();

        SMSLocalDBHelper dbHelper = new SMSLocalDBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {"*"};

        String selection = null;
        String[] selectionArgs = null;
        String mSortOrder = date+" DESC";

        Cursor c = db.query(SMSLocal.TABLE_NAME, projection, selection, selectionArgs, null, null, mSortOrder );
        if(c == null){
            log.info(methodName, "Cursor for conversation Query Returned null");
            return convMap;
        }

        log.info(methodName, "Latest Messages Cursor returned rows count: "+c.getCount());
        while(c.moveToNext()){
            String address = c.getString(c.getColumnIndexOrThrow(this.address));
            String id = c.getString(c.getColumnIndexOrThrow(this._id));
            String body = c.getString(c.getColumnIndexOrThrow(this.body));
            int subscriptionId = c.getInt(c.getColumnIndexOrThrow(this.subscriptionId));
            boolean readState = c.getInt(c.getColumnIndex(this.read)) == 1;
            long time = c.getLong(c.getColumnIndexOrThrow(this.date));
            long type = c.getLong(c.getColumnIndexOrThrow(this.type));
            String strPhotoUri = c.getString(c.getColumnIndexOrThrow(this.photo));
            String strPhotoThumbUri = c.getString(c.getColumnIndexOrThrow(this.photothumb));
            String contactName = c.getString(c.getColumnIndexOrThrow(this.contactName));
            int unreadCount = c.getInt(c.getColumnIndexOrThrow(this.unreadCount));

            Uri uriPhoto = null;
            if(strPhotoUri!=null){
                uriPhoto = Uri.parse(strPhotoUri);
            }

            Uri uriPhotoThumb = null;
            if(strPhotoThumbUri!=null){
                uriPhotoThumb = Uri.parse(strPhotoThumbUri);
            }

            Conversation conversation = new Conversation();
            conversation.setAddress(address);
            conversation.setSmsId(id);
            conversation.setBody(body);
            conversation.setSubscriptionId(subscriptionId);
            conversation.setReadState(readState);
            conversation.setDateTime(time);
            conversation.setType(type);
            conversation.setPhotoUri(uriPhoto);
            conversation.setPhotoThumbnailUri(uriPhotoThumb);
            conversation.setContactName(contactName);
            conversation.setUnreadCount(unreadCount);

            convMap.put(address, conversation);

        }

        c.close();
        dbHelper.close();

        log.returning(methodName);
        return convMap;
    }

    @Override
    protected void onPostExecute(IndexedHashMap<String, Conversation> map) {
        final String methodName =  "onPostExecute()";
        log.justEntered(methodName);

        callback.onLatestMsgFetched(map);

        super.onPostExecute(map);
        log.returning(methodName);
    }

    public interface Callback{
        void onLatestMsgFetched(IndexedHashMap<String, Conversation> map);
    }
}
