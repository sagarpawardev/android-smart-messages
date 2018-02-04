package dev.sagar.smsblocker.tech.threads;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Telephony;

import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 05/02/18.
 */

public class DBHelperThread extends AsyncTask<String, Void, Void> {
    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android
    private Context context = null;

    //Constants
    private final String address = Telephony.Sms.ADDRESS;
    private final String read = Telephony.Sms.READ;


    public DBHelperThread(Context context){
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... strings) {
        final String methodName = "doInBackground()";
        log.justEntered(methodName);

        String fromNumber = strings[0];
        log.error(methodName, "Can be improved Here");

        Uri uriSMSUri = Telephony.Sms.Inbox.CONTENT_URI;
        String selection = address+" = ?";
        String[] selectionArgs = {fromNumber};
        ContentValues values = new ContentValues();
        values.put(read, true);

        int updateCount = context
                .getContentResolver()
                .update(uriSMSUri, values, selection, selectionArgs);
        log.info(methodName, "Update Count: "+updateCount);

        log.returning(methodName);

        return null;
    }
}
