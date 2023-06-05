/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.api.Mesibo;
import com.mesibo.mediapicker.MediaPicker;

import java.lang.ref.WeakReference;

public class MesiboUIManager {
    private static WeakReference<MesiboMessagingActivity> mMessagingActivityNew = null;
    private static boolean mTestMode = false;

    public static void setTestMode(boolean testMode) {
        mTestMode = testMode;
    }

    public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, Bundle bundle, String forwardMessage, String task) {
        if (task.equals("addMembers")) {
            Intent intent = new Intent(context, MesiboUserListActivity.class);
            intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
                    .putExtra("mid", forwardid).putExtra(MesiboUI.START_IN_BACKGROUND, startInBackground)
                    .putExtra(MesiboUI.KEEP_RUNNING, keepRunning);
            intent.putExtra("isTask", "true")
                    .putExtra("task", "addMembers");
            if (!TextUtils.isEmpty(forwardMessage)) {
                intent.putExtra("message", forwardMessage);
            }
            if (flag > 0) {
                intent.setFlags(flag);
            }
            if (bundle != null) {
                intent.putExtra(MesiboUI.BUNDLE, bundle);
            }

            context.startActivity(intent);
        } else if (task.equals("")) {
            Intent intent = new Intent(context, MesiboUserListActivityNew.class);
            intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
                    .putExtra("mid", forwardid).putExtra(MesiboUI.START_IN_BACKGROUND, startInBackground)
                    .putExtra(MesiboUI.KEEP_RUNNING, keepRunning);
            intent.putExtra("isTask", "false");
            if (!TextUtils.isEmpty(forwardMessage)) {
                intent.putExtra("message", forwardMessage);
            }
            if (flag > 0) {
                intent.setFlags(flag);
            }
            if (bundle != null) {
                intent.putExtra(MesiboUI.BUNDLE, bundle);
            }
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, MesiboUserListActivity.class);
            intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode)
                    .putExtra("mid", forwardid).putExtra(MesiboUI.START_IN_BACKGROUND, startInBackground)
                    .putExtra(MesiboUI.KEEP_RUNNING, keepRunning);
            intent.putExtra("isTask", "true");
            if (!TextUtils.isEmpty(forwardMessage)) {
                intent.putExtra("message", forwardMessage);
            }
            if (flag > 0) {
                intent.setFlags(flag);
            }
            if (bundle != null) {
                intent.putExtra(MesiboUI.BUNDLE, bundle);
            }
            context.startActivity(intent);
        }

    }

    public static void launchContactActivity(Context context, long forwardid, int selectionMode, int flag, boolean startInBackground, boolean keepRunning, Bundle bundle, String task) {
        launchContactActivity(context, forwardid, selectionMode, flag, startInBackground, keepRunning, bundle, (String) null, task);
    }

    public static void launchContactActivity(Context context, int selectionMode, long[] mids) {
        Intent intent = new Intent(context, MesiboUserListActivityNew.class);
        intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, selectionMode).putExtra(MesiboUI.MESSAGE_IDS, mids);
        context.startActivity(intent);
    }

    public static void launchForwardActivity(Context context, String forwardMessage, boolean forwardAndClose) {
        Intent intent = new Intent(context, MesiboUserListActivityNew.class);
        intent.putExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD).putExtra("message", forwardMessage).putExtra(MesiboUI.FORWARD_AND_CLOSE, forwardAndClose);
        context.startActivity(intent);
    }

    public static void showEndToEndEncryptionInfo(Context context, String peer, long groupid) {
        Intent intent = new Intent(context, MesiboEndToEndEncryptionActivity.class);
        intent.putExtra(MesiboUI.PEER, peer);
        intent.putExtra(MesiboUI.GROUP_ID, groupid);
        context.startActivity(intent);
    }

    public static void launchGroupActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CreateNewGroupActivity.class);
        if (bundle != null) {
            intent.putExtra(MesiboUI.BUNDLE, bundle);
        }
        context.startActivity(intent);
    }

    public static void launchPictureActivity(Context context, String title, String filePath) {
        if (context != null) {
            MediaPicker.launchImageViewer((AppCompatActivity) context, filePath);
        }
    }

    public static void launchMessagingActivity(Context context, long forwardid, String peer, long groupid) {
        MesiboMessagingActivity oldActivity;
        Intent intent = new Intent(context, MesiboMessagingActivity.class);
        intent.putExtra(MesiboUI.PEER, peer);
        intent.putExtra(MesiboUI.GROUP_ID, groupid);
        intent.putExtra("mid", forwardid);
        context.startActivity(intent);
        if (mMessagingActivityNew != null && (oldActivity = (MesiboMessagingActivity) mMessagingActivityNew.get()) != null) {
            oldActivity.finish();
        }
    }

    public static void launchPlacePicker(Context context, Intent intent, int REQUEST_CODE) {
        ((AppCompatActivity) context).startActivityForResult(intent, REQUEST_CODE);
    }

    public static void launchImageEditor(Context context, int type, int drawableid, String title, String filePath, boolean showEditControls, boolean showTitle, boolean showCropOverlay, boolean squareCrop, int maxDimension, MediaPicker.ImageEditorListener listener) {
        MediaPicker.launchEditor((AppCompatActivity) context, type, drawableid, title, filePath, showEditControls, showTitle, showCropOverlay, squareCrop, maxDimension, listener);
    }

    public static void setMessagingActivity(MesiboMessagingActivity activity) {
        mMessagingActivityNew = new WeakReference<>(activity);
    }

    protected static boolean enableSecureScreen(AppCompatActivity activity) {
//        if (!Mesibo.isSetSecureScreen()) {
//            return false;
//        }
//        activity.getWindow().setFlags(8192, 8192);
//        return true;
        return true;
    }
}
