package com.qamp.app.messaging;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class MesiboImages {
    public static int[] deliveryStatus = {MesiboConfiguration.STATUS_TIMER, MesiboConfiguration.STATUS_SEND, MesiboConfiguration.STATUS_NOTIFIED, MesiboConfiguration.STATUS_READ, MesiboConfiguration.STATUS_ERROR};
    private static Context mContext = null;
    private static Bitmap mDefaultGroupBmp = null;
    private static Bitmap mDefaultLocationBmp = null;
    private static Bitmap mDefaultUserBmp = null;
    private static Drawable mDefaultUserRoundedDrawable = null;
    private static Drawable mDeletedMessageDrawable = null;
    private static Bitmap mDeletedMessageImage = null;
    private static Bitmap mE2EEImage = null;
    private static Bitmap mHeaderImage = null;
    private static Drawable mMissedVideoCallDrawable = null;
    private static Bitmap mMissedVideoCallImage = null;
    private static Drawable mMissedVoiceCallDrawable = null;
    private static Bitmap mMissedVoiceCallImage = null;
    private static Bitmap[] mStatusBitmaps = {null, null, null, null, null, null};

    public static void init(Context context) {
        if (mContext == null) {
            mContext = context;
            getDummyFunction(true);
        }
    }

    public static Bitmap getDeletedMessageImage() {
        if (mDeletedMessageImage == null) {
            mDeletedMessageImage = tint(BitmapFactory.decodeResource(mContext.getResources(), MesiboConfiguration.DELETED_DRAWABLE), MesiboConfiguration.DELETED_TINT_COLOR);
            mDeletedMessageDrawable = new BitmapDrawable(mContext.getResources(), mDeletedMessageImage);
        }
        return mDeletedMessageImage;
    }

    public static Drawable getDeletedMessageDrawable() {
        if (mDeletedMessageDrawable == null) {
            getDeletedMessageImage();
        }
        return mDeletedMessageDrawable;
    }

    public static Bitmap getStatusImage(int status) {
        int tintColor = MesiboConfiguration.NORMAL_TINT_COLOR;
        if (2 == status) {
            tintColor = MesiboConfiguration.DELETED_TINT_COLOR;
        } else if (3 == status) {
            tintColor = MesiboConfiguration.READ_TINT_COLOR;
        } else if (status > 3) {
            status = 4;
            tintColor = 13369344;
        }
        if (mStatusBitmaps[status] != null) {
            return mStatusBitmaps[status];
        }
        mStatusBitmaps[status] = tint(BitmapFactory.decodeResource(mContext.getResources(), deliveryStatus[status]), tintColor);
        return mStatusBitmaps[status];
    }

    public static Bitmap getE2EEImage() {
        if (mE2EEImage == null) {
            Resources resources = mContext.getResources();
            MesiboUI.getConfig();
            mE2EEImage = tint(BitmapFactory.decodeResource(resources, MesiboUI.Config.e2eeIcon), MesiboUI.getConfig().e2eeIconColor);
        }
        return mE2EEImage;
    }

    public static Bitmap getHeaderImage() {
        if (mHeaderImage == null) {
            mHeaderImage = tint(BitmapFactory.decodeResource(mContext.getResources(), MesiboUI.getConfig().headerIcon), MesiboUI.getConfig().headerIconColor);
        }
        return mHeaderImage;
    }

    public static Bitmap getMissedVideoCallImage() {
        if (mMissedVideoCallImage == null) {
            mMissedVideoCallImage = tint(BitmapFactory.decodeResource(mContext.getResources(), MesiboConfiguration.MISSED_VIDEOCALL_DRAWABLE), 13369344);
            mMissedVideoCallDrawable = new BitmapDrawable(mContext.getResources(), mMissedVideoCallImage);
        }
        return mMissedVideoCallImage;
    }

    public static Bitmap getMissedVoiceCallImage() {
        if (mMissedVoiceCallImage == null) {
            mMissedVoiceCallImage = tint(BitmapFactory.decodeResource(mContext.getResources(), MesiboConfiguration.MISSED_VOICECALL_DRAWABLE), 13369344);
            mMissedVoiceCallDrawable = new BitmapDrawable(mContext.getResources(), mMissedVoiceCallImage);
        }
        return mMissedVoiceCallImage;
    }

    public static Bitmap getMissedCallImage(boolean video) {
        if (video) {
            return getMissedVideoCallImage();
        }
        return getMissedVoiceCallImage();
    }

    public static Drawable getMissedCallDrawable(boolean video) {
        if (video) {
            if (mMissedVideoCallDrawable == null) {
                getMissedVideoCallImage();
            }
            return mMissedVideoCallDrawable;
        }
        if (mMissedVoiceCallDrawable == null) {
            getMissedVoiceCallImage();
        }
        return mMissedVoiceCallDrawable;
    }

    public static Bitmap getDefaultUserBitmap() {
        if (mDefaultUserBmp != null) {
            return mDefaultUserBmp;
        }
        mDefaultUserBmp = BitmapFactory.decodeResource(mContext.getResources(), MesiboConfiguration.DEFAULT_PROFILE_PICTURE);
        return mDefaultUserBmp;
    }

    public static Bitmap getDefaultGroupBitmap() {
        if (mDefaultGroupBmp != null) {
            return mDefaultGroupBmp;
        }
        mDefaultGroupBmp = BitmapFactory.decodeResource(mContext.getResources(), MesiboConfiguration.DEFAULT_GROUP_PICTURE);
        return mDefaultGroupBmp;
    }

    public static Drawable getDefaultRoundedDrawable() {
        if (mDefaultUserRoundedDrawable != null) {
            return mDefaultUserRoundedDrawable;
        }
        mDefaultUserRoundedDrawable = new RoundImageDrawable(getDefaultUserBitmap());
        return mDefaultUserRoundedDrawable;
    }

    public static Bitmap getDefaultLocationBitmap() {
        if (mDefaultLocationBmp == null) {
            mDefaultLocationBmp = BitmapFactory.decodeResource(mContext.getResources(), MesiboConfiguration.DEFAULT_LOCATION_IMAGE);
        }
        return mDefaultLocationBmp;
    }

    public static int getDummyFunction(boolean test) {
        if (test) {
            return 0;
        }
        return mContext.getResources().getIdentifier("file_audio", "drawable", mContext.getPackageName()) + mContext.getResources().getIdentifier("file_doc", "drawable", mContext.getPackageName()) + mContext.getResources().getIdentifier("file_file", "drawable", mContext.getPackageName()) + mContext.getResources().getIdentifier("file_pdf", "drawable", mContext.getPackageName()) + mContext.getResources().getIdentifier("file_txt", "drawable", mContext.getPackageName()) + mContext.getResources().getIdentifier("file_xls", "drawable", mContext.getPackageName()) + mContext.getResources().getIdentifier("file_xml", "drawable", mContext.getPackageName());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0088, code lost:
        r0 = mContext.getResources().getIdentifier("file_audio", "drawable", mContext.getPackageName());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getFileDrawable(String r7) {
        /*
            r5 = 3
            boolean r3 = android.text.TextUtils.isEmpty(r7)
            if (r3 == 0) goto L_0x000a
            int r0 = com.qamp.app.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE
        L_0x0009:
            return r0
        L_0x000a:
            java.lang.String r3 = "."
            int r1 = r7.lastIndexOf(r3)
            if (r1 >= 0) goto L_0x0015
            int r0 = com.qamp.app.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE
            goto L_0x0009
        L_0x0015:
            java.lang.String r3 = "."
            int r3 = r7.lastIndexOf(r3)
            int r3 = r3 + 1
            int r4 = r7.length()
            java.lang.String r2 = r7.substring(r3, r4)
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 == 0) goto L_0x002e
            int r0 = com.qamp.app.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE
            goto L_0x0009
        L_0x002e:
            int r3 = r2.length()
            if (r3 <= r5) goto L_0x0039
            r3 = 0
            java.lang.String r2 = r2.substring(r3, r5)
        L_0x0039:
            android.content.Context r3 = mContext
            android.content.res.Resources r3 = r3.getResources()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "file_"
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r2)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "drawable"
            android.content.Context r6 = mContext
            java.lang.String r6 = r6.getPackageName()
            int r0 = r3.getIdentifier(r4, r5, r6)
            if (r0 != 0) goto L_0x0009
            java.lang.String r3 = "mp3"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 != 0) goto L_0x0088
            java.lang.String r3 = "wav"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 != 0) goto L_0x0088
            java.lang.String r3 = "amr"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 != 0) goto L_0x0088
            java.lang.String r3 = "aif"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 != 0) goto L_0x0088
            java.lang.String r3 = "wma"
            boolean r3 = r2.equalsIgnoreCase(r3)
            if (r3 == 0) goto L_0x009e
        L_0x0088:
            android.content.Context r3 = mContext
            android.content.res.Resources r3 = r3.getResources()
            java.lang.String r4 = "file_audio"
            java.lang.String r5 = "drawable"
            android.content.Context r6 = mContext
            java.lang.String r6 = r6.getPackageName()
            int r0 = r3.getIdentifier(r4, r5, r6)
            if (r0 != 0) goto L_0x0009
        L_0x009e:
            int r0 = com.qamp.app.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE
            goto L_0x0009
        */
        throw new UnsupportedOperationException("Method not decompiled: com.qamp.app.messaging.MesiboImages.getFileDrawable(java.lang.String):int");
    }

    public static Bitmap tint(Bitmap bmp, int color) {
        if (bmp == null) {
            return null;
        }
        ColorFilter colorFilter = new ColorMatrixColorFilter(new float[]{0.0f, 0.0f, 0.0f, 0.0f, (float) ((16711680 & color) / 65535), 0.0f, 0.0f, 0.0f, 0.0f, (float) ((65280 & color) / 255), 0.0f, 0.0f, 0.0f, 0.0f, (float) (color & 255), 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        if (bmp.isMutable()) {
        }
        Bitmap resultBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
        new Canvas(resultBitmap).drawBitmap(resultBitmap, 0.0f, 0.0f, paint);
        return resultBitmap;
    }
}
