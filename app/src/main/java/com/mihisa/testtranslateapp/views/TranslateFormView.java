package com.mihisa.testtranslateapp.views;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mihisa.testtranslateapp.R;

public class TranslateFormView extends RelativeLayout {
    private EditText editText;
    private ImageButton imageButton;
    private TextChangingListener textChangingListener;
    private Handler handler;

    Runnable instantLoaderTask = new Runnable() {
        @Override
        public void run() {
            textChangingListener.initInstantTranslation();
        }
    };

    public TranslateFormView(Context context) {
        super(context);
        handler = new Handler();
        initViews();
    }

    public TranslateFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler();
        initViews();
    }

    private void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.translate_form, this);
        editText = findViewById(R.id.et_translate);
        imageButton = findViewById(R.id.btn_translate_clear);
        imageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.getText().clear();
            }
        });
        editText.setHorizontallyScrolling(false);
        editText.setMinLines(4);
        editText.setMaxLines(1000);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    if(s.toString().charAt(s.toString().length()-1) != ' ') {
                        imageButton.setVisibility(VISIBLE);

                        if (textChangingListener != null) {
                            handler.removeCallbacks(instantLoaderTask);
                            handler.postDelayed(instantLoaderTask, 200);
                        }
                    }
                } else {
                    imageButton.setVisibility(INVISIBLE);
                    if (textChangingListener != null) textChangingListener.removeTranslation();
                }
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textChangingListener.initNormalTranslation();
                }
                return false;
            }
        });
    }


    public String getText() {
        return editText.getText().toString();
    }

    public void setTextChangingListener(TextChangingListener textChangingListener) {
        this.textChangingListener = textChangingListener;
    }

    public void setText(String text) {
        editText.setText(text);
    }

    public interface TextChangingListener {

        void initInstantTranslation();

        void initNormalTranslation();

        void removeTranslation();
    }
}
