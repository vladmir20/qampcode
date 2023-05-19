/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;


import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment{}

/**
import static android.os.Looper.getMainLooper;
import static com.mesibo.api.Mesibo.ACTIVITY_TYPING;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLINCOMING;
import static com.mesibo.api.Mesibo.MSGSTATUS_CALLOUTGOING;
import static com.mesibo.api.Mesibo.getProfile;
import static com.mesibo.MessagingModule.MesiboActivity.TAG;
import static com.qamp.app.MessagingModule.MesiboConfiguration.ALL_USERS_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.ATTACHMENT_ICON;
import static com.qamp.app.MessagingModule.MesiboConfiguration.ATTACHMENT_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.AUDIO_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.DELETED_DRAWABLE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.FREQUENT_USERS_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.GROUP_MEMBERS_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.IMAGE_ICON;
import static com.qamp.app.MessagingModule.MesiboConfiguration.IMAGE_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.LOCATION_ICON;
import static com.qamp.app.MessagingModule.MesiboConfiguration.LOCATION_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MESIBO_SEARCH_READ_USERLIST;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MESSAGE_DELETED_STRING;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MESSAGE_STRING_USERLIST_SEARCH;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MISSED_VIDEOCALL_DRAWABLE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MISSED_VIDEO_CALL;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MISSED_VOICECALL_DRAWABLE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MISSED_VOICE_CALL;
import static com.qamp.app.MessagingModule.MesiboConfiguration.USERS_STRING_USERLIST_SEARCH;
import static com.qamp.app.MessagingModule.MesiboConfiguration.VIDEO_ICON;
import static com.qamp.app.MessagingModule.MesiboConfiguration.VIDEO_STRING;
import static com.qamp.app.MessagingModule.MesiboImages.getMissedCallDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.MessagingModule.MesiboUIListener;
import com.qamp.app.Model.UserData;
import com.qamp.app.UiComponents.Fragments.ChatContacts.ContactListFragment;
import com.qamp.app.UiComponents.Fragments.ChatContacts.ContactsBottomSheetFragment;
import com.qamp.app.Util.AppUtils;
import com.qamp.app.Util.Helpers.LetterTitleProvider;
import com.qamp.app.MessagingModule.LetterTitleProvider;
import com.qamp.app.MessagingModule.MesiboConfiguration;
import com.qamp.app.Util.Helpers.MesiboImages;
import com.qamp.app.Util.Helpers.MesiboUI;
import com.qamp.app.Util.Helpers.RoundImageDrawable;
import com.qamp.app.MessagingModule.MesiboImages;
import com.qamp.app.MessagingModule.MesiboUI;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

//import me.leolin.shortcutbadger.ShortcutBadger;

public class ChatFragment extends Fragment implements Mesibo.MessageListener, Mesibo.ConnectionListener,
        Mesibo.ProfileListener, Mesibo.SyncListener, Mesibo.GroupListener {


    //contact variables
    LinearLayout fab_add;
    int mMode = 0;
    long mForwardIdForContactList = 0;
    long[] mForwardIds = {0};
    String forwardMessage = MesiboUI.MESSAGE_CONTENT;
    Bundle mEditGroupBundle = null;
    //contact variables
    RecyclerView mRecyclerView = null;
    MessageContactAdapter mAdapter = null;
    public static ArrayList<MesiboProfile> mMemberProfiles = new ArrayList<>();
    public static MesiboGroupProfile.Member[] mExistingMembers = null;

    ImageView search_image;
    LinearLayout name_tite_layout, search_view;
    androidx.appcompat.widget.SearchView search_func;


    public ChatFragment() {
        // Required empty public constructor
    }


    SearchView searchChats;
    public TextView mEmptyView;
    public boolean mContactView = false;
    public long mForwardId = 0;
    private Boolean mIsMessageSearching = false;
    private LinearLayout mforwardLayout;
    private ArrayList<MesiboProfile> mUserProfiles = null;
    private ArrayList<MesiboProfile> mSearchResultList = null;
    private ArrayList<MesiboProfile> mAdhocUserList = null;
    private String mSearchQuery = null;
    private String mReadQuery = null;
    public int mSelectionMode = 0;
    private Mesibo.UIHelperListner mMesiboUIHelperListener = null;
    private long[] mForwardMessageIds = null;
    private String mForwardedMessage = null;
    private boolean mCloseAfterForward = false;
    private WeakReference<ContactListFragment.FragmentListener> mListener = null;

    Bundle mGroupEditBundle = null;
    MesiboProfile mGroupProfile = null;
    Set<String> mGroupMembers = null;
    ArrayList<MesiboProfile> memberProfiles = new ArrayList<MesiboProfile>();
    long mGroupId = 0;
    MesiboUI.Config mMesiboUIOptions = null;
    LetterTitleProvider mLetterTileProvider = null;

    private boolean mSyncDone = false;
    private long mUiUpdateTimestamp = 0;
    private TimerTask mUiUpdateTimerTask = null;
    private Timer mUiUpdateTimer = null;
    private final Handler mUiUpdateHandler = new Handler(getMainLooper());
    private final Runnable mUiUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mAdapter) {
                mAdapter.notifyChangeInData();
            }
        }
    };

    private int mTotalUnread = 0;
    private MesiboReadSession mDbSession = null;

    public void updateTitle(String title) {
        ContactListFragment.FragmentListener l = getListener();
        if (null == l)
            return;

        l.Mesibo_onUpdateTitle(title);
    }

    public void updateSubTitle(String title) {
        ContactListFragment.FragmentListener l = getListener();
        if (null == l)
            return;

        l.Mesibo_onUpdateSubTitle(title);
    }

    public boolean onClickUser(String address, long groupid, long forwardid) {
        ContactListFragment.FragmentListener l = getListener();
        if (null == l)
            return false;

        return l.Mesibo_onClickUser(address, groupid, forwardid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MesiboImages.init(getActivity());
        mMesiboUIHelperListener = MesiboUIListener.getUIHelperListner();
        mMesiboUIOptions = MesiboUI.getConfig();

        //   showConatcts();

        //final ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();

        mSelectionMode = ContactListFragment.MODE_MESSAGELIST;
        mReadQuery = null;

        Bundle b = this.getArguments();
        if (null != b) {
            mSelectionMode = b.getInt(ContactListFragment.MESSAGE_LIST_MODE, ContactListFragment.MODE_MESSAGELIST);
            mReadQuery = b.getString("query", null);
        }

        if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD)
            mForwardId = getArguments().getLong(MesiboUI.MESSAGE_ID);
        if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD) {
            mForwardMessageIds = getArguments().getLongArray(MesiboUI.MESSAGE_IDS);
            mForwardedMessage = getArguments().getString(MesiboUI.MESSAGE_CONTENT);
            mCloseAfterForward = getArguments().getBoolean(MesiboUI.FORWARD_AND_CLOSE, false);
        }

        if (mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
            String title = mMesiboUIOptions.messageListTitle;
            if (TextUtils.isEmpty(title)) title = Mesibo.getAppName();
            updateTitle(title);
        } else if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT)
            updateTitle(mMesiboUIOptions.selectContactTitle);
        else if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD)
            updateTitle(mMesiboUIOptions.forwardTitle);
        else if (mSelectionMode == ContactListFragment.MODE_SELECTGROUP)
            updateTitle(mMesiboUIOptions.selectGroupContactsTitle);
        else if (mSelectionMode == ContactListFragment.MODE_EDITGROUP) {
            updateTitle(mMesiboUIOptions.selectGroupContactsTitle);
            mGroupEditBundle = getArguments().getBundle(MesiboUI.BUNDLE);
            if (null != mGroupEditBundle) {
                mGroupId = mGroupEditBundle.getLong(MesiboUI.GROUP_ID);
                mGroupProfile = Mesibo.getProfile(mGroupId);

            }
        }

        if (mMesiboUIOptions.useLetterTitleImage)
            mLetterTileProvider = new LetterTitleProvider(getActivity(), 60, mMesiboUIOptions.mLetterTitleColors);

        mSearchResultList = new ArrayList<>();
        mUserProfiles = new ArrayList<>();

        int layout = R.layout.fragment_chat;

        if (MesiboUI.getConfig().mUserListFragmentLayout != 0)
            layout = MesiboUI.getConfig().mUserListFragmentLayout;


        // Inflate the layout for this fragment
        View view = inflater.inflate(layout, container, false);

        //getFillData();
        mforwardLayout = view.findViewById(R.id.bottom_forward_btn);
        searchChats = view.findViewById(R.id.searchChats);
        fab_add = view.findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConatcts();
            }
        });
        mforwardLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectionMode == ContactListFragment.MODE_SELECTGROUP) {
                    mAdapter.createNewGroup();
                } else if (mSelectionMode == ContactListFragment.MODE_EDITGROUP) {
                    mAdapter.modifyGroupDetail();
                } else if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD) {
                    mAdapter.forwardMessageToContacts();
                }
            }
        });

        mEmptyView = view.findViewById(R.id.emptyview_text);
        setEmptyViewText();
        mUserProfiles = new ArrayList<MesiboProfile>();
        //Toast.makeText(getContext(), "1   " + mUserProfiles.size(), Toast.LENGTH_SHORT).show();

        mRecyclerView = view.findViewById(R.id.message_contact_frag_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext()));
        mAdapter = new MessageContactAdapter(getActivity(), this, mUserProfiles, mSearchResultList);
        mRecyclerView.setAdapter(mAdapter);

        searchChats.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });

        //Toast.makeText(getContext(), "2   " + mUserProfiles.size(), Toast.LENGTH_SHORT).show();

        search_image = getActivity().findViewById(R.id.search_image);
        name_tite_layout = getActivity().findViewById(R.id.name_tite_layout);
        search_view = getActivity().findViewById(R.id.search_view);
        search_func = getActivity().findViewById(R.id.search_func);

        search_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name_tite_layout.setVisibility(View.GONE);
                search_view.setVisibility(View.VISIBLE);
                search_func.setIconified(false);
                search_func.requestFocus();
            }
        });

        search_func.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                mAdapter.notifyDataSetChanged();
                return false;
            }
        });
        search_func.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                name_tite_layout.setVisibility(View.VISIBLE);
                search_view.setVisibility(View.GONE);
                return false;
            }
        });

        return view;
    }

    public void setListener(ContactListFragment.FragmentListener listener) {
        mListener = new WeakReference<ContactListFragment.FragmentListener>(listener);
    }

    public ContactListFragment.FragmentListener getListener() {
        if (null == mListener)
            return null;

        return mListener.get();
    }

    public void setEmptyViewText() {
        if (mSelectionMode == ContactListFragment.MODE_MESSAGELIST)
            mEmptyView.setText(getString(R.string.not_have_chat));
        else
            mEmptyView.setText(getResources().getString(R.string.no_contacts_found));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        if (mMesiboUIHelperListener == null)
            return;

        //int menuResourceId = mMesiboUIHelperListener.Mesibo_onGetMenuResourceId(0);
        //inflater.inflate(menuResourceId, menu);
        mMesiboUIHelperListener.Mesibo_onGetMenuResourceId(getActivity(), 0, null, menu);

        MenuItem searchViewItem = menu.findItem(R.id.mesibo_search);

        if (null != searchViewItem && mMesiboUIOptions.enableSearch) {
//            SearchView searchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
//            MenuItemCompat.setShowAsAction(searchViewItem, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//            MenuItemCompat.setActionView(searchViewItem, searchView);
//            searchView.setIconifiedByDefault(true);
//            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//                @Override
//                public boolean onClose() {
//                    Log.d("search View closed", "SEARCHVIEW");
//                    mSearchQuery = null;
//                    return false;
//                }
//            });

//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                @Override
//                public boolean onQueryTextSubmit(String query) {
//                    return true;
//                }

//                @Override
//                public boolean onQueryTextChange(String newText) {
//                    mAdapter.filter(newText);
//                    mAdapter.notifyDataSetChanged();
//
//                    return true;
//                }
//            });

//            searchView.setOnClickListener(new View.OnClickListener() {
//                                              @Override
//                                              public void onClick(View v) {
//
//                                              }
//                                          }
//            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mesibo_contacts) {
            //QampUiHelper.launchContactActivity(getActivity(), 0, MODE_SELECTCONTACT, 0, false, false, null);
        } else if (item.getItemId() == R.id.mesibo_search) {
            return false;
        } else {
            if (mMesiboUIHelperListener == null)
                return false;

            mMesiboUIHelperListener.Mesibo_onMenuItemSelected(getActivity(), 0, null, item.getItemId());
        }
        return false;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mesibo_contacts);
        if (null != item && mSelectionMode != ContactListFragment.MODE_MESSAGELIST) {
            item.setVisible(false);
        }
    }

    public void showForwardLayout() {
        mforwardLayout.setVisibility(View.VISIBLE);
        return;
    }

    public void handleEmptyUserList(int userListsize) {
        if (userListsize == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

    }

    public void hideForwardLayout() {
        mforwardLayout.setVisibility(View.GONE);
        return;
    }

    private void updateNotificationBadge() {
        if (!mMesiboUIOptions.mEnableNotificationBadge)
            return;
        try {
            if (mTotalUnread > 0)
                ShortcutBadger.applyCount(getActivity(), mTotalUnread);
            else
                ShortcutBadger.removeCount(getActivity());

        } catch (Exception e) {
            mMesiboUIOptions.mEnableNotificationBadge = false;
        }
    }

    public synchronized void addNewMessage(Mesibo.MessageParams params, String message) {

        //should only happen for db messages and deleted on unsynced group
        if (params.groupid > 0 && null == params.groupProfile)
            return;

        ContactListFragment.FragmentListener l = getListener();
        if (null != l && l.Mesibo_onUserListFilter(params))
            return;

        UserData data = UserData.getUserData(params);

        // This is to inflate other title message like 2users or 6 messages match
        if (mIsMessageSearching && params.origin == Mesibo.ORIGIN_DBMESSAGE) {
            mAdhocUserList = mAdapter.getActiveUserlist();
            MesiboProfile mup = null;

            if (mAdhocUserList.size() > mAdapter.mCountProfileMatched + 1) {
                int i = mAdapter.mCountProfileMatched == 0 ? mAdapter.mCountProfileMatched : mAdapter.mCountProfileMatched + 1;
                mup = mAdhocUserList.get(i);
                mup.setName(mAdhocUserList.size() - (i) + " " + MESSAGE_STRING_USERLIST_SEARCH);
            } else {
                mup = new MesiboProfile();
                mup.setName("1" + " " + MESSAGE_STRING_USERLIST_SEARCH);
                mAdhocUserList.add(mAdhocUserList.size(), mup);
            }
        }

        MesiboProfile user = params.profile;
        if (params.groupProfile != null)
            user = params.groupProfile;

        //This MUST not happen
        if (null == user) {
            Log.d(TAG, "Should not happen");
            //Mesibo.de
            //Mesibo.newUserProfile(params.peer, params.groupid, name);
        }


        // depending on whether we want to show user in search or group in search
        //TBD, this need to be fixed, implementation
        if (mIsMessageSearching) {
            if (params.groupProfile != null)
                user = params.groupProfile.cloneProfile();
            else
                user = params.profile.cloneProfile();
        }

        if (null == user.other) {
            user.other = new UserData(user);
        }

        data = (UserData) user.other;

        /*if(null == data.getImagePath()){
            data.setImagePath(MesiboImages.getDafaultUserPath());
        }*/
