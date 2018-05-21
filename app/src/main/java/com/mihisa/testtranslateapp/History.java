package com.mihisa.testtranslateapp;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class History implements Comparable<History> {
    @SerializedName("lang")
    @Expose
    private String lang;

    @SerializedName("textTo")
    @Expose
    private String to;

    @SerializedName("textFrom")
    @Expose
    private String from;

    @SerializedName("date")
    @Expose
    private long date;

    @SerializedName("fav")
    @Expose
    private boolean isMarkedFav;

    public History(String lang, String to, String from, long date, boolean markedFav) {
        this.lang = lang;
        this.to = to;
        this.from = from;
        this.date = date;
        this.isMarkedFav = markedFav;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getTextFrom() {
        return from;
    }

    public String getTextTo() {
        return to;
    }

    public long getDate() {
        return date;
    }

    @Override
    public int compareTo(@NonNull History o) {
        if (date > o.getDate()) {
            return -1;
        } else if (date < o.getDate()) {
            return 1;
        } else {
            return 0;
        }
    }

    public boolean isMarkedFav() {
        return isMarkedFav;
    }

    public void setMarkedFav(boolean markedFav) {
        isMarkedFav = markedFav;
    }
}
