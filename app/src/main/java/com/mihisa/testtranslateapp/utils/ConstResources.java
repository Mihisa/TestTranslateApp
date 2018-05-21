package com.mihisa.testtranslateapp.utils;

import java.util.HashMap;

public class ConstResources {

    public final static String PREFS_SPINNERS_STATE = "spinners_state";
    public final static String PREFS_CACHE_NAME = "translation_cache";
    public final static HashMap<String, String> LANGUAGES = createMap();
    public final static String KEY = "trnsl.1.1.20170415T124611Z.ad6b17355364d141.64ba3777be0638c0334939d343b1920945530598";

    private static HashMap<String, String> createMap() {
        HashMap<String, String> lang = new HashMap<>();
        lang.put("английский", "en");
        lang.put("русский", "ru");
        return lang;
    }

    public static String getKeyByValue(String value) {
        for (HashMap.Entry<String, String> entry : LANGUAGES.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
