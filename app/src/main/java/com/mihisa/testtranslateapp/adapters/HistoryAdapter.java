package com.mihisa.testtranslateapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mihisa.testtranslateapp.History;
import com.mihisa.testtranslateapp.OnFavoriteClickListener;
import com.mihisa.testtranslateapp.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> dataset;
    private OnFavoriteClickListener onFavClickListener;

    public HistoryAdapter(List<History> dataset, OnFavoriteClickListener onFavClickListener) {
        this.dataset = dataset;
        this.onFavClickListener = onFavClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view, onFavClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.from.setText(dataset.get(position).getTextFrom());
        holder.to.setText(dataset.get(position).getTextTo());
        holder.lang.setText(dataset.get(position).getLang());
        holder.fav.setSelected(dataset.get(position).isMarkedFav());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView from;
        public TextView to;
        public TextView lang;
        public ImageButton fav;

        public ViewHolder(View view, OnFavoriteClickListener listener) {
            super(view);
            from = view.findViewById(R.id.tv_history_from);
            to = view.findViewById(R.id.tv_history_to);
            lang = view.findViewById(R.id.tv_history_lang);
            fav = view.findViewById(R.id.ib_history_fav);

            setFavListener(listener);
        }

        private void setFavListener(final OnFavoriteClickListener listener) {
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        fav.setSelected(!fav.isSelected());
                        listener.onFavClick(getAdapterPosition());
                    }
                }
            });
        }

    }
}