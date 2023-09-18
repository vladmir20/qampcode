package com.qamp.app.CustomClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;

import com.qamp.app.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class CheckboxImageView extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener {

    private boolean isChecked = false;
    private OnCheckedChangeListener onCheckedChangeListener;

    public CheckboxImageView(Context context) {
        super(context);
        init();
    }

    public CheckboxImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckboxImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setOnClickListener(this);
        updateImage();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            updateImage();
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, isChecked);
            }
        }
    }

    public void toggle() {
        setChecked(!isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        toggle();
    }

    private void updateImage() {
        if (isChecked) {
            setImageResource(R.drawable.checkbox_checked);
        } else {
            setImageResource(R.drawable.checked_unchecked);
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckboxImageView checkboxImageView, boolean isChecked);
    }
}
