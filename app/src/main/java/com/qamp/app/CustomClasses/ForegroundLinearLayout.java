package com.qamp.app.CustomClasses;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.core.view.ViewCompat;

public class ForegroundLinearLayout  extends LinearLayout {

    private Drawable foregroundDrawable;

    public ForegroundLinearLayout(Context context) {
        super(context);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForegroundLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (foregroundDrawable != null) {
            foregroundDrawable.setState(getDrawableState());
            invalidate();
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (foregroundDrawable != null) {
            foregroundDrawable.jumpToCurrentState();
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || (who == foregroundDrawable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (foregroundDrawable != null) {
            foregroundDrawable.setBounds(0, 0, w, h);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setForeground(Drawable drawable) {
        if (foregroundDrawable != drawable) {
            if (foregroundDrawable != null) {
                foregroundDrawable.setCallback(null);
                unscheduleDrawable(foregroundDrawable);
            }
            foregroundDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
            }
            requestLayout();
            invalidate();
        }
    }

    @Override
    public void draw(android.graphics.Canvas canvas) {
        super.draw(canvas);
        if (foregroundDrawable != null) {
            foregroundDrawable.draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (foregroundDrawable != null) {
            foregroundDrawable.setBounds(0, 0, right - left, bottom - top);
        }
    }
}