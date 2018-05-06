package dev.sagar.smsblocker.tech.handlers.httprequest.translate;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by sagarpawar on 06/05/18.
 */

public interface TranslateAPI {
    @GET("translate_a/single?client=gtx&sl=client=t&hl=en&dt=t")
    Call<String> translate(
            @Query("sl") String sourceLang,
            @Query("tl") String transLang,
            @Query("q") String query
    );
}
