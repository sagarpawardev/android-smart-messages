package dev.sagar.smsblocker.tech.handlers.httprequest.translate;

import android.content.Context;
import android.os.AsyncTask;

import dev.sagar.smsblocker.tech.beans.TranslateResponse;
import dev.sagar.smsblocker.tech.utils.TranslateUtil;

/**
 * Created by sagarpawar on 06/05/18.
 */

public class TranslateService extends AsyncTask<String, Void, TranslateResponse>{
    private Context context;
    private Callback callback;

    public TranslateService(Context context, Callback callback){
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected TranslateResponse doInBackground(String... strings) {
        String requestCode = strings[0];
        String textToTranslate = null;

        String fromLang = TranslateUtil.ENGLISH;
        String toLang = TranslateUtil.HINDI;

        TranslateResponse response = new TranslateResponse();
        response.setRequestCode(requestCode);
        try {
            textToTranslate = strings[1];
            TranslateUtil util = new TranslateUtil(context, fromLang, toLang);
            String translatedText = util.translate(textToTranslate);
            response.setFromLang(fromLang);
            response.setToLang(toLang);
            response.setOrgText(textToTranslate);
            response.setTransText(translatedText);
        }
        catch (Exception e){
            e.printStackTrace();
            response.setErr(e);
        }
        return response;
    }

    @Override
    protected void onPostExecute(TranslateResponse response) {
        if(response.getErr() != null){
            Exception e = response.getErr();
            callback.onTranslationFailure(e);
        }
        else {
            callback.onTranslationSuccess(response);
        }
        super.onPostExecute(response);
    }

    public interface Callback{
        void onTranslationSuccess(TranslateResponse response);
        void onTranslationFailure(Exception e);
    }

}
