/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class RoundImageDrawable extends Drawable {
    private final Bitmap mBitmap;
    private final int mBitmapHeight;
    private final int mBitmapWidth;
    private final Paint mPaint = new Paint();
    private final RectF mRectF = new RectF();

    public RoundImageDrawable(Bitmap bitmap) {
        this.mBitmap = bitmap;
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        this.mBitmapWidth = this.mBitmap.getWidth();
        this.mBitmapHeight = this.mBitmap.getHeight();
    }

    public void draw(Canvas canvas) {
        canvas.drawOval(this.mRectF, this.mPaint);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mRectF.set(bounds);
    }

    public void setAlpha(int alpha) {
        if (this.mPaint.getAlpha() != alpha) {
            this.mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    public void setColorFilter(ColorFilter cf) {
        this.mPaint.setColorFilter(cf);
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    public void setAntiAlias(boolean aa) {
        this.mPaint.setAntiAlias(aa);
        invalidateSelf();
    }

    public void setFilterBitmap(boolean filter) {
        this.mPaint.setFilterBitmap(filter);
        invalidateSelf();
    }

    public void setDither(boolean dither) {
        this.mPaint.setDither(dither);
        invalidateSelf();
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }
}
