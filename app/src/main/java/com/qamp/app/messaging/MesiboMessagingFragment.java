/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.messaging;

import android.graphics.Bitmap;

import com.mesibo.api.MesiboProfile;

import java.util.ArrayList;

public class MesiboMessagingFragment extends MessagingFragment {
    public static final String CREATE_PROFILE = "createprofile";
    public static final int ERROR_FILE = 3;
    public static final int ERROR_INVALIDGROUP = 2;
    public static final int ERROR_PERMISSION = 1;
    public static final String HIDE_REPLY = "hidereply";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_ID = "mid";
    public static final String READONLY = "readonly";
    public static final String SHOWMISSEDCALLS = "missedcalls";

    public interface FragmentListener {
        void Mesibo_onContextUserInterfaceCount(int i);

        void Mesibo_onError(int i, String str, String str2);

        void Mesibo_onHideInContextUserInterface();

        void Mesibo_onShowInContextUserInterface();

        void Mesibo_onUpdateUserOnlineStatus(MesiboProfile mesiboProfile, String str);


        void Mesibo_onUpdateUserPicture(MesiboProfile mesiboProfile, Bitmap bitmap, String str);
    }

    public void sendTextMessage(String newText) {
        super.sendTextMessage(newText);
    }
}