/**

        data.setMessage(params.mid, getDate(params.ts), params.getStatus(), params.isDeleted(), message);

        mTotalUnread -= data.getUnreadCount();
        if (mTotalUnread < 0)
            mTotalUnread = 0;

        if (Mesibo.isReading(params)) {
            data.setUnreadCount(0);
        } else {
            if (Mesibo.ORIGIN_DBSUMMARY == params.origin || Mesibo.ORIGIN_DBMESSAGE == params.origin)
                data.setUnreadCount(user.unread);
            else
                data.setUnreadCount(data.getUnreadCount() + 1);
        }

        mTotalUnread += data.getUnreadCount();

        if (Mesibo.ORIGIN_REALTIME == params.origin) {
            updateNotificationBadge();
        }

        // remove message from existing position so that it can go to top
        //Note that we must do this always as DB messages may be received while someone else is reading it
        if (true || (Mesibo.ORIGIN_DBSUMMARY != params.origin && Mesibo.ORIGIN_DBMESSAGE != params.origin)) {
            for (int i = 0; i < mAdhocUserList.size(); i++) {
                // we are comparing peer and not object as it might have been changed by setUserProfile
                UserData mcd = ((UserData) mAdhocUserList.get(i).other);

                if (null != mcd && params.compare(mcd.getPeer(), mcd.getGroupId())) {
                    //TBD, if we have not reordered the list (not removed element), we can just update the row instead of table
                    mAdhocUserList.remove(i);
                    break;
                }
            }
        }

        if (Mesibo.ORIGIN_DBSUMMARY != params.origin && Mesibo.ORIGIN_DBMESSAGE != params.origin)
            mAdhocUserList.add(0, user);
        else
            mAdhocUserList.add(user);

        if (null != mUiUpdateTimer) {
            mUiUpdateTimer.cancel();
            mUiUpdateTimer = null;
        }

        if (Mesibo.ORIGIN_REALTIME == params.origin) {

            long ts = Mesibo.getTimestamp();

            // if UI is not updated recently, update it
            if ((ts - mUiUpdateTimestamp) > 2000) {
                //TBD, if we have not reordered the list (message is for the top element), we can just
                // update row instead of entire table
                mAdapter.notifyChangeInData();
                return;
            }

            long timeout = 2000;
            // if old message (though realtime), then we can update little later (2000ms) else quickly (500ms)
            if ((ts - params.ts) < 5000) {
                timeout = 500;
            }

            mUiUpdateTimestamp = ts; // so that it doesn't update even though messages are keep coming

            mUiUpdateTimer = new Timer();
            mUiUpdateTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mUiUpdateHandler.post(mUiUpdateRunnable);
                }
            };

            mUiUpdateTimer.schedule(mUiUpdateTimerTask, timeout);
        }

        //TBD, we can use library like

    }

    @Override
    public void Mesibo_onActivity(Mesibo.MessageParams params, int activity) {
        if (Mesibo.ACTIVITY_TYPING != activity && Mesibo.ACTIVITY_TYPINGCLEARED != activity && Mesibo.ACTIVITY_LEFT != activity)
            return;

        //TBD, we got one crash with params null (08 Feb 2018), need to check
        if (null == params || null == params.profile)
            return;

        //should only happen for db messages and deleted on unsynced group
        if (params.groupid > 0 && null == params.groupProfile)
            return;

        MesiboProfile profile = params.profile;

        UserData data;
        //TBD, why not using params.groupProfile
        if (params.groupid > 0) {
            profile = Mesibo.getProfile(params.groupid);
            if (null == profile)
                return; //MUST not happen
        }

        data = UserData.getUserData(profile);

        int position = data.getUserListPosition();
        if (position < 0)
            return;

        if (mAdhocUserList.size() <= position)
            return;

        if (ACTIVITY_TYPING == activity && params.groupid > 0)
            data.setTypingUser(params.profile);

        //in case mAdhocUserList is changed
        try {
            if (profile != mAdhocUserList.get(position))
                return;
        } catch (Exception e) {
            return;
        }

        mAdapter.notifyItemChanged(position);


    }

    @Override
    public void Mesibo_onLocation(Mesibo.MessageParams params, Mesibo.Location location) {

        addNewMessage(params, LOCATION_STRING);
        updateUiIfLastMessage(params);
    }

    @Override
    public void Mesibo_onFile(Mesibo.MessageParams params, Mesibo.FileInfo fileInfo) {

        String type = ATTACHMENT_STRING;
        if (fileInfo.type == Mesibo.FileInfo.TYPE_IMAGE)
            type = IMAGE_STRING;
        else if (fileInfo.type == Mesibo.FileInfo.TYPE_VIDEO)
            type = VIDEO_STRING;
        else if (fileInfo.type == Mesibo.FileInfo.TYPE_AUDIO)
            type = AUDIO_STRING;

        addNewMessage(params, type);

        updateUiIfLastMessage(params);
        return;
    }

    private String appendNameToMessage(Mesibo.MessageParams params, String msg) {
        String name = params.peer;
        if (null != params.profile && null != params.profile.getName())
            name = params.profile.getName();

        if (TextUtils.isEmpty(name)) return msg;
        String[] splited = name.split("\\s+");
        if (splited.length < 1)
            return msg;

        name = splited[0];

        if (name.length() > 12)
            name = name.substring(0, 12);

        return name + ": " + msg;
    }

    private void updateUiIfLastMessage(Mesibo.MessageParams params) {
        if (!params.isLastMessage()) return;

        // TBD, this logic is complicated, need to fix.
        // in any case we are doing non-search read because we need mUsers
        if (!mIsMessageSearching && !TextUtils.isEmpty(mSearchQuery)) {
            mAdapter.filter(mSearchQuery);
        }

        mAdapter.notifyChangeInData();
        updateNotificationBadge();

    }

    @Override
    public boolean Mesibo_onMessage(Mesibo.MessageParams params, byte[] data) {
        // This we will only get for real-time origin, we filter this in DB
        if (MSGSTATUS_CALLINCOMING == params.getStatus() || MSGSTATUS_CALLOUTGOING == params.getStatus()) {
            updateUiIfLastMessage(params); //?? required
            return true;
        }

        if (params.groupid > 0 && null == params.groupProfile) {
            updateUiIfLastMessage(params);
            return true;
        }

        String str = "";
        try {
            str = new String(data, StandardCharsets.UTF_8);
        } catch (Exception e) {
            str = "";
        }

        if (params.isDeleted()) {
            str = MESSAGE_DELETED_STRING;
        }

        if (params.groupid > 0 && params.isIncoming()) {
            str = appendNameToMessage(params, str);
        }

        if (Mesibo.MSGSTATUS_CALLMISSED == params.getStatus()) {
            str = MISSED_VIDEO_CALL;
            if ((params.getType() & 1) == 0)
                str = MISSED_VOICE_CALL;
        }

        addNewMessage(params, str);
        updateUiIfLastMessage(params);
        return true;
    }

    @Override
    public void Mesibo_onMessageStatus(Mesibo.MessageParams params) {
        if (params.isRealtimeMessage() && params.isMessageStatusInProgress()) return;

        for (int i = 0; i < mUserProfiles.size(); i++) {
            UserData mcd = ((UserData) mUserProfiles.get(i).other);

            if (mcd.getmid() != 0 && mcd.getmid() == params.mid) {
                mcd.setStatus(params.getStatus());
                if (params.isDeleted()) {
                    mcd.setMessage(MESSAGE_DELETED_STRING);
                    mcd.setDeletedMessage(true);
                }
                mAdapter.notifyItemChanged(i);
            }
        }

    }

    @Override
    public void Mesibo_onConnectionStatus(int status) {
        status = Mesibo.getConnectionStatus();

        if (status == Mesibo.STATUS_ONLINE) {
            // updated App Name
            if (mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
                String title = mMesiboUIOptions.messageListTitle;
                if (TextUtils.isEmpty(title)) title = Mesibo.getAppName();
                updateTitle(title);
            }
            updateSubTitle(mMesiboUIOptions.onlineIndicationTitle);
        } else if (status == Mesibo.STATUS_CONNECTING)
            updateSubTitle(mMesiboUIOptions.connectingIndicationTitle);
        else if (status == Mesibo.STATUS_SUSPENDED) {
            updateSubTitle(mMesiboUIOptions.suspendIndicationTitle);
            AppUtils.showServicesSuspendedAlert(getActivity());
        } else if (status == Mesibo.STATUS_NONETWORK)
            updateSubTitle(mMesiboUIOptions.noNetworkIndicationTitle);
        else if (status == Mesibo.STATUS_SHUTDOWN) {
            getActivity().finish();
        } else
            updateSubTitle(mMesiboUIOptions.offlineIndicationTitle);
    }

    private String getDate(long time) {
        int days = Mesibo.daysElapsed(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = null;
        if (days == 0) {
            date = DateFormat.format("HH:mm", cal).toString();
        } else if (days == 1) {
            date = "Yesterday";
        } else if (days < 7) {
            date = DateFormat.format("E, dd MMM", cal).toString();
        } else {
            date = DateFormat.format("dd-MM-yyyy", cal).toString();
        }

        return date;
    }

    @Override
    public void onResume() {
        super.onResume();

        showUserList(MESIBO_INTITIAL_READ_USERLIST);
        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());

        AppUtils.showServicesSuspendedAlert(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

        Mesibo.removeListener(this);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == ContactListFragment.MODE_SELECTGROUP || mSelectionMode == ContactListFragment.MODE_EDITGROUP) {
            for (MesiboProfile d : mUserProfiles) {
                d.uiFlags &= ~MesiboProfile.FLAG_MARKED;
            }
        }
    }

    public void onLongClick() {

    }

    @Override
    public void Mesibo_onSync(int count) {
        final int c = count;
        if (count > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showUserList(MESIBO_INTITIAL_READ_USERLIST);
                }
            });

        }
    }

    public void showUserList(int readCount) {
        Log.d("showUserList", "showUserList");

        setEmptyViewText();

        if (mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {

            mTotalUnread = 0;
            Mesibo.addListener(this);
            mAdhocUserList = mUserProfiles;
            mUserProfiles.clear();
            mAdapter.onResumeAdapter();

            Mesibo.ReadDbSession.endAllSessions();

            mDbSession = new Mesibo.ReadDbSession(null, 0, mReadQuery, this);
            mDbSession.enableSummary(true);
            int rcount = mDbSession.read(readCount);

            if (false && rcount < readCount && !mSyncDone) {
                mSyncDone = true;
                mDbSession.sync(readCount, this);
            }

        } else {
            mUserProfiles.clear();

            ArrayList profiles = Mesibo.getSortedUserProfiles();

            if (null != profiles && profiles.size() > 0 && !TextUtils.isEmpty(getString(R.string.create_new_group)) && mSelectionMode == ContactListFragment.MODE_SELECTCONTACT) {
                MesiboProfile user = new MesiboProfile();
                user.address = getString(R.string.create_new_group);
                user.setName(getString(R.string.create_new_group));
                user.setStatus(getString(R.string.add_member_text));
                //user.setPicturePath(null); //MesiboImages.getDefaultGroupPath();
                user.lookedup = true;

                UserData ud = new UserData(user);
                Bitmap b = MesiboImages.getDefaultGroupBitmap();
                ud.setImageThumbnail(b);
                ud.setImage(b);
                ud.setFixedImage(true); // so that it does not reset image on setUser
                user.other = ud;
                ud.setMessage(user.getStatus());
                mUserProfiles.add(user);
            }

            if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD && mMesiboUIOptions.showRecentInForward) {
                mUserProfiles.addAll(Mesibo.getRecentUserProfiles());
                if (mUserProfiles.size() > 0) {
                    MesiboProfile tempUserProfile = new MesiboProfile();
                    tempUserProfile.setName(FREQUENT_USERS_STRING);
                    mUserProfiles.add(0, tempUserProfile);
                }
                MesiboProfile tempUserProfile1 = new MesiboProfile();
                tempUserProfile1.setName(ALL_USERS_STRING);
                mUserProfiles.add(tempUserProfile1);

            }


            if (mSelectionMode == ContactListFragment.MODE_EDITGROUP) {
                if (0 == memberProfiles.size()) {
                    Mesibo.addListener(this);
                    MesiboProfile profile = getProfile(mGroupId);
                    profile.getGroupProfile().getMembers(100, true, this);
                    return;
                }

                mUserProfiles.addAll(memberProfiles);

                // add separator
                MesiboProfile tempUserProfile = new MesiboProfile();
                tempUserProfile.setName(GROUP_MEMBERS_STRING);
                mUserProfiles.add(0, tempUserProfile);


                // add separator for all profiles
                MesiboProfile tempUserProfile1 = new MesiboProfile();
                tempUserProfile1.setName(ALL_USERS_STRING);
                mUserProfiles.add(tempUserProfile1);

            }

            mUserProfiles.addAll(Mesibo.getSortedUserProfiles());

            /* filtering userlist for rogue userdata and groups in case for selection of users */
