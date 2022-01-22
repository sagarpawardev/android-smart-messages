package dev.sagar.smsblocker.tech.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import dev.sagar.smsblocker.tech.Constants;
import dev.sagar.smsblocker.tech.exceptions.TranslateAPIException;
import dev.sagar.smsblocker.tech.handlers.httprequest.translate.TranslateAPI;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by sagarpawar on 06/05/18.
 */

public class TranslateUtil {
    public static final String ENGLISH = "en";
    public static final String HINDI = "hi";

    private String sourceLang, transLang;
    private Context context;

    public TranslateUtil(Context context, String sourceLang, String transLang){
        this.sourceLang = sourceLang;
        this.transLang = transLang;
        this.context = context;
    }

    public String translate(String textToTranslate) throws TranslateAPIException {
        String result = null;
        String translation = null;
        try {
            Properties properties = new Properties();;
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(Constants.HTTP_PROPERTIES_PATH);
            properties.load(inputStream);
            String baseUrl = properties.getProperty(Constants.KEY_TRANSLATE_URL);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    //.addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

            TranslateAPI service = retrofit.create(TranslateAPI.class);
            Call<String> call = service.translate(this.sourceLang, this.transLang, textToTranslate);

            StringBuilder sbTranslation = new StringBuilder();
            Response<String> response = call.execute();
            if(response.isSuccessful()){
                result = response.body();
                JSONArray arr = new JSONArray(result);
                arr = arr.getJSONArray(0);
                for(int i=0; i<arr.length(); i++) {
                    JSONArray tArr = arr.getJSONArray(i);
                    String text = tArr.getString(0);
                    if(text!=null){
                        sbTranslation.append(text);
                        sbTranslation.append(" ");
                    }
                }
                translation = sbTranslation.toString();
            }
            else{
                /*Crashlytics.log(Log.ERROR,
                        "Translate_API_Error",
                        "Google Translate API Error: "+response.errorBody()+" \nURL: "+response.raw().request().url());*/
                throw new TranslateAPIException("Problem in Calling API");
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Crashlytics.logException(e);
            throw new TranslateAPIException("Cannot read property file: "+e.getMessage());
        } catch (JSONException e) {
            /*Crashlytics.log(Log.ERROR,
                    "Translate_API_Error",
                    "Error in Response Structure..\n"+
                    result);*/
            throw new TranslateAPIException("Cannot read property file: "+e.getMessage());
        }

        return translation;
    }
}
