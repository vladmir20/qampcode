/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.messaging;

import com.mesibo.api.MesiboMessage;

public class MesiboUserListFragment extends UserListFragment {
    public static final String MESSAGE_LIST_MODE = "message_list_mode";
    public static int MODE_EDITGROUP = 4;
    public static int MODE_MESSAGELIST = 0;
    public static int MODE_SELECTCONTACT = 1;
    public static int MODE_SELECTCONTACT_FORWARD = 2;
    public static int MODE_SELECTGROUP = 3;

    public interface FragmentListener {
        boolean Mesibo_onClickUser(String str, long j, long j2);

        void Mesibo_onUpdateSubTitle(String str);

        void Mesibo_onUpdateTitle(String str);

        boolean Mesibo_onUserListFilter(MesiboMessage mesiboMessage);
    }
}