/**
            for (int i = mUserProfiles.size() - 1; i >= 0; i--) {
                MesiboProfile user = mUserProfiles.get(i);
                if (TextUtils.isEmpty(user.address) && user.groupid == 0) {
                    if (!user.getName().equalsIgnoreCase(ALL_USERS_STRING) && !user.getName().equalsIgnoreCase(FREQUENT_USERS_STRING)
                            && !user.getName().equalsIgnoreCase(GROUP_MEMBERS_STRING))
                        mUserProfiles.remove(i);
                } else if (TextUtils.isEmpty(user.getName()) && user.groupid > 0) {
                    mUserProfiles.remove(i);

                } else if (mSelectionMode == ContactListFragment.MODE_EDITGROUP || mSelectionMode == ContactListFragment.MODE_SELECTGROUP) {
                    if (user.groupid > 0) {
                        mUserProfiles.remove(i);
                    } else {
                        if (!TextUtils.isEmpty(user.address) && null != mGroupMembers && mGroupMembers.contains(user.address)) {
                            user.uiFlags = user.uiFlags | MesiboProfile.FLAG_MARKED;
                            showForwardLayout();
                        }
                    }
                }
            }

        }
        mAdapter.notifyChangeInData();
    }

    private void updateContacts(MesiboProfile userProfile) {
        if (null == userProfile) {
            showUserList(MESIBO_INTITIAL_READ_USERLIST);
            return;
        }

        if (null == userProfile.other)
            return;

        UserData data = UserData.getUserData(userProfile);
        int position = data.getUserListPosition();
        if (position >= 0)
            mAdapter.notifyItemChanged(position);

        return;

    }


    @Override
    public boolean Mesibo_onGetProfile(MesiboProfile userProfile) {
        return false;
    }

    @Override
    public void Mesibo_onProfileUpdated(MesiboProfile userProfile) {

        if (null != userProfile && null == userProfile.other)
            return;

        if (Mesibo.isUiThread()) {
            updateContacts(userProfile);
            return;
        }

        final MesiboProfile profile = userProfile;
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                updateContacts(profile);
            }
        });

    }

    @Override
    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {

    }

    @Override
    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
        mExistingMembers = members;
        for (MesiboGroupProfile.Member m : members) {
            memberProfiles.add(m.getProfile());
        }
        if (memberProfiles.size() > 0)
            showUserList(MESIBO_INTITIAL_READ_USERLIST);
    }

    @Override
    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {

    }

    @Override
    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {

    }

    @Override
    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long error) {

    }

    public class MessageContactAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int mBackground = 0;
        private Context mContext = null;
        private ArrayList<MesiboProfile> mDataList = null;
        private ArrayList<MesiboProfile> mUsers = null;
        private ArrayList<MesiboProfile> mSearchResults = null;
        private final ChatFragment mHost;
        private final SparseBooleanArray mSelectionItems;
        public int mCountProfileMatched = 0;

        public final static int SECTION_HEADER = 100;
        public final static int SECTION_CELLS = 300;

        public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
            public TextView mSectionTitle = null;


            public SectionHeaderViewHolder(View itemView) {
                super(itemView);
                mSectionTitle = itemView.findViewById(R.id.section_header);
            }
        }

        public class SectionCellsViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString = null;
            public View mView = null;
            public ImageView mContactsProfile = null;
            public TextView mContactsName = null;
            public TextView mContactsTime = null;
            public EmojiconTextView mContactsMessage = null;
            public ImageView mContactsDeliveryStatus = null;
            public TextView mNewMesAlert = null;
            public MenuPopupHelper PopupMenu = null;
            public RelativeLayout mHighlightView = null;
            public int position = 0;

            public SectionCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = view.findViewById(R.id.mes_rv_profile);
                mContactsName = view.findViewById(R.id.mes_rv_name);
                mContactsTime = view.findViewById(R.id.mes_rv_date);
                mContactsMessage = view.findViewById(R.id.mes_cont_post_or_details);
                mContactsDeliveryStatus = view.findViewById(R.id.mes_cont_status);
                mNewMesAlert = view.findViewById(R.id.mes_alert);
                mHighlightView = view.findViewById(R.id.highlighted_view);
            }

        }

        public MessageContactAdapter(Context context, ChatFragment host, ArrayList<MesiboProfile> list, ArrayList<MesiboProfile> searchResults) {

            this.mContext = context;
            mHost = host;
            mUsers = list;
            mSearchResults = searchResults;
            mDataList = list;
            mSelectionItems = new SparseBooleanArray();

        }


        public ArrayList<MesiboProfile> getActiveUserlist() {
            if (mIsMessageSearching)
                return mSearchResults;
            else
                return mUsers;
        }

        @Override
        public int getItemViewType(int position) {
            String address = mDataList.get(position).address;

            if (address == null && mDataList.get(position).groupid <= 0)
                return SECTION_HEADER;
            else
                return SECTION_CELLS;

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == SECTION_HEADER) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_header_title, parent, false);
                return new SectionHeaderViewHolder(v);

            } else {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list, parent, false);
                return new SectionCellsViewHolder(view);
            }
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, @SuppressLint("RecyclerView") final int position) {


            if (vh.getItemViewType() == SECTION_HEADER) {

                ((SectionHeaderViewHolder) vh).mSectionTitle.setText(mDataList.get(position).getName());


            } else {

                final int pos = position;
                final MesiboProfile user = mDataList.get(position);

                final SectionCellsViewHolder holder = (SectionCellsViewHolder) vh;

                holder.position = position;

                UserData userdata = UserData.getUserData(user);
                userdata.setUser(user); // in case user is changed dynamically
                userdata.setUserListPosition(position);

                final UserData data = userdata;
                //Toast.makeText(mContext, "" + mDataList.size(), Toast.LENGTH_SHORT).show();
                holder.mContactsName.setText(data.getUserName());

                if (mHost.mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
                    holder.mContactsTime.setVisibility(View.VISIBLE);
                    holder.mContactsTime.setText(data.getTime());
                } else {
                    holder.mContactsTime.setVisibility(View.GONE);
                }

                int imageDrawableId = 0;
                Drawable imageDrawable = null;
                int padding = 5;
                if (data.getLastMessage().equals(MESSAGE_DELETED_STRING)) {
                    imageDrawableId = DELETED_DRAWABLE;
                    imageDrawable = MesiboImages.getDeletedMessageDrawable();
                } else if (data.getLastMessage().equals(ATTACHMENT_STRING)) {
                    imageDrawableId = ATTACHMENT_ICON;
                } else if (data.getLastMessage().equals(LOCATION_STRING)) {
                    imageDrawableId = LOCATION_ICON;
                } else if (data.getLastMessage().equals(VIDEO_STRING)) {
                    imageDrawableId = VIDEO_ICON;
                } else if (data.getLastMessage().equals(IMAGE_STRING)) {
                    imageDrawableId = IMAGE_ICON;
                } else if (data.getLastMessage().equals(MISSED_VIDEO_CALL)) {
                    imageDrawableId = MISSED_VIDEOCALL_DRAWABLE;
                    imageDrawable = getMissedCallDrawable(true);
                } else if (data.getLastMessage().equals(MISSED_VOICE_CALL)) {
                    imageDrawableId = MISSED_VOICECALL_DRAWABLE;
                    imageDrawable = getMissedCallDrawable(false);
                } else {
                    imageDrawableId = 0;
                    padding = 0;
                }

                boolean typing = data.isTyping();
                if (typing) {
                    imageDrawableId = 0;
                    padding = 0;
                }

                if (mHost.mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
                    if (null != imageDrawable) {
                        holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawable, null, null, null);
                    } else {
                        holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawableId, 0, 0, 0);
                    }

                    holder.mContactsMessage.setCompoundDrawablePadding(padding);

                    if (!typing) {
                        holder.mContactsMessage.setText(userdata.getLastMessage());
                        holder.mContactsMessage.setTextColor(mMesiboUIOptions.mUserListStatusColor);
                    } else {
                        MesiboProfile typingProfile = data.getTypingProfile();

                        String typingText = MesiboConfiguration.USER_STATUS_TYPING;
                        if (null != typingProfile) {
                            typingText = typingProfile.getName() + " is " + MesiboConfiguration.USER_STATUS_TYPING;
                        }

                        holder.mContactsMessage.setText(typingText);
                        holder.mContactsMessage.setTextColor(mMesiboUIOptions.mUserListTypingIndicationColor);
                    }
                } else
                    holder.mContactsMessage.setText(null != user.getStatus() ? user.getStatus() : "");

                Bitmap b = data.getThumbnail(mLetterTileProvider);

                Drawable d = new RoundImageDrawable(b);

                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));

                if (mHost.mSelectionMode == ContactListFragment.MODE_MESSAGELIST && data.getUnreadCount() > 0) {
                    holder.mNewMesAlert.setVisibility(View.VISIBLE);
                    holder.mNewMesAlert.setText(String.valueOf(data.getUnreadCount()));

                } else {
                    holder.mNewMesAlert.setVisibility(View.INVISIBLE);
                }

                holder.mContactsDeliveryStatus.setVisibility(View.GONE);
                if (!typing && mHost.mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
                    holder.mContactsDeliveryStatus.setVisibility(View.VISIBLE);

                    int sts = data.getStatus();
                    if (sts == Mesibo.MSGSTATUS_RECEIVEDREAD || sts == Mesibo.MSGSTATUS_RECEIVEDNEW || sts == Mesibo.MSGSTATUS_CALLMISSED || sts == Mesibo.MSGSTATUS_CUSTOM || data.isDeletedMessage()) {
                        holder.mContactsDeliveryStatus.setVisibility(View.GONE);
                    } else {
                        holder.mContactsDeliveryStatus.setImageBitmap(MesiboImages.getStatusImage(sts));
                    }
                }

                if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == ContactListFragment.MODE_SELECTGROUP || mSelectionMode == ContactListFragment.MODE_EDITGROUP) {
                    if ((mDataList.get(position).uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                        holder.mHighlightView.setVisibility(View.VISIBLE);
                        mHost.showForwardLayout();
                    } else {
                        holder.mHighlightView.setVisibility(View.GONE);
                    }
                }

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == ContactListFragment.MODE_SELECTGROUP || mSelectionMode == ContactListFragment.MODE_EDITGROUP) {
                            if ((user.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                                user.uiFlags = user.uiFlags & ~MesiboProfile.FLAG_MARKED;
                            } else {
                                user.uiFlags = user.uiFlags | MesiboProfile.FLAG_MARKED;
                            }

                            notifyDataSetChanged();

                            if (isForwardContactsSelected()) {
                                mHost.showForwardLayout();
                            } else {
                                mHost.hideForwardLayout();
                            }

                        } else {

                            //TBD, it's checking user name, instead we should set flag
                            if (null != user.getName() && null != getString(R.string.create_new_group) && user.getName().equals(getString(R.string.create_new_group)) && mSelectionMode == ContactListFragment.MODE_SELECTCONTACT) {
                                //      MesiboUIManager.launchContactActivity(getActivity(), 0, MODE_SELECTGROUP, 0, false, false, null);
                                getActivity().finish();
                                return;
                            }

                            data.clearUnreadCount();
                            Context context = v.getContext();

                            boolean handledByApp = onClickUser(user.address, user.groupid, mHost.mForwardId);

                            if (!handledByApp) {
                                QampUiHelper.launchMessagingActivity(getActivity(), mHost.mForwardId, user.address, user.groupid);
                                mHost.mForwardId = 0;
                                if (mSelectionMode != ContactListFragment.MODE_MESSAGELIST)
                                    getActivity().finish();
                            } else {
                                mHost.mForwardId = 0;
                            }
                        }
                    }
                });

                holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public boolean onLongClick(View v) {
                        if (mSelectionMode == ContactListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == ContactListFragment.MODE_SELECTGROUP || mSelectionMode == ContactListFragment.MODE_EDITGROUP)
                            return true;

                        if (!TextUtils.isEmpty(getString(R.string.create_new_group)) && user.getName().equalsIgnoreCase(getString(R.string.create_new_group)))
                            return true;
                        try {
                            if (true || null == holder.PopupMenu) {

                                MenuBuilder menuBuilder = new MenuBuilder(getActivity());
                                MenuInflater inflater = new MenuInflater(getActivity());
                                inflater.inflate(R.menu.selected_contact, menuBuilder);
                                holder.PopupMenu = new MenuPopupHelper(mContext, menuBuilder, holder.mView);
                                holder.PopupMenu.setForceShowIcon(true);
                                menuBuilder.setCallback(new MenuBuilder.Callback() {
                                    @Override
                                    public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                        if (item.getItemId() == R.id.menu_remove) {
                                            if (mHost.mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
                                                Mesibo.deleteMessages(user.address, user.groupid, 0);
                                                UserData data = (UserData) user.other;
                                                if (null != data) {
                                                    data.setUnreadCount(0);
                                                }
                                                mDataList.remove(position);
                                                notifyDataSetChanged();
                                            } else if (mHost.mSelectionMode == ContactListFragment.MODE_SELECTCONTACT) {
                                                mDataList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                            return true;
                                        } /*else if (item.getItemId() == R.id.menu_archive) {
                                            //user.flag |= MesiboProfile.FLAG_ARCHIVCE;
                                        } */
