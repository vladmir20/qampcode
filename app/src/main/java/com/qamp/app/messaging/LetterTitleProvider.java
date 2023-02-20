package com.qamp.app.messaging;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

import com.qamp.app.messaging.AllUtils.LetterTileProvider;


public class LetterTitleProvider extends LetterTileProvider {
    /**
     * The number of available tile colors (see R.array.letter_tile_colors)
     */

    public static final String SAN_SERIF_LIGHT = "sans-serif-light";

    /**
     * The {@link TextPaint} used to draw the letter onto the tile
     */
    private final TextPaint mPaint = new TextPaint();
    /**
     * The bounds that enclose the letter
     */
    private final Rect mBounds = new Rect();
    /**
     * The {@link Canvas} to draw on
     */
    private final Canvas mCanvas = new Canvas();
    /**
     * The first char of the name being displayed
     */
    private final char[] mFirstChar = new char[1];
    /**
     * The font size used to display the letter
     */
    private final int mTileLetterFontSize;
    private final int mTileSize;
    /**
     * The default image to display
     */
    private final Bitmap mBitmap;
    /**
     * The background colors of the tile
     */
    //private final TypedArray mColors;
    private int[] mColors = {
            0xfff16364, 0xfff58559, 0xfff9a43e, 0xffe4c62e,
            0xff67bf74, 0xff59a2be, 0xff2093cd, 0xffad62a7
    };

    /**
     * Constructor for <code>LetterTileProvider</code>
     *
     * @param context  The {@link Context} to use
     * @param tileSize
     * @param colors
     */
    public LetterTitleProvider(Context context, int tileSize, int[] colors) {
        super(context, tileSize, colors);
        final Resources res = context.getResources();
        if (null != colors) {
            mColors = colors;
        }

        mPaint.setTypeface(Typeface.create(SAN_SERIF_LIGHT, Typeface.NORMAL));
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);

        mTileLetterFontSize = 25;
        mTileSize = tileSize;

        mBitmap = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);
    }

    /**
     * Check whether it is english letter or a digit
     *
     * @param c The char to check
     * @return True if <code>c</code> is in the English alphabet or is a digit,
     * false otherwise
     */
    private static boolean isEnglishLetterOrDigit(char c) {
        return 'A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9';
    }

    /**
     * @param displayName The name used to create the letter for the tile
     * @param newBitmap   New bitmap
     * @return A {@link Bitmap} that contains a letter used in the English
     * alphabet or digit, if there is no letter or digit available, a
     * default image is shown instead
     */
    public Bitmap getLetterTile(String displayName, boolean newBitmap) {
        int width = mTileSize;
        int height = mTileSize;
        //final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap bmp = mBitmap;
        if (newBitmap)
            bmp = Bitmap.createBitmap(mTileSize, mTileSize, Bitmap.Config.ARGB_8888);

        char firstChar = '*';

        final Canvas c = mCanvas;

        c.setBitmap(bmp);
        if (!TextUtils.isEmpty(displayName))
            firstChar = displayName.charAt(0);

        int color = pickColor(displayName);
        c.drawColor(color);
        if (isEnglishLetterOrDigit(firstChar)) {
            mFirstChar[0] = Character.toUpperCase(firstChar);
        } else {
            //c.drawBitmap(mDefaultBitmap, 0, 0, null);
            mFirstChar[0] = '*';
        }

        mPaint.setTextSize(mTileLetterFontSize);
        mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);
        c.drawText(mFirstChar, 0, 1, 0 + width / 2, 0 + height / 2
                + (mBounds.bottom - mBounds.top) / 2, mPaint);

        return bmp;
    }

    /**
     * Pick a color
     *
     * @param key The key used to generate the tile color
     * @return A new or previously chosen color for <code>key</code> used as the
     * tile background color
     */
    public int pickColor(String key) {
        if (TextUtils.isEmpty(key))
            return 0;
        // String.hashCode() is not supposed to change across java versions, so
        // this should guarantee the same key always maps to the same color
        int color = Math.abs(key.hashCode()) % mColors.length;
        return mColors[color];
    }
}
