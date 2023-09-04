
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