/**
                                        return false;

                                    }

                                    @Override
                                    public void onMenuModeChange(MenuBuilder menu) {

                                    }
                                });
                                holder.PopupMenu.show();
                            }

                            holder.PopupMenu.show();
                        } catch (Exception e) {

                        }
                        return true;
                    }
                });
            }

        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (!(holder instanceof SectionCellsViewHolder)) {
                return;
            }

            SectionCellsViewHolder vh = (SectionCellsViewHolder) holder;
            //vh.clearData();
        }

        @Override
        public int getItemCount() {
            handleEmptyUserList(mDataList.size());
            return mDataList.size();
        }

        public void notifyChangeInData() {
            mUiUpdateTimestamp = Mesibo.getTimestamp();
            mDataList = getActiveUserlist();
            notifyDataSetChanged();
        }

        public void onResumeAdapter() {
            mSearchResults.clear();
            mIsMessageSearching = false;
            mUsers.clear();
            mDataList = mUsers;
        }

        public Boolean isForwardContactsSelected() {
            Boolean retValue = false;
            for (MesiboProfile d : mDataList) {
                if ((d.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                    retValue = true;
                }
            }
            return retValue;
        }

        public void createNewGroup() {
            mMemberProfiles.clear();
            for (MesiboProfile d : mDataList) {
                if ((d.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                    if (!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            // MesiboUIManager.launchGroupActivity(getActivity(), null);
            getActivity().finish();
            return;
        }

        public void modifyGroupDetail() {
            mMemberProfiles.clear();
            for (MesiboProfile d : mDataList) {
                if ((d.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                    if (!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            // MesiboUIManager.launchGroupActivity(getActivity(), mGroupEditBundle);
            getActivity().finish();
            return;
        }

        public void forwardMessageToContacts() {
            mMemberProfiles.clear();
            int i = 0;
            for (MesiboProfile d : mDataList) {
                if ((d.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                    d.uiFlags &= ~MesiboProfile.FLAG_MARKED;
                    if (!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }

            if (mMemberProfiles.size() == 0) {
                return;
            }

            for (i = 0; i < mMemberProfiles.size(); i++) {
                MesiboProfile user = mMemberProfiles.get(i);
                UserData data = (UserData) (user).other;
                Mesibo.MessageParams messageParams = new Mesibo.MessageParams(data.getPeer(), data.getGroupId(), Mesibo.FLAG_DEFAULT, 0);

                for (int j = 0; null != mForwardMessageIds && j <= mForwardMessageIds.length - 1; j++) {
                    if (mForwardMessageIds[j] > 0) {
                        long mId = Mesibo.random();
                        Mesibo.forwardMessage(messageParams, mId, mForwardMessageIds[j]);
                    }
                }

                if (!TextUtils.isEmpty(mForwardedMessage)) {
                    long mId = Mesibo.random();
                    Mesibo.sendMessage(messageParams, mId, mForwardedMessage);
                }
            }


            if (!mCloseAfterForward && mMemberProfiles.size() == 1) {
                MesiboProfile user = mMemberProfiles.get(0);
                UserData data = (UserData) (user).other;
                data.clearUnreadCount();
                boolean handledByApp = onClickUser(user.address, user.groupid, 0);
                if (!handledByApp)
                    QampUiHelper.launchMessagingActivity(getActivity(), 0, user.address, user.groupid);
            }

            getActivity().finish();
            mForwardId = 0;
            return;
        }

        public void filter(String text) {
            mSearchQuery = text;
            mCountProfileMatched = 0;
            mSearchResults.clear();
            mIsMessageSearching = false;
            if (TextUtils.isEmpty(text)) {
                mDataList = mUsers;
            } else {
                mDataList = mSearchResults;
                text = text.toLowerCase();

                for (MesiboProfile item : mUsers) {
                    if (item.getName().toLowerCase().contains(text) || item.getName().equals(ALL_USERS_STRING) || item.getName().equals(FREQUENT_USERS_STRING) || item.getName().equals(GROUP_MEMBERS_STRING)) {
                        mSearchResults.add(item);
                    }
                    if (item.getAddress().toLowerCase().contains(text) || item.getAddress().equals(ALL_USERS_STRING) || item.getAddress().equals(FREQUENT_USERS_STRING) || item.getAddress().equals(GROUP_MEMBERS_STRING)) {
                        mSearchResults.add(item);
                    }

                }
                if (mSearchResults.size() > 0 && mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {

                    MesiboProfile tempUserProfile = new MesiboProfile();
                    mCountProfileMatched = mSearchResults.size();
                    tempUserProfile.setName(mSearchResults.size() + " " + USERS_STRING_USERLIST_SEARCH);
                    mSearchResults.add(0, tempUserProfile);
                }

                mDataList = mSearchResults;
                setEmptyViewText();

                if (mSelectionMode == ContactListFragment.MODE_MESSAGELIST) {
                    mEmptyView.setText(getResources().getString(R.string.no_result_found));
                    if (!TextUtils.isEmpty(text)) {
                        mIsMessageSearching = true;
                        Mesibo.ReadDbSession rbd = new Mesibo.ReadDbSession(null, 0, text, ChatFragment.this);
                        rbd.read(MESIBO_SEARCH_READ_USERLIST);

                    }
                }
            }

        }
    }

    private void showConatcts() {
        ContactsBottomSheetFragment contactsBottomSheetFragment = new ContactsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ContactListFragment.MESSAGE_LIST_MODE, ContactListFragment.MODE_SELECTCONTACT);
        bundle.putLong(MesiboUI.MESSAGE_ID, mForwardIdForContactList);

        if (!TextUtils.isEmpty(""))
            bundle.putString(MesiboUI.MESSAGE_CONTENT, forwardMessage);

        bundle.putLongArray(MesiboUI.MESSAGE_IDS, mForwardIds);
        if (mMode == ContactListFragment.MODE_EDITGROUP)
            bundle.putBundle(MesiboUI.BUNDLE, mEditGroupBundle);

        bundle.putBoolean(MesiboUI.FORWARD_AND_CLOSE, false);
        contactsBottomSheetFragment.setArguments(bundle);
        contactsBottomSheetFragment.show(getChildFragmentManager(), contactsBottomSheetFragment.getTag());
    }


}
*/