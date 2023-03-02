// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TouchInterceptLayout extends RelativeLayout
{
    LayoutInflater mInflater;
    
    public TouchInterceptLayout(final Context context) {
        super(context);
        this.mInflater = LayoutInflater.from(context);
    }
    
    public TouchInterceptLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this.mInflater = LayoutInflater.from(context);
    }
    
    public TouchInterceptLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.mInflater = LayoutInflater.from(context);
    }
    
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        return true;
    }
}
