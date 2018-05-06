package dev.sagar.smsblocker.tech.handlers.dbrequest;

import android.content.Context;
import android.os.AsyncTask;

import dev.sagar.smsblocker.tech.service.DBServiceSingleton;
import dev.sagar.smsblocker.tech.utils.LogUtil;

/**
 * Created by sagarpawar on 16/02/18.
 */

public class RefreshDBHandler extends AsyncTask<Context, Void, Integer> {

    //Log Initiate
    private LogUtil log = new LogUtil( this.getClass().getName() );

    private Callback callback;
    public RefreshDBHandler(Callback callback){
        this.callback = callback;
    }

    @Override
    protected Integer doInBackground(Context... contexts) {
        final String methodName =  "doInBackground()";
        log.justEntered(methodName);

        Context context = contexts[0];
        int count = DBServiceSingleton.getInstance().refreshConversations(context);

        log.returning(methodName);
        return count;
    }

    @Override
    protected void onPostExecute(Integer count) {
        final String methodName =  "onPostExecute()";
        log.justEntered(methodName);

        callback.onDBServiceRefreshed(count);

        log.returning(methodName);
    }

    public interface Callback{
        void onDBServiceRefreshed(int count);
    }
}
