package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import dev.sagar.smsblocker.tech.beans.Conversation;
import dev.sagar.smsblocker.tech.datastructures.IndexedHashMap;
import dev.sagar.smsblocker.tech.handlers.LatestMsgHandler;
import dev.sagar.smsblocker.tech.handlers.RefreshDBHandler;
import dev.sagar.smsblocker.tech.service.DBServiceSingleton;
import dev.sagar.smsblocker.tech.service.helper.SMSLocalContract.SMSLocal;
import dev.sagar.smsblocker.tech.service.helper.SMSLocalDBHelper;

/**
 * Created by sagarpawar on 10/02/18.
 */

public class ConversationUtil implements RefreshDBHandler.Callback, LatestMsgHandler.Callback{


    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    //Java Android References
    private Context context;
    private InboxUtil reader = null;
    private DBServiceSingleton dbService = DBServiceSingleton.getInstance();

    //Java Core
    private Callback callback;


    public ConversationUtil(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }


    /**
     * Helps in getting most recent SMSes from all contacts using separate thread,
     * after execution onLatestMsgsFetched() is invoked
     */
    public void getLatestMsgs(){
        final String methodName =  "getLatestMsgs()";
        log.justEntered(methodName);

        log.info(methodName, "Started DB Asynctask Fetch");
        new LatestMsgHandler(this).execute(context);

        log.returning(methodName);
    }

    /***
     * Refreshes Conversations DB in which latest messages are read, Photos are updated  using
     * separate thread, after execution onDBServiceRefreshed() is invoked
     */
    public void refreshDB(){
        final String methodName = "refreshDB()";
        log.justEntered(methodName);

        log.info(methodName, "Starting DB Refresh AsyncTask ");
        new RefreshDBHandler(this).execute(context);

        log.returning(methodName);
    }

    @Override
    public void onDBServiceRefreshed(int count) {
        final String methodName = "onDBServiceRefreshed()";
        log.justEntered(methodName);

        callback.onDBRefreshed(count);

        log.returning(methodName);
    }

    @Override
    public void onLatestMsgFetched(IndexedHashMap<String, Conversation> map) {
        final String methodName = "onLatestMsgFetched()";
        log.justEntered(methodName);

        callback.onLatestMsgsFetched(map);

        log.returning(methodName);
    }

    public interface Callback{
        void onDBRefreshed(int count);
        void onLatestMsgsFetched(IndexedHashMap<String, Conversation> map);
    }

}
