// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.view.View;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class OverLayFrameLayout extends FrameLayout
{
    public OverLayFrameLayout(final Context context) {
        this(context, null);
    }
    
    public OverLayFrameLayout(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public OverLayFrameLayout(final Context context, final AttributeSet attrs, final int defaultStyle) {
        super(context, attrs, defaultStyle);
    }
    
    protected void onMeasure(int widthMeasureSpec, final int heightMeasureSpec) {
        final int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int size = 0;
        if (heightMode == 1073741824 && heightSize > 0) {
            size = (int)(heightSize * 0.66);
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
