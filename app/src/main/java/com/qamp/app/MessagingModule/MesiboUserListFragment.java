/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

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
