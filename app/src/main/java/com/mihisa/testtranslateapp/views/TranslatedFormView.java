package com.mihisa.testtranslateapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mihisa.testtranslateapp.R;

public class TranslatedFormView extends RelativeLayout {

    private TextView textView;
    private ImageButton favButton;

    private FavoriteButtonListener favButtonListener;

    public TranslatedFormView(Context context) {
        super(context);
        initViews();
    }

    public TranslatedFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.translated_form, this);
        textView = findViewById(R.id.tv_translated);
        favButton =  findViewById(R.id.btn_translated_fav);


        favButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favButtonListener != null) {
                    favButton.setSelected(!favButton.isSelected());
                    favButtonListener.favButtonClick();
                }
            }
        });

    }
        public void setText(String text) {
            textView.setText(text);
        }

        public void clearText(){
            textView.setText("");
        }

        public String getText() {
            return textView.getText().toString();
        }

        public void setFavoriteButtonState(boolean state){
            favButton.setSelected(state);
        }

        public void setFavoriteButtonListener(FavoriteButtonListener favButtonListener){
            this.favButtonListener = favButtonListener;
        }


    public interface FavoriteButtonListener{
        void favButtonClick();
    }
}
