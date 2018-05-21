package com.mihisa.testtranslateapp;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface YandexTranslateApi {
    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/translate")
    Call<YandexTranslateResponse> translate (@FieldMap Map<String, String> keys);

    @FormUrlEncoded
    @POST("/api/v1.5/tr.json/detect")
    Call<YandexDetectResponse> detectLang(@FieldMap Map<String, String> keys);
}
