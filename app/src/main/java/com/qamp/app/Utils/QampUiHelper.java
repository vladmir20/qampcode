/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

package com.qamp.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.api.Mesibo;
import com.mesibo.mediapicker.AlbumListData;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.messaging.MesiboConfiguration;
import com.mesibo.messaging.MesiboUI;
import com.qamp.app.Activity.EditProfileActivityNew;
import com.qamp.app.Activity.ShowProfileActivity;
import com.qamp.app.Activity.ZoomPictureActivity;
import com.qamp.app.MessagingModule.MesiboMessagingActivity;
import com.qamp.app.MessagingModule.MesiboUserListActivityNew;
import com.qamp.app.Activity.LoginActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class QampUiHelper {
    public static MesiboConfiguration mesiboConfiguration = new MesiboConfiguration();
    public static boolean mMesiboLaunched = false;
    private static boolean mTestMode = false;
    private static WeakReference<MesiboMessagingActivity> mMessagingActivity = null;
    private static WeakReference<MesiboMessagingActivity> mMessagingActivityNew = null;

    public static void setConfig(com.qamp.app.MessagingModule.MesiboConfiguration mesiboConfiguration) {
        mesiboConfiguration = mesiboConfiguration;
    }

    public static void launchMainActivity(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("flowFromLogin", true);
        editor.commit();

        Intent mainActivity = new Intent(context, MesiboUserListActivityNew.class);
        context.startActivity(mainActivity);
    }

    /**public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, Bundle bundle, String forwardMessage) {
     Intent intent = new Intent(context, MesiboActivity.class);
     intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
     .putExtra(MesiboUI.MESSAGE_ID, forwardid)
     .putExtra(MesiboUI.START_IN_BACKGROUND, startInBackground)
     .putExtra(MesiboUI.KEEP_RUNNING, keepRunning);

     if (!TextUtils.isEmpty(forwardMessage)) {
     intent.putExtra(MesiboUI.MESSAGE_CONTENT, forwardMessage);
     }

     if (flag > 0)
     intent.setFlags(flag);

     if (null != bundle)
     intent.putExtra(MesiboUI.BUNDLE, bundle);
     context.startActivity(intent);
     }

     public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, Bundle bundle) {
     launchContactActivity(context, forwardid, selectionMode, flag, startInBackground, keepRunning, bundle, null);
     }*/

    public static void launchMessagingActivity(Context context, long forwardid, String peer, long groupid) {
        Intent intent = new Intent(context, MesiboMessagingActivity.class);
        intent.putExtra(MesiboUI.PEER, peer);
        intent.putExtra(MesiboUI.GROUP_ID, groupid);
        intent.putExtra(MesiboUI.MESSAGE_ID, forwardid);
        context.startActivity(intent);

        if (null != mMessagingActivityNew) {
            MesiboMessagingActivity oldActivity = mMessagingActivityNew.get();
            if (null != oldActivity)
                oldActivity.finish();
        }
    }

    public static void launchGroupActivity(Context context, Bundle bundle) {
//        Intent intent = new Intent(context, CreateNewGroupActivity.class);
//        if(null != bundle)
//            intent.putExtra(MesiboUI.BUNDLE,bundle);
//        context.startActivity(intent);
    }

    /**
     * public static void launchContactActivity(Context context, int selectionMode, long[] mids) {
     * Intent intent = new Intent(context, MesiboActivity.class);
     * intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
     * .putExtra(MesiboUI.MESSAGE_IDS, mids);
     * context.startActivity(intent);
     * }
     */

    public static boolean enableSecureScreen(AppCompatActivity activity) {
//        if (!Mesibo.isSetSecureScreen()) return false;
//
//        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        return true;
    }

    public static void launchEditProfile(Context context, int flag, long groupid, boolean launchMesibo) {
        Intent subActivity = new Intent(context, EditProfileActivityNew.class);
        if (flag > 0)
            subActivity.setFlags(flag);
        subActivity.putExtra("groupid", groupid);
        subActivity.putExtra("launchMesibo", launchMesibo);

        context.startActivity(subActivity);
    }

    public static void launchEditProfileNew(Context context, int flag, long groupid, boolean launchMesibo) {
        Intent subActivity = new Intent(context, EditProfileActivityNew.class);
        if (flag > 0)
            subActivity.setFlags(flag);
        subActivity.putExtra("groupid", groupid);
        subActivity.putExtra("launchMesibo", launchMesibo);

        context.startActivity(subActivity);
    }

    public static void launchUserProfile(Context context, long groupid, String peer) {
        Intent subActivity = new Intent(context, ShowProfileActivity.class);
        subActivity.
                putExtra("peer", peer).
                putExtra("groupid", groupid);
        context.startActivity(subActivity);
    }

    /**
     * public static void launchGroupProfile(Context context, int flag, long groupid, boolean launchMesibo) {
     * Intent subActivity = new Intent(context, EditGroupProfileActivity.class);
     * if (flag > 0)
     * subActivity.setFlags(flag);
     * subActivity.putExtra("groupid", groupid);
     * subActivity.putExtra("launchMesibo", launchMesibo);
     * <p>
     * context.startActivity(subActivity);
     * }
     */

    public static void launchStartupActivity(Context context, boolean skipTour) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * public static void launchMesiboContacts(Context context, long forwardid, int selectionMode, int flag, Bundle bundle) {
     * MesiboUI.launchContacts(context, forwardid, selectionMode, flag, bundle);
     * }
     */

    public static void launchAlbum(Activity context, List<AlbumListData> albumList) {
        MediaPicker.launchAlbum(context, albumList);
    }

    public static void launchImageViewer(Activity context, ArrayList<String> files, int firstIndex) {
        MediaPicker.launchImageViewer(context, files, firstIndex);
    }

    public static void launchImageEditor(Context context, int type, int drawableid, String title, String filePath, boolean showEditControls, boolean showTitle, boolean showCropOverlay, boolean squareCrop, int maxDimension, MediaPicker.ImageEditorListener listener) {
        MediaPicker.launchEditor((AppCompatActivity) context, type, drawableid, title, filePath, showEditControls, showTitle, showCropOverlay, squareCrop, maxDimension, listener);
    }

    public static void launchImageViewer(Activity context, String filePath) {
        Intent intent = new Intent(context, ZoomPictureActivity.class);
        intent.putExtra("filePath", filePath);
        context.startActivity(intent);
    }

    public static void launchPictureActivity(Context context, String title, String filePath) {
        launchImageViewer((AppCompatActivity) context, filePath);
    }

    public static void setMessagingActivity(MesiboMessagingActivity activity) {
        mMessagingActivity = new WeakReference<MesiboMessagingActivity>(activity);
    }

    public static void launchMesibo(Context context, int flag, boolean startInBackground, boolean keepRunningOnBackPressed) {
        mMesiboLaunched = true;
        com.qamp.app.MessagingModule.MesiboUI.launch(context, flag, startInBackground, keepRunningOnBackPressed);
    }


}
