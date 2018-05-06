package dev.sagar.smsblocker.tech.handlers.httprequest.translate;

import android.content.Context;
import android.os.AsyncTask;

import dev.sagar.smsblocker.tech.utils.TranslateUtil;

/**
 * Created by sagarpawar on 06/05/18.
 */

public class TranslateService extends AsyncTask<String, Void, Object[]>{
    private Context context;
    private Callback callback;

    public TranslateService(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected Object[] doInBackground(String... strings) {
        String textId = strings[0];
        String textToTranslate = null;
        try {
            textToTranslate = strings[1];
            TranslateUtil util = new TranslateUtil(context, TranslateUtil.ENGLISH, TranslateUtil.HINDI);
            String translatedText = util.translate(textToTranslate);
            String[] result = {textId, translatedText};
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
            Object[] result = {e};
            return result;
        }
    }

    @Override
    protected void onPostExecute(Object[] obj) {
        if(obj[0] instanceof Exception){
            callback.onTranslationFailure((Exception) obj[0]);
        }
        else {
            String[] s = (String[]) obj;
            callback.onTranslationSuccess(s[0], s[1]);
        }
        super.onPostExecute(obj);
    }

    public interface Callback{
        void onTranslationSuccess(String textId, String translatedText);
        void onTranslationFailure(Exception e);
    }

}
