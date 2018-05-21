package com.mihisa.testtranslateapp.adapters;

import com.mihisa.testtranslateapp.History;
import com.mihisa.testtranslateapp.OnFavoriteClickListener;

import java.util.List;

public class FavoriteAdapter extends HistoryAdapter {

    public FavoriteAdapter(List<History> dataset, OnFavoriteClickListener onFavClickListener) {
        super(dataset, onFavClickListener);
    }


}
