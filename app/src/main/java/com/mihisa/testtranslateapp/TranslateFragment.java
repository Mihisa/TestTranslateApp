package com.mihisa.testtranslateapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mihisa.testtranslateapp.utils.ConstResources;
import com.mihisa.testtranslateapp.views.SwitchLanguageView;
import com.mihisa.testtranslateapp.views.TranslateFormView;
import com.mihisa.testtranslateapp.views.TranslatedFormView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TranslateFragment extends Fragment {

    private static final String CURRENT_TRANSLATION = "Translation";
    private static final String TRANSLATION_SPINNER_FROM = "Translation_spinner_from";
    private static final String TRANSLATION_SPINNER_TO = "Translation_spinner_to";

    private boolean autoTranslate;
    private boolean autoDetectLang;

    private TranslateFormView translateForm;
    private TranslatedFormView translatedForm;
    private SwitchLanguageView switchLanguageForm;
    private RelativeLayout relativeLayout;

    YandexTranslateApi api = RetrofitClient.getYandexTranslateApi();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_translate, container, false);
        translateForm = view.findViewById(R.id.view_translate_form);
        translatedForm = view.findViewById(R.id.view_translated_form);
        switchLanguageForm = view.findViewById(R.id.view_switch_form);
        relativeLayout = view.findViewById(R.id.rl_translate_fragment);
        return view;
    }

    public TranslateFragment() {
    }



    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        restoreSwitchLanguageState();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        autoDetectLang = prefs.getBoolean("trans_auto_detect_lang", true);
        autoTranslate = prefs.getBoolean("trans_auto_translate", true);

        translateForm.setTextChangingListener(new TranslateFormView.TextChangingListener() {
            @Override
            public void initInstantTranslation() {
                 detectLanguageAndLoad();
            }

            @Override
            public void initNormalTranslation() {
                loadTranslate();
            }

            @Override
            public void removeTranslation() {
                hideTranslatedForm();
            }
        });

        switchLanguageForm.setSpinnerChangeListener(new SwitchLanguageView.SpinnerChangeListener() {
            @Override
            public void initTranslation() {
                loadTranslate();
            }

            @Override
            public void swapTranslateResults() {
                if (translatedForm.getText().length() > 0)
                    translateForm.setText(translatedForm.getText());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_TRANSLATION, translatedForm.getText());
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSwitchLanguageState();
    }

    private void saveSwitchLanguageState() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ConstResources.PREFS_SPINNERS_STATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(TRANSLATION_SPINNER_FROM, switchLanguageForm.getSpinnerFromPos());
        editor.putInt(TRANSLATION_SPINNER_TO, switchLanguageForm.getSpinnerToPos());
        editor.apply();
    }

    private void restoreSwitchLanguageState() {
        SharedPreferences prefs = getActivity().getSharedPreferences(ConstResources.PREFS_SPINNERS_STATE, Context.MODE_PRIVATE);
        int pos1 = prefs.getInt(TRANSLATION_SPINNER_FROM, 0);
        int pos2 = prefs.getInt(TRANSLATION_SPINNER_TO, 1);
        switchLanguageForm.setSpinnerFromPos(pos1);
        switchLanguageForm.setSpinnerToPos(pos2);
        switchLanguageForm.setPrevSpinnerFromPos(pos1);
        switchLanguageForm.setPrevSpinnerToPos(pos2);
    }

    private void hideTranslatedForm() {
        translatedForm.clearText();
        translatedForm.setVisibility(View.INVISIBLE);
    }

    private void showTranslatedForm() {
        translatedForm.setVisibility(View.VISIBLE);
    }

    private void loadTranslate() {
        if (translateForm.getText().length() > 0) {
            if(isNetworkAvailable()) {
                String translation = ConstResources.LANGUAGES.get(switchLanguageForm.getSpinnerFromText()) + "-"
                        + ConstResources.LANGUAGES.get(switchLanguageForm.getSpinnerToText());

                Map<String, String> keys = new HashMap<>();
                keys.put("key", ConstResources.KEY);
                keys.put("text", translateForm.getText());
                keys.put("lang", translation);

                Call<YandexTranslateResponse> call = api.translate(keys);
                call.enqueue(new Callback<YandexTranslateResponse>() {
                    @Override
                    public void onResponse(Call<YandexTranslateResponse> call, final Response<YandexTranslateResponse> response) {
                        if (response.isSuccessful()) {
                            showTranslatedForm();

                            String name = translateForm.getText().trim() + response.body().getText().get(0).trim() + response.body().getLang();
                            translatedForm.setFavoriteButtonState(isResponseAlreadyLiked(name));

                            translatedForm.setText(response.body().getText().get(0));

                            translatedForm.setFavoriteButtonListener(new TranslatedFormView.FavoriteButtonListener() {
                                @Override
                                public void favButtonClick() {
                                    saveResponseToFav(response.body(), translateForm.getText());
                                }
                            });

                            saveResponse(response.body(), translateForm.getText());
                        } else {
                            Toast.makeText(getContext(), String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<YandexTranslateResponse> call, Throwable t) {

                    }
                });
            }
            else{
                hideKeyboard();
                Snackbar.make(relativeLayout, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void detectLanguageAndLoad() {
        if(isNetworkAvailable()) {
            Map<String, String> keys = new HashMap<>();
            keys.put("key", ConstResources.KEY);
            keys.put("hint", "en,ru");
            keys.put("text", translateForm.getText());

            Call<YandexDetectResponse> call = api.detectLang(keys);

            call.enqueue(new Callback<YandexDetectResponse>() {
                @Override
                public void onResponse(Call<YandexDetectResponse> call, Response<YandexDetectResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode() == 200 && response.body().getLang().length() > 0) {
                            switchLanguageForm.setPrevSpinnerFromPos(switchLanguageForm.getSpinnerFromPos());
                            switchLanguageForm.setSpinnerTextFrom(response.body().getLang());
                            loadTranslate();
                        }
                    }
                }

                @Override
                public void onFailure(Call<YandexDetectResponse> call, Throwable t) {
                    hideKeyboard();
                    Snackbar.make(relativeLayout, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
                }
            });
        }
        else{
            hideKeyboard();
            Snackbar.make(relativeLayout, "Отсутствует подключение к интернету", Snackbar.LENGTH_LONG).show();
        }
    }

    private void saveResponse(YandexTranslateResponse object, String fromText) {
        String name = fromText.trim() + object.getText().get(0).trim() + object.getLang();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        History historyItem = new History(object.getLang(), object.getText().get(0).trim(), fromText.trim(), new Date().getTime(), isResponseAlreadyLiked(name));
        String json = gson.toJson(historyItem);

        SharedPreferences prefs = getActivity().getSharedPreferences(ConstResources.PREFS_CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, json);
        editor.apply();
    }


    private void saveResponseToFav(YandexTranslateResponse object, String fromText) {
        String name = fromText.trim() + object.getText().get(0).trim() + object.getLang();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        History historyItem = new History(object.getLang(), object.getText().get(0).trim(), fromText.trim(), new Date().getTime(), !isResponseAlreadyLiked(name));
        String json = gson.toJson(historyItem);

        SharedPreferences prefs = getActivity().getSharedPreferences(ConstResources.PREFS_CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(name, json);
        editor.apply();
    }
    private boolean isResponseAlreadyLiked(String name) {
        Map<String, String> allEntries = (Map<String, String>) getContext().getSharedPreferences(ConstResources.PREFS_CACHE_NAME, Context.MODE_PRIVATE).getAll();
        if (allEntries.containsKey(name)) {
            Gson gson = new Gson();
            String json = allEntries.get(name);
            History historyItem = gson.fromJson(json, History.class);
            return historyItem.isMarkedFav();
        }
        return false;
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(relativeLayout.getWindowToken(), 0);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

