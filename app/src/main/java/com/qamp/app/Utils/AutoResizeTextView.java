/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

public class AutoResizeTextView extends AppCompatTextView {
    private static final int NO_LINE_LIMIT = -1;
    private final RectF _availableViewRect;
    private final SizeTester _sizeTester;
    private float _maxTextSize;
    private float _spacingMult;
    private float _spacingAdd;
    private float _minTextSize;
    private int _widthLimit;
    private int _maxLines;
    private boolean _initialized;
    private TextPaint _paint;

    public AutoResizeTextView(final Context context) {
        this(context, null, 16842884);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 16842884);
    }

    public AutoResizeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        this._availableViewRect = new RectF();
        this._spacingMult = 1.0f;
        this._spacingAdd = 0.0f;
        this._initialized = false;
        this._minTextSize = TypedValue.applyDimension(2, 12.0f, this.getResources().getDisplayMetrics());
        this._maxTextSize = this.getTextSize();
        this._paint = new TextPaint((Paint) this.getPaint());
        if (this._maxLines == 0) {
            this._maxLines = -1;
        }
        this._sizeTester = new SizeTester() {
            final RectF textRect = new RectF();

            @TargetApi(16)
            @Override
            public int onTestSize(final int suggestedSize, final RectF availableView) {
                AutoResizeTextView.this._paint.setTextSize((float) suggestedSize);
                final TransformationMethod transformationMethod = AutoResizeTextView.this.getTransformationMethod();
                String text;
                if (transformationMethod != null) {
                    text = transformationMethod.getTransformation(AutoResizeTextView.this.getText(), (View) AutoResizeTextView.this).toString();
                } else {
                    text = AutoResizeTextView.this.getText().toString();
                }
                final boolean singleLine = AutoResizeTextView.this.getMaxLines() == 1;
                if (singleLine) {
                    this.textRect.bottom = AutoResizeTextView.this._paint.getFontSpacing();
                    this.textRect.right = AutoResizeTextView.this._paint.measureText(text);
                } else {
                    final StaticLayout layout = new StaticLayout((CharSequence) text, AutoResizeTextView.this._paint, AutoResizeTextView.this._widthLimit, Layout.Alignment.ALIGN_NORMAL, AutoResizeTextView.this._spacingMult, AutoResizeTextView.this._spacingAdd, true);
                    if (AutoResizeTextView.this.getMaxLines() != -1 && layout.getLineCount() > AutoResizeTextView.this.getMaxLines()) {
                        return 1;
                    }
                    this.textRect.bottom = (float) layout.getHeight();
                    int maxWidth = -1;
                    for (int lineCount = layout.getLineCount(), i = 0; i < lineCount; ++i) {
                        final int end = layout.getLineEnd(i);
                        if (i < lineCount - 1 && end > 0 && !AutoResizeTextView.this.isValidWordWrap(text.charAt(end - 1), text.charAt(end))) {
                            return 1;
                        }
                        if (maxWidth < layout.getLineRight(i) - layout.getLineLeft(i)) {
                            maxWidth = (int) layout.getLineRight(i) - (int) layout.getLineLeft(i);
                        }
                    }
                    this.textRect.right = (float) maxWidth;
                }
                this.textRect.offsetTo(0.0f, 0.0f);
                if (availableView.contains(this.textRect)) {
                    return -1;
                }
                return 1;
            }
        };
        this._initialized = true;
    }

    public boolean isValidWordWrap(final char before, final char after) {
        return before == ' ' || before == '-';
    }

    public void setAllCaps(final boolean allCaps) {
        super.setAllCaps(allCaps);
        this.adjustTextSize();
    }

    public void setTypeface(final Typeface tf) {
        super.setTypeface(tf);
        this.adjustTextSize();
    }

    public void setTextSize(final float size) {
        this._maxTextSize = size;
        this.adjustTextSize();
    }

    public int getMaxLines() {
        return this._maxLines;
    }

    public void setMaxLines(final int maxLines) {
        super.setMaxLines(maxLines);
        this._maxLines = maxLines;
        this.adjustTextSize();
    }

    public void setSingleLine() {
        super.setSingleLine();
        this._maxLines = 1;
        this.adjustTextSize();
    }

    public void setSingleLine(final boolean singleLine) {
        super.setSingleLine(singleLine);
        if (singleLine) {
            this._maxLines = 1;
        } else {
            this._maxLines = -1;
        }
        this.adjustTextSize();
    }

    public void setLines(final int lines) {
        super.setLines(lines);
        this._maxLines = lines;
        this.adjustTextSize();
    }

    public void setTextSize(final int unit, final float size) {
        final Context c = this.getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        this._maxTextSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        this.adjustTextSize();
    }

    public void setLineSpacing(final float add, final float mult) {
        super.setLineSpacing(add, mult);
        this._spacingMult = mult;
        this._spacingAdd = add;
    }

    public void setMinTextSize(final float minTextSize) {
        this._minTextSize = minTextSize;
        this.adjustTextSize();
    }

    private void adjustTextSize() {
        if (!this._initialized) {
            return;
        }
        final int startSize = (int) this._minTextSize;
        final int heightLimit = this.getMeasuredHeight() - this.getCompoundPaddingBottom() - this.getCompoundPaddingTop();
        this._widthLimit = this.getMeasuredWidth() - this.getCompoundPaddingLeft() - this.getCompoundPaddingRight();
        if (this._widthLimit <= 0) {
            return;
        }
        this._paint = new TextPaint((Paint) this.getPaint());
        this._availableViewRect.right = (float) this._widthLimit;
        this._availableViewRect.bottom = (float) heightLimit;
        this.superSetTextSize(startSize);
    }

    private void superSetTextSize(final int startSize) {
        final int textSize = this.binarySearch(startSize, (int) this._maxTextSize, this._sizeTester, this._availableViewRect);
        super.setTextSize(0, (float) textSize);
    }

    private int binarySearch(final int start, final int end, final SizeTester sizeTester, final RectF availableView) {
        int lastBest = start;
        int lo = start;
        int hi = end - 1;
        while (lo <= hi) {
            final int mid = lo + hi >>> 1;
            final int midValCmp = sizeTester.onTestSize(mid, availableView);
            if (midValCmp < 0) {
                lastBest = lo;
                lo = mid + 1;
            } else {
                if (midValCmp <= 0) {
                    return mid;
                }
                hi = (lastBest = mid - 1);
            }
        }
        return lastBest;
    }

    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        super.onTextChanged(text, start, before, after);
        this.adjustTextSize();
    }

    protected void onSizeChanged(final int width, final int height, final int oldwidth, final int oldheight) {
        super.onSizeChanged(width, height, oldwidth, oldheight);
        if (width != oldwidth || height != oldheight) {
            this.adjustTextSize();
        }
    }

    private interface SizeTester {
        int onTestSize(final int p0, final RectF p1);
    }
}
