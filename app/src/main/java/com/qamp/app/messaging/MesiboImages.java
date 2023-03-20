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
import android.text.TextUtils;

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


    public static int getFileDrawable(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return com.mesibo.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE;
        } else {
            int dotindex = fileName.lastIndexOf(".");
            if (dotindex < 0) {
                return com.mesibo.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE;
            } else {
                String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
                if (TextUtils.isEmpty(ext)) {
                    return com.mesibo.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE;
                } else {
                    if (ext.length() > 3) {
                        ext = ext.substring(0, 3);
                    }

                    int checkExistence = mContext.getResources().getIdentifier("file_" + ext, "drawable", mContext.getPackageName());
                    if (checkExistence != 0) {
                        return checkExistence;
                    } else {
                        if (ext.equalsIgnoreCase("mp3") || ext.equalsIgnoreCase("wav") || ext.equalsIgnoreCase("amr") || ext.equalsIgnoreCase("aif") || ext.equalsIgnoreCase("wma")) {
                            checkExistence = mContext.getResources().getIdentifier("file_audio", "drawable", mContext.getPackageName());
                            if (checkExistence != 0) {
                                return checkExistence;
                            }
                        }

                        return com.mesibo.messaging.MesiboConfiguration.DEFAULT_FILE_IMAGE;
                    }
                }
            }
        }
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
