package com.qamp.app.messaging;

import static com.qamp.app.messaging.MesiboUserListActivityNew.MesiboUserListActivityNewActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboPresence;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconTextView;
import com.qamp.app.R;
import com.qamp.app.messaging.AllUtils.LetterTileProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class UserListFragment extends Fragment implements Mesibo.MessageListener,
        Mesibo.PresenceListener, Mesibo.ConnectionListener, Mesibo.ProfileListener, Mesibo.SyncListener, Mesibo.GroupListener {
    public static MesiboGroupProfile.Member[] mExistingMembers = null;
    public static ArrayList<MesiboProfile> mMemberProfiles = new ArrayList<>();
    public static ArrayList<MesiboProfile> mMemberGroup = new ArrayList<>();
    public static boolean isSheetOpen = false;
    /* access modifiers changed from: private */
    public boolean mCloseAfterForward = false;
    public boolean mContactView = false;
    public TextView mEmptyView;
    public long mForwardId = 0;
    /* access modifiers changed from: private */
    public long[] mForwardMessageIds = null;
    /* access modifiers changed from: private */
    public String mForwardedMessage = null;
    /* access modifiers changed from: private */
    public Boolean mIsMessageSearching = false;
    /* access modifiers changed from: private */
    public String mSearchQuery = null;
    /* access modifiers changed from: private */
    public int mSelectionMode = 0;
    /* access modifiers changed from: private */
    public Handler mUiUpdateHandler = new Handler(Looper.getMainLooper());
    /* access modifiers changed from: private */
    public long mUiUpdateTimestamp = 0;
    MessageContactAdapter mAdapter = null;
    /* access modifiers changed from: private */
    public Runnable mUiUpdateRunnable = new Runnable() {
        public void run() {
            if (UserListFragment.this.mAdapter != null) {
                UserListFragment.this.mAdapter.notifyChangeInData();
            }
        }
    };
    Bundle mGroupEditBundle = null;
    long mGroupId = 0;
    Set<String> mGroupMembers = null;
    MesiboProfile mGroupProfile = null;
    LetterTileProvider mLetterTileProvider = null;
    MesiboUI.Config mMesiboUIOptions = null;
    RecyclerView mRecyclerView = null;
    LinearLayout horizontal_channel_recycler;
    ArrayList<MesiboProfile> tempmemberProfiles = new ArrayList<>();
    ArrayList<MesiboProfile> memberProfiles = new ArrayList<>();
    long mForwardIdForContactList = 0;
    String forwardMessage = MesiboUI.MESSAGE_CONTENT;
    long[] mForwardIds = {0};
    int mMode = 0;
    Bundle mEditGroupBundle = null;
    ImageView search_image;
    LinearLayout name_tite_layout, search_view;
    androidx.appcompat.widget.SearchView search_func;
    SearchView searchChats;
    private ArrayList<MesiboProfile> mAdhocUserList = null;
    private MesiboReadSession mDbSession = null;
    private boolean mFirstOnline = false;
    private WeakReference<MesiboUserListFragment.FragmentListener> mListener = null;
    private MesiboUI.Listener mMesiboUIHelperListener = null;
    private String mReadQuery = null;
    private long mRefreshTs = 0;
    private ArrayList<MesiboProfile> mSearchResultList = null;
    private boolean mSyncDone = false;
    private Timer mUiUpdateTimer = null;
    private TimerTask mUiUpdateTimerTask = null;
    private ArrayList<MesiboProfile> mUserProfiles = null;
    private LinearLayout mforwardLayout;
    private LinearLayout fabadd;
    private ImageView isOnlineDot;

    public void updateTitle(String title) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if (l != null) {
            l.Mesibo_onUpdateTitle(title);
        }
    }

    public void updateSubTitle(String title) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if (l != null) {
            l.Mesibo_onUpdateSubTitle(title);
        }
    }

    public boolean onClickUser(String address, long groupid, long forwardid) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if (l == null) {
            return false;
        }
        return l.Mesibo_onClickUser(address, groupid, forwardid);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MesiboImages.init(getActivity());
        this.mMesiboUIHelperListener = MesiboUI.getListener();
        this.mMesiboUIOptions = MesiboUI.getConfig();
        this.mSelectionMode = MesiboUserListFragment.MODE_MESSAGELIST;
        this.mReadQuery = null;
        Bundle b = getArguments();
        if (b != null) {
            this.mSelectionMode = b.getInt(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_MESSAGELIST);
            this.mReadQuery = b.getString("query", (String) null);
        }
        if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD) {
            this.mForwardId = getArguments().getLong("mid");
        }
        if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD) {
            this.mForwardMessageIds = getArguments().getLongArray(MesiboUI.MESSAGE_IDS);
            this.mForwardedMessage = getArguments().getString("message");
            this.mCloseAfterForward = getArguments().getBoolean(MesiboUI.FORWARD_AND_CLOSE, false);
        }
        if (this.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
            String title = this.mMesiboUIOptions.messageListTitle;
            if (TextUtils.isEmpty(title)) {
                title = Mesibo.getAppName();
            }
            updateTitle(title);
        } else if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT) {
            updateTitle(this.mMesiboUIOptions.selectContactTitle);
        } else if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD) {
            updateTitle(this.mMesiboUIOptions.forwardTitle);
        } else if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP) {
            updateTitle(this.mMesiboUIOptions.selectGroupContactsTitle);
        } else if (this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
            updateTitle(this.mMesiboUIOptions.selectGroupContactsTitle);
            this.mGroupEditBundle = getArguments().getBundle(MesiboUI.BUNDLE);
            if (this.mGroupEditBundle != null) {
                this.mGroupId = this.mGroupEditBundle.getLong(MesiboUI.GROUP_ID);
                this.mGroupProfile = Mesibo.getProfile(this.mGroupId);
            }
        }
        if (this.mMesiboUIOptions.useLetterTitleImage) {
            this.mLetterTileProvider = new LetterTileProvider(getActivity(), 60, this.mMesiboUIOptions.mLetterTitleColors);
        }
        this.mSearchResultList = new ArrayList<>();
        this.mUserProfiles = new ArrayList<>();
        int layout = R.layout.fragment_message_contact;
        if (MesiboUI.getConfig().mUserListFragmentLayout != 0) {
            layout = MesiboUI.getConfig().mUserListFragmentLayout;
        }
        View view = inflater.inflate(layout, container, false);
        setHasOptionsMenu(true);
        this.mforwardLayout = (LinearLayout) view.findViewById(R.id.bottom_forward_btn);
        this.mforwardLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP) {
                    UserListFragment.this.mAdapter.createNewGroup();
                } else if (UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                     UserListFragment.this.mAdapter.modifyGroupDetail();
                } else if (UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD) {
                    UserListFragment.this.mAdapter.forwardMessageToContacts();
                }
            }
        });
        this.mEmptyView = (TextView) view.findViewById(R.id.emptyview_text);
        setEmptyViewText();
        this.mUserProfiles = new ArrayList<>();
        this.mRecyclerView = view.findViewById(R.id.message_contact_frag_rv);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this.mRecyclerView.getContext()));
        this.mAdapter = new MessageContactAdapter(getActivity(), this, this.mUserProfiles, this.mSearchResultList);
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.horizontal_channel_recycler = view.findViewById(R.id.horizontal_channel_recycler);
        this.fabadd = view.findViewById(R.id.fab_add);
        this.fabadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSheetOpen)
                    showConatcts();
            }
        });
        this.isOnlineDot = (ImageView) view.findViewById(R.id.isOnlineDot);

        searchChats = view.findViewById(R.id.searchChats);
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
        search_image = getActivity().findViewById(R.id.search_image);
        name_tite_layout = getActivity().findViewById(R.id.name_tite_layout);
        search_view = getActivity().findViewById(R.id.search_view);
        search_func = getActivity().findViewById(R.id.search_func);
        if (getActivity() == MesiboUserListActivityNewActivity && MesiboUserListActivityNewActivity != null) {
            this.horizontal_channel_recycler.setVisibility(View.VISIBLE);
            this.fabadd.setVisibility(View.VISIBLE);
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
        }
        return view;
    }

    private void showConatcts() {

        ContactsBottomSheetFragment contactsBottomSheetFragment = new ContactsBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_SELECTCONTACT);
        bundle.putLong(MesiboUI.MESSAGE_ID, mForwardIdForContactList);

        if (!TextUtils.isEmpty(""))
            bundle.putString(MesiboUI.MESSAGE_CONTENT, forwardMessage);

        bundle.putLongArray(MesiboUI.MESSAGE_IDS, mForwardIds);
        if (mMode == MesiboUserListFragment.MODE_EDITGROUP)
            bundle.putBundle(MesiboUI.BUNDLE, mEditGroupBundle);

        bundle.putBoolean(MesiboUI.FORWARD_AND_CLOSE, false);
        contactsBottomSheetFragment.setArguments(bundle);
        contactsBottomSheetFragment.show(getChildFragmentManager(), contactsBottomSheetFragment.getTag());
    }

    public MesiboUserListFragment.FragmentListener getListener() {
        if (this.mListener == null) {
            return null;
        }
        return (MesiboUserListFragment.FragmentListener) this.mListener.get();
    }

    public void setListener(MesiboUserListFragment.FragmentListener listener) {
        this.mListener = new WeakReference<>(listener);
    }

    public void setEmptyViewText() {
        if (this.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
            this.mEmptyView.setText(MesiboUI.getConfig().emptyMessageListMessage);
        } else {
            this.mEmptyView.setText(MesiboUI.getConfig().emptyUserListMessage);
        }
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        UserListFragment.super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if (this.mMesiboUIHelperListener != null) {
            this.mMesiboUIHelperListener.MesiboUI_onGetMenuResourceId(getActivity(), 0, (MesiboProfile) null, menu);
            MenuItem searchViewItem = menu.findItem(R.id.mesibo_search);
            if (searchViewItem != null && this.mMesiboUIOptions.enableSearch) {
                SearchView searchView = new SearchView(((AppCompatActivity) getActivity()).getSupportActionBar().getThemedContext());
                MenuItemCompat.setShowAsAction(searchViewItem, 9);
                MenuItemCompat.setActionView(searchViewItem, searchView);
                searchView.setIconifiedByDefault(true);
                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                    public boolean onClose() {
                        Log.d("search View closed", "SEARCHVIEW");
                        String unused = UserListFragment.this.mSearchQuery = null;
                        return false;
                    }
                });
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    public boolean onQueryTextSubmit(String query) {
                        return true;
                    }

                    public boolean onQueryTextChange(String newText) {
                        UserListFragment.this.mAdapter.filter(newText);
                        UserListFragment.this.mAdapter.notifyDataSetChanged();
                        return true;
                    }
                });
                searchView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    }
                });
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getActivity();
        if (context != null) {
            if (item.getItemId() == R.id.mesibo_contacts) {
                MesiboUIManager.launchContactActivity(context, 0, MesiboUserListFragment.MODE_SELECTCONTACT, 0, false, false, (Bundle) null, "");
            } else if (!(item.getItemId() == R.id.mesibo_search || this.mMesiboUIHelperListener == null)) {
                this.mMesiboUIHelperListener.MesiboUI_onMenuItemSelected(context, 0, (MesiboProfile) null, item.getItemId());
            }
        }
        return false;
    }

    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.mesibo_contacts);
        if (item != null && this.mSelectionMode != MesiboUserListFragment.MODE_MESSAGELIST) {
            item.setVisible(false);
        }
    }

    public void filterUsersByName(String newText) {
        if (this.mAdapter != null) {
            this.mAdapter.filter(newText);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void showForwardLayout() {
        this.mforwardLayout.setVisibility(View.VISIBLE);
    }

    public void handleEmptyUserList(int userListsize) {
        if (userListsize == 0) {
            this.mRecyclerView.setVisibility(View.INVISIBLE);
            this.mEmptyView.setVisibility(View.VISIBLE);
            return;
        }
        this.mRecyclerView.setVisibility(View.VISIBLE);
        this.mEmptyView.setVisibility(View.GONE);
    }

    public void hideForwardLayout() {
        this.mforwardLayout.setVisibility(View.GONE);
    }

    private void updateNotificationBadge() {
        if (!this.mMesiboUIOptions.mEnableNotificationBadge) {
        }
    }

    public synchronized void addNewMessage(MesiboMessage params) {
        if (params.groupid <= 0 || params.groupProfile != null) {
            MesiboUserListFragment.FragmentListener l = getListener();
            if (l == null || !l.Mesibo_onUserListFilter(params)) {
                UserData userData = UserData.getUserData(params);
                if (this.mIsMessageSearching.booleanValue() && params.isDbMessage()) {
                    this.mAdhocUserList = this.mAdapter.getActiveUserlist();
                    if (this.mAdhocUserList.size() > this.mAdapter.mCountProfileMatched + 1) {
                        int i = this.mAdapter.mCountProfileMatched == 0 ? this.mAdapter.mCountProfileMatched : this.mAdapter.mCountProfileMatched + 1;
                        this.mAdhocUserList.get(i).setName(String.valueOf(this.mAdhocUserList.size() - i) + " " + MesiboConfiguration.MESSAGE_STRING_USERLIST_SEARCH);
                    } else {
                        MesiboProfile mup = new MesiboProfile();
                        mup.setName("1 Messages");
                        this.mAdhocUserList.add(this.mAdhocUserList.size(), mup);
                    }
                }
                MesiboProfile user = params.profile;
                if (params.groupProfile != null) {
                    user = params.groupProfile;
                }
                if (user == null) {
                    Log.d(MesiboUserListActivity.TAG, "Should not happen");
                }
                if (this.mIsMessageSearching.booleanValue()) {
                    if (params.groupProfile != null) {
                        user = params.groupProfile.cloneProfile();
                    } else {
                        user = params.profile.cloneProfile();
                    }
                }
                if (user.other == null) {
                    user.other = new UserData(user);
                }
                ((UserData) user.other).setMessage(params);
                if (params.isRealtimeMessage()) {
                    updateNotificationBadge();
                }
                int i2 = 0;
                while (true) {
                    if (i2 < this.mAdhocUserList.size()) {
                        UserData mcd = (UserData) this.mAdhocUserList.get(i2).other;
                        if (mcd != null && params.compare(mcd.getPeer(), mcd.getGroupId())) {
                            this.mAdhocUserList.remove(i2);
                            break;
                        }
                        i2++;
                    } else {
                        break;
                    }
                }
                if (params.isDbSummaryMessage() || params.isDbMessage()) {
                    this.mAdhocUserList.add(user);
                } else {
                    this.mAdhocUserList.add(0, user);
                }
                if (this.mUiUpdateTimer != null) {
                    this.mUiUpdateTimer.cancel();
                    this.mUiUpdateTimer = null;
                }
                if (params.isRealtimeMessage()) {
                    long ts = Mesibo.getTimestamp();
                    if (ts - this.mUiUpdateTimestamp > 2000) {
                        this.mAdapter.notifyChangeInData();
                    } else {
                        long timeout = 2000;
                        if (ts - params.ts < 5000) {
                            timeout = 500;
                        }
                        this.mUiUpdateTimestamp = ts;
                        this.mUiUpdateTimer = new Timer();
                        this.mUiUpdateTimerTask = new TimerTask() {
                            public void run() {
                                UserListFragment.this.mUiUpdateHandler.post(UserListFragment.this.mUiUpdateRunnable);
                            }
                        };
                        this.mUiUpdateTimer.schedule(this.mUiUpdateTimerTask, timeout);
                    }
                }
            }
        }
    }


    public void Mesibo_onPresence(MesiboPresence msg) {
        if (3L == msg.presence || 4L == msg.presence || 11L == msg.presence) {
            if (null != msg && null != msg.profile) {
                if (msg.groupid <= 0L || null != msg.groupProfile) {
                    MesiboProfile profile = msg.profile;
                    if (msg.isGroupMessage()) {
                        profile = Mesibo.getProfile(msg.groupid);
                        if (null == profile) {
                            return;
                        }
                    }

                    UserData data = UserData.getUserData(profile);
                    int position = data.getUserListPosition();
                    if (position >= 0) {
                        if (this.mAdhocUserList.size() > position) {
                            if (3L == msg.presence && msg.isGroupMessage()) {
                                data.setTypingUser(msg.profile);
                            }

                            try {
                                if (profile != this.mAdhocUserList.get(position)) {
                                    return;
                                }
                            } catch (Exception var6) {
                                return;
                            }

                            this.mAdapter.notifyItemChanged(position);
                        }
                    }
                }
            }
        }
    }

    public void Mesibo_onPresenceRequest(MesiboPresence mesiboPresence) {
    }

    private void updateUiIfLastMessage(MesiboMessage params) {
        if (params.isLastMessage()) {
            if (!this.mIsMessageSearching.booleanValue() && !TextUtils.isEmpty(this.mSearchQuery)) {
                this.mAdapter.filter(this.mSearchQuery);
            }
            this.mAdapter.notifyChangeInData();
            updateNotificationBadge();
        }
    }

    public void Mesibo_onMessage(MesiboMessage msg) {
        if (msg.isIncomingCall() || msg.isOutgoingCall() || msg.isEndToEndEncryptionStatus()) {
            updateUiIfLastMessage(msg);
        } else if (msg.groupid <= 0 || msg.groupProfile != null) {
            addNewMessage(msg);
            updateUiIfLastMessage(msg);
        } else {
            updateUiIfLastMessage(msg);
        }
    }

    public void Mesibo_onMessageStatus(MesiboMessage msg) {
        for (int i = 0; i < this.mUserProfiles.size(); i++) {
            UserData mcd = (UserData) this.mUserProfiles.get(i).other;
            if (mcd.getmid() != 0 && mcd.getmid() == msg.mid) {
                mcd.setMessage(msg);
                if (msg.isDeleted()) {
                    mcd.setMessage(MesiboUI.getConfig().deletedMessageTitle);
                    mcd.setDeletedMessage(true);
                }
                this.mAdapter.notifyItemChanged(i);
            }
        }
    }

    public void Mesibo_onMessageUpdate(MesiboMessage msg) {
        Mesibo_onMessageStatus(msg);
    }

    public void Mesibo_onConnectionStatus(int status) {
        int status2 = Mesibo.getConnectionStatus();
        if (status2 == 1) {
            if (!this.mFirstOnline && Mesibo.getLastProfileUpdateTimestamp() > this.mRefreshTs) {
                showUserList(100);
            }
            this.mFirstOnline = true;
            if (this.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                String title = this.mMesiboUIOptions.messageListTitle;
                if (TextUtils.isEmpty(title)) {
                    title = Mesibo.getAppName();
                }
                updateTitle(title);
            }
            updateSubTitle(this.mMesiboUIOptions.onlineIndicationTitle);
        } else if (status2 == 6) {
            updateSubTitle(this.mMesiboUIOptions.connectingIndicationTitle);
        } else if (status2 == 10) {
            updateSubTitle(this.mMesiboUIOptions.suspendIndicationTitle);
            Utils.showServicesSuspendedAlert(getActivity());
        } else if (status2 == 8) {
            updateSubTitle(this.mMesiboUIOptions.noNetworkIndicationTitle);
        } else if (status2 == 22) {
            getActivity().finish();
        } else {
            updateSubTitle(this.mMesiboUIOptions.offlineIndicationTitle);
        }
    }

    private String getDate(long time) {
        int days = Mesibo.daysElapsed(time);
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        if (days == 0) {
            return DateFormat.format("HH:mm", cal).toString();
        }
        if (days == 1) {
            return "Yesterday";
        }
        if (days < 7) {
            return DateFormat.format("E, dd MMM", cal).toString();
        }
        return DateFormat.format("dd-MM-yyyy", cal).toString();
    }

    public void onResume() {
        UserListFragment.super.onResume();
        showUserList(100);
        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());
        Utils.showServicesSuspendedAlert(getActivity());
    }

    public void onPause() {
        UserListFragment.super.onPause();
        Mesibo.removeListener(this);
    }

    public void onStop() {
        UserListFragment.super.onStop();
        if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
            Iterator<MesiboProfile> it = this.mUserProfiles.iterator();
            while (it.hasNext()) {
                it.next().uiFlags &= -16777217;
            }
        }
    }

    public void onLongClick() {
    }

    public void Mesibo_onSync(int count) {
        int i = count;
        if (count > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    UserListFragment.this.showUserList(100);
                }
            });
        }
    }

    public void showUserList(int readCount) {
        Log.d("showUserList", "showUserList");
        setEmptyViewText();
        if (this.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
            this.mRefreshTs = Mesibo.getTimestamp();
            Mesibo.addListener(this);
            this.mAdhocUserList = this.mUserProfiles;
            this.mUserProfiles.clear();
            this.mAdapter.onResumeAdapter();
            MesiboReadSession.endAllSessions();
            this.mDbSession = new MesiboReadSession(this);
            this.mDbSession.setQuery(this.mReadQuery);
            this.mDbSession.enableSummary(true);
            this.mDbSession.read(readCount);
        } else {
            this.mUserProfiles.clear();
            ArrayList profiles = Mesibo.getSortedUserProfiles();
            if (profiles != null && profiles.size() > 0 && !TextUtils.isEmpty(this.mMesiboUIOptions.createGroupTitle) && this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT) {
                MesiboProfile user = new MesiboProfile();
                user.address = this.mMesiboUIOptions.createGroupTitle;
                user.setName(this.mMesiboUIOptions.createGroupTitle);
                //user.setStatus(MesiboConfiguration.CREATE_NEW_GROUP_MESSAGE_STRING);
                user.lookedup = true;
                UserData ud = new UserData(user);
                Bitmap b = MesiboImages.getDefaultGroupBitmap();
                ud.setImageThumbnail(b);
                ud.setImage(b);
                ud.setFixedImage(true);
                user.other = ud;
                ud.setMessage(user.getStatus());
                this.mUserProfiles.add(user);
            }
            if (this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD && this.mMesiboUIOptions.showRecentInForward) {
                this.mUserProfiles.addAll(Mesibo.getRecentUserProfiles());
                if (this.mUserProfiles.size() > 0) {
                    MesiboProfile tempUserProfile = new MesiboProfile();
                    tempUserProfile.setName(String.valueOf(this.mMesiboUIOptions.recentUsersTitle));
                    this.mUserProfiles.add(0, tempUserProfile);
                }
                MesiboProfile tempUserProfile1 = new MesiboProfile();
                tempUserProfile1.setName(String.valueOf(this.mMesiboUIOptions.allUsersTitle));
                this.mUserProfiles.add(tempUserProfile1);
            }
            if (this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                if (this.memberProfiles.size() == 0) {
                    Mesibo.addListener(this);
                    Mesibo.getProfile(this.mGroupId).getGroupProfile().getMembers(100, true, this);
                    return;
                }

                this.mUserProfiles.addAll(this.memberProfiles);
                MesiboProfile tempUserProfile2 = new MesiboProfile();
                tempUserProfile2.setName(String.valueOf(this.mMesiboUIOptions.groupMembersTitle));
                this.mUserProfiles.add(0, tempUserProfile2);
                MesiboProfile tempUserProfile12 = new MesiboProfile();
                tempUserProfile12.setName(String.valueOf(this.mMesiboUIOptions.allUsersTitle));
                this.mUserProfiles.add(tempUserProfile12);
            }
            this.mUserProfiles.addAll(Mesibo.getSortedUserProfiles());
            for (int i = this.mUserProfiles.size() - 1; i >= 0; i--) {
                MesiboProfile user2 = this.mUserProfiles.get(i);
                if (!TextUtils.isEmpty(user2.address) || user2.groupid != 0) {
                    if (TextUtils.isEmpty(user2.getName()) && user2.groupid > 0) {
                        this.mUserProfiles.remove(i);
                    } else if (this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP || this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP) {
                        if (user2.groupid > 0) {
                            this.mUserProfiles.remove(i);
                        } else if (!TextUtils.isEmpty(user2.address) && this.mGroupMembers != null && this.mGroupMembers.contains(user2.address)) {
                            user2.uiFlags |= 16777216;
                            showForwardLayout();
                        }
                    }
                } else if (!user2.getName().equalsIgnoreCase(this.mMesiboUIOptions.allUsersTitle) && !user2.getName().equalsIgnoreCase(this.mMesiboUIOptions.recentUsersTitle) && !user2.getName().equalsIgnoreCase(this.mMesiboUIOptions.groupMembersTitle)) {
                    this.mUserProfiles.remove(i);
                }
            }
        }
        this.mAdapter.notifyChangeInData();
    }

    /* access modifiers changed from: private */
    public void updateContacts(MesiboProfile userProfile) {
        int position;
        if (userProfile == null) {
            showUserList(100);
        } else if (userProfile.other != null && (position = UserData.getUserData(userProfile).getUserListPosition()) >= 0) {
            if (userProfile.isDeleted()) {
                this.mUserProfiles.remove(position);
                this.mAdapter.notifyDataSetChanged();
            } else if (position >= 0) {
                this.mAdapter.notifyItemChanged(position);
            }
        }
    }

    public boolean Mesibo_onGetProfile(MesiboProfile userProfile) {
        return false;
    }

    public void Mesibo_onProfileUpdated(MesiboProfile userProfile) {
        if (userProfile != null && userProfile.other == null) {
            return;
        }
        if (Mesibo.isUiThread()) {
            updateContacts(userProfile);
            return;
        }
        final MesiboProfile profile = userProfile;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                UserListFragment.this.updateContacts(profile);
            }
        });
    }

    public void Mesibo_onGroupCreated(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupJoined(MesiboProfile mesiboProfile) {
    }

    public void Mesibo_onGroupLeft(MesiboProfile mesiboProfile) {
    }
    public void Mesibo_onGroupMembers(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
        mExistingMembers = members;

        for (MesiboGroupProfile.Member m : members) {
            this.tempmemberProfiles.add(m.getProfile());
        }

        Set<MesiboProfile> s = new HashSet<MesiboProfile>(tempmemberProfiles);
     memberProfiles = new ArrayList<>(s);
        if (this.memberProfiles.size() > 0) {
            showUserList(100);
        }

    }

    public void Mesibo_onGroupMembersJoined(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
    }

    public void Mesibo_onGroupMembersRemoved(MesiboProfile mesiboProfile, MesiboGroupProfile.Member[] members) {
    }

    public void Mesibo_onGroupSettings(MesiboProfile mesiboProfile, MesiboGroupProfile.GroupSettings groupSettings, MesiboGroupProfile.MemberPermissions memberPermissions, MesiboGroupProfile.GroupPin[] groupPins) {
    }

    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long error) {
    }

    public class MessageContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        public static final int SECTION_CELLS = 300;
        public static final int SECTION_HEADER = 100;
        /* access modifiers changed from: private */
        public Context mContext = null;
        public int mCountProfileMatched = 0;
        /* access modifiers changed from: private */
        public ArrayList<MesiboProfile> mDataList = null;
        /* access modifiers changed from: private */
        public UserListFragment mHost;
        private int mBackground = 0;
        private ArrayList<MesiboProfile> mSearchResults = null;
        private SparseBooleanArray mSelectionItems;
        private ArrayList<MesiboProfile> mUsers = null;

        public MessageContactAdapter(Context context, UserListFragment host, ArrayList<MesiboProfile> list, ArrayList<MesiboProfile> searchResults) {
            this.mContext = context;
            this.mHost = host;
            this.mUsers = list;
            this.mSearchResults = searchResults;
            this.mDataList = list;
            this.mSelectionItems = new SparseBooleanArray();
        }

        public ArrayList<MesiboProfile> getActiveUserlist() {
            if (UserListFragment.this.mIsMessageSearching.booleanValue()) {
                return this.mSearchResults;
            }
            return this.mUsers;
        }

        public int getItemViewType(int position) {
            if (this.mDataList.get(position).address != null || this.mDataList.get(position).groupid > 0) {
                return SECTION_CELLS;
            }
            return 100;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 100) {
                return new SectionHeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_header_title, parent, false));
            }
            return new SectionCellsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder vh, @SuppressLint({"RecyclerView"}) int position) {
            int imageDrawableId;
            if (vh.getItemViewType() == 100) {
                ((SectionHeaderViewHolder) vh).mSectionTitle.setText(this.mDataList.get(position).getName());
                return;
            }
            int i = position;
            MesiboProfile user = this.mDataList.get(position);
            final SectionCellsViewHolder holder = (SectionCellsViewHolder) vh;
            holder.position = position;
            UserData userdata = UserData.getUserData(user);
            userdata.setUser(user);
            userdata.setUserListPosition(position);
            UserData data = userdata;
            holder.mContactsName.setText(data.getUserName());
            if (this.mHost.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                holder.mContactsTime.setVisibility(View.VISIBLE);
                holder.mContactsTime.setText(data.getTime());
            } else {
                holder.mContactsTime.setVisibility(View.GONE);
            }
            if (user.isOnline()) {
                holder.isOnlineDot.setVisibility(View.VISIBLE);
            } else {
                holder.isOnlineDot.setVisibility(View.INVISIBLE);
            }
            Drawable imageDrawable = null;
            int padding = 5;
            MesiboMessage msg = data.getMessage();
            if (msg != null && msg.isDeleted()) {
                imageDrawableId = MesiboConfiguration.DELETED_DRAWABLE;
                imageDrawable = MesiboImages.getDeletedMessageDrawable();
            } else if (msg != null && msg.hasImage()) {
                imageDrawableId = MesiboConfiguration.IMAGE_ICON;
            } else if (msg != null && msg.hasVideo()) {
                imageDrawableId = MesiboConfiguration.VIDEO_ICON;
            } else if (msg != null && msg.hasFile()) {
                imageDrawableId = MesiboConfiguration.ATTACHMENT_ICON;
            } else if (msg != null && msg.isMissedCall() && msg.isVideoCall()) {
                imageDrawableId = MesiboConfiguration.MISSED_VIDEOCALL_DRAWABLE;
                imageDrawable = MesiboImages.getMissedCallDrawable(true);
            } else if (msg != null && msg.isMissedCall() && msg.isVoiceCall()) {
                imageDrawableId = MesiboConfiguration.MISSED_VOICECALL_DRAWABLE;
                imageDrawable = MesiboImages.getMissedCallDrawable(false);
            } else if (msg == null || !msg.hasLocation()) {
                imageDrawableId = 0;
                padding = 0;
            } else {
                imageDrawableId = MesiboConfiguration.LOCATION_ICON;
            }
            boolean typing = data.isTyping();
            if (typing) {
                imageDrawableId = 0;
                padding = 0;
            }
            if (this.mHost.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                if (imageDrawable != null) {
                    holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
                } else {
                    holder.mContactsMessage.setCompoundDrawablesWithIntrinsicBounds(imageDrawableId, 0, 0, 0);
                }
                holder.mContactsMessage.setCompoundDrawablePadding(padding);
                if (!typing) {
                    holder.mContactsMessage.setText(userdata.getLastMessage());
                    holder.mContactsMessage.setTextColor(UserListFragment.this.mMesiboUIOptions.mUserListStatusColor);
                } else {
                    MesiboProfile typingProfile = data.getTypingProfile();
                    String typingText = UserListFragment.this.mMesiboUIOptions.typingIndicationTitle;
                    if (typingProfile != null) {
                        typingText = typingProfile.getName() + " is " + UserListFragment.this.mMesiboUIOptions.typingIndicationTitle;
                    }
                    holder.mContactsMessage.setText(typingText);
                    holder.mContactsMessage.setTextColor(UserListFragment.this.mMesiboUIOptions.mUserListTypingIndicationColor);
                }
            } else {
                holder.mContactsMessage.setText(user.getStatus() != null ? user.getStatus() : "");
            }
            Bitmap b = data.getThumbnail(UserListFragment.this.mLetterTileProvider);
            new RoundImageDrawable(b);
            holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));
            if (this.mHost.mSelectionMode != MesiboUserListFragment.MODE_MESSAGELIST || data.getUnreadCount().intValue() <= 0) {
                holder.mNewMesAlert.setVisibility(View.INVISIBLE);
            } else {
                holder.mNewMesAlert.setVisibility(View.VISIBLE);
                holder.mNewMesAlert.setText(String.valueOf(data.getUnreadCount()));
            }
            holder.mContactsDeliveryStatus.setVisibility(View.GONE);
            if (!typing && this.mHost.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                holder.mContactsDeliveryStatus.setVisibility(View.VISIBLE);
                int sts = data.getStatus().intValue();
                if (sts == 19 || sts == 18 || sts == 21 || sts == 32 || data.isDeletedMessage()) {
                    holder.mContactsDeliveryStatus.setVisibility(View.GONE);
                } else {
                    holder.mContactsDeliveryStatus.setImageBitmap(MesiboImages.getStatusImage(sts));
                }
            }
            if (UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                if ((this.mDataList.get(position).uiFlags & 16777216) == 16777216) {
                    holder.mHighlightView.setVisibility(View.VISIBLE);
                    this.mHost.showForwardLayout();
                } else {
                    holder.mHighlightView.setVisibility(View.GONE);
                }
            }
            final MesiboProfile mesiboProfile = user;
            holder.mView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                        if ((mesiboProfile.uiFlags & 16777216) == 16777216) {
                            mesiboProfile.uiFlags &= -16777217;
                        } else {
                            mesiboProfile.uiFlags |= 16777216;
                        }
                        MessageContactAdapter.this.notifyDataSetChanged();
                        if (MessageContactAdapter.this.isForwardContactsSelected().booleanValue()) {
                            MessageContactAdapter.this.mHost.showForwardLayout();
                        } else {
                            MessageContactAdapter.this.mHost.hideForwardLayout();
                        }
                    } else if (mesiboProfile.getName() == null || UserListFragment.this.mMesiboUIOptions.createGroupTitle == null || !mesiboProfile.getName().equals(UserListFragment.this.mMesiboUIOptions.createGroupTitle) || UserListFragment.this.mSelectionMode != MesiboUserListFragment.MODE_SELECTCONTACT) {
                        Context context = v.getContext();
                        if (!UserListFragment.this.onClickUser(mesiboProfile.address, mesiboProfile.groupid, MessageContactAdapter.this.mHost.mForwardId)) {
                            MesiboUIManager.launchMessagingActivity(UserListFragment.this.getActivity(), MessageContactAdapter.this.mHost.mForwardId, mesiboProfile.address, mesiboProfile.groupid);
                            MessageContactAdapter.this.mHost.mForwardId = 0;
                            if (UserListFragment.this.mSelectionMode != MesiboUserListFragment.MODE_MESSAGELIST) {
                                UserListFragment.this.getActivity().finish();
                                return;
                            }
                            return;
                        }
                        MessageContactAdapter.this.mHost.mForwardId = 0;
                    } else {
                        MesiboUIManager.launchContactActivity(UserListFragment.this.getActivity(), 0, MesiboUserListFragment.MODE_SELECTGROUP, 0, false, false, (Bundle) null, "");
                        UserListFragment.this.getActivity().finish();
                    }
                }
            });
            final MesiboProfile mesiboProfile2 = user;
            final int i2 = position;
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @SuppressLint({"RestrictedApi"})
                public boolean onLongClick(View v) {
                    if (!(UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP || (!TextUtils.isEmpty(UserListFragment.this.mMesiboUIOptions.createGroupTitle) && mesiboProfile2.getName().equalsIgnoreCase(UserListFragment.this.mMesiboUIOptions.createGroupTitle)))) {
                        try {
                            MenuBuilder menuBuilder = new MenuBuilder(UserListFragment.this.getActivity());
                            new MenuInflater(UserListFragment.this.getActivity()).inflate(R.menu.selected_contact, menuBuilder);
                            holder.PopupMenu = new MenuPopupHelper(MessageContactAdapter.this.mContext, menuBuilder, holder.mView);
                            holder.PopupMenu.setForceShowIcon(true);
                            menuBuilder.setCallback(new MenuBuilder.Callback() {
                                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                                    if (item.getItemId() != R.id.menu_remove) {
                                        return false;
                                    } else {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                                        builder1.setMessage("Do you really want to delete this chat?,it cannot be recovered");
                                        builder1.setCancelable(true);
                                        builder1.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //put your code that needed to be executed when okay is clicked
                                                        if (MessageContactAdapter.this.mHost.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                                                            Mesibo.deleteMessages(mesiboProfile2.address, mesiboProfile2.groupid, 0);
                                                            UserData userData = (UserData) mesiboProfile2.other;
                                                            MessageContactAdapter.this.mDataList.remove(i2);
                                                            MessageContactAdapter.this.notifyDataSetChanged();
                                                        } else if (MessageContactAdapter.this.mHost.mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT) {
                                                            MessageContactAdapter.this.mDataList.remove(i2);
                                                            MessageContactAdapter.this.notifyDataSetChanged();
                                                        }
                                                        dialog.cancel();
                                                    }
                                                });
                                        builder1.setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                        AlertDialog alert11 = builder1.create();
                                        alert11.show();
                                    }

                                    return true;
                                }

                                public void onMenuModeChange(MenuBuilder menu) {
                                }
                            });
                            holder.PopupMenu.show();
                            holder.PopupMenu.show();
                        } catch (Exception e) {
                        }
                    }
                    return true;
                }
            });
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder instanceof SectionCellsViewHolder) {
                SectionCellsViewHolder sectionCellsViewHolder = (SectionCellsViewHolder) holder;
            }
        }

        public int getItemCount() {
            UserListFragment.this.handleEmptyUserList(this.mDataList.size());
            return this.mDataList.size();
        }

        public void notifyChangeInData() {
            long unused = UserListFragment.this.mUiUpdateTimestamp = Mesibo.getTimestamp();
            this.mDataList = getActiveUserlist();
            notifyDataSetChanged();
        }

        public void onResumeAdapter() {
            this.mSearchResults.clear();
            Boolean unused = UserListFragment.this.mIsMessageSearching = false;
            this.mUsers.clear();
            this.mDataList = this.mUsers;
        }

        public Boolean isForwardContactsSelected() {
            boolean retValue = false;
            Iterator<MesiboProfile> it = this.mDataList.iterator();
            while (it.hasNext()) {
                if ((it.next().uiFlags & 16777216) == 16777216) {
                    retValue = true;
                }
            }
            return retValue;
        }

        public void createNewGroup() {
            UserListFragment.mMemberProfiles.clear();
            Iterator<MesiboProfile> it = this.mDataList.iterator();
            while (it.hasNext()) {
                MesiboProfile d = it.next();
                if ((d.uiFlags & 16777216) == 16777216 && !UserListFragment.mMemberProfiles.contains(d)) {
                    UserListFragment.mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(UserListFragment.this.getActivity(), (Bundle) null);
            UserListFragment.this.getActivity().finish();
        }

        public void modifyGroupDetail() {
            UserListFragment.mMemberProfiles.clear();
            Iterator<MesiboProfile> it = this.mDataList.iterator();
            while (it.hasNext()) {
                MesiboProfile d = it.next();
                if ((d.uiFlags & 16777216) == 16777216 && !UserListFragment.mMemberProfiles.contains(d)) {
                    UserListFragment.mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(UserListFragment.this.getActivity(), UserListFragment.this.mGroupEditBundle);
            UserListFragment.this.getActivity().finish();

        }

        public void forwardMessageToContacts() {
            UserListFragment.mMemberProfiles.clear();
            Iterator<MesiboProfile> it = this.mDataList.iterator();
            while (it.hasNext()) {
                MesiboProfile d = it.next();
                if ((d.uiFlags & 16777216) == 16777216) {
                    d.uiFlags &= -16777217;
                    if (!UserListFragment.mMemberProfiles.contains(d)) {
                        UserListFragment.mMemberProfiles.add(d);
                    }
                }
            }
            if (UserListFragment.mMemberProfiles.size() != 0) {
                for (int i = 0; i < UserListFragment.mMemberProfiles.size(); i++) {
                    MesiboProfile user = UserListFragment.mMemberProfiles.get(i);
                    UserData userData = (UserData) user.other;
                    MesiboMessage msg = user.newMessage();
                    if (UserListFragment.this.mForwardMessageIds != null) {
                        msg.setForwarded(UserListFragment.this.mForwardMessageIds);
                        msg.send();
                    } else if (!TextUtils.isEmpty(UserListFragment.this.mForwardedMessage)) {
                        msg.message = UserListFragment.this.mForwardedMessage;
                        msg.send();
                    }
                }
                if (!UserListFragment.this.mCloseAfterForward && UserListFragment.mMemberProfiles.size() == 1) {
                    MesiboProfile user2 = UserListFragment.mMemberProfiles.get(0);
                    UserData userData2 = (UserData) user2.other;
                    if (!UserListFragment.this.onClickUser(user2.address, user2.groupid, 0)) {
                        MesiboUIManager.launchMessagingActivity(UserListFragment.this.getActivity(), 0, user2.address, user2.groupid);
                    }
                }
                UserListFragment.this.getActivity().finish();
                UserListFragment.this.mForwardId = 0;
            }
        }

        public void filter(String text) {
            String unused = UserListFragment.this.mSearchQuery = text;
            this.mCountProfileMatched = 0;
            this.mSearchResults.clear();
            Boolean unused2 = UserListFragment.this.mIsMessageSearching = false;
            if (TextUtils.isEmpty(text)) {
                this.mDataList = this.mUsers;
                return;
            }
            this.mDataList = this.mSearchResults;
            String text2 = text.toLowerCase();
            Iterator<MesiboProfile> it = this.mUsers.iterator();
            while (it.hasNext()) {
                MesiboProfile item = it.next();
                if (item.getName().toLowerCase().contains(text2) || item.getName().equals(UserListFragment.this.mMesiboUIOptions.allUsersTitle) || item.getName().equals(UserListFragment.this.mMesiboUIOptions.recentUsersTitle) || item.getName().equals(UserListFragment.this.mMesiboUIOptions.groupMembersTitle)) {
                    this.mSearchResults.add(item);
                }
            }
            if (this.mSearchResults.size() > 0 && UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                MesiboProfile tempUserProfile = new MesiboProfile();
                this.mCountProfileMatched = this.mSearchResults.size();
                tempUserProfile.setName(String.valueOf(this.mSearchResults.size()) + " " + MesiboConfiguration.USERS_STRING_USERLIST_SEARCH);
                this.mSearchResults.add(0, tempUserProfile);
            }
            this.mDataList = this.mSearchResults;
            UserListFragment.this.setEmptyViewText();
            if (UserListFragment.this.mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                UserListFragment.this.mEmptyView.setText(MesiboUI.getConfig().emptySearchListMessage);
                if (!TextUtils.isEmpty(text2)) {
                    Boolean unused3 = UserListFragment.this.mIsMessageSearching = true;
                    MesiboReadSession rbd = new MesiboReadSession(UserListFragment.this);
                    rbd.setQuery(text2);
                    rbd.read(100);
                }
            }
        }

        public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
            public TextView mSectionTitle = null;

            public SectionHeaderViewHolder(View itemView) {
                super(itemView);
                this.mSectionTitle = (TextView) itemView.findViewById(R.id.section_header);
            }
        }

        public class SectionCellsViewHolder extends RecyclerView.ViewHolder {
            public MenuPopupHelper PopupMenu = null;
            public String mBoundString = null;
            public ImageView mContactsDeliveryStatus = null;
            public EmojiconTextView mContactsMessage = null;
            public TextView mContactsName = null;
            public ImageView mContactsProfile = null;
            public TextView mContactsTime = null;
            public RelativeLayout mHighlightView = null;
            public TextView mNewMesAlert = null;
            public View mView = null;
            public int position = 0;
            public ImageView isOnlineDot;

            public SectionCellsViewHolder(View view) {
                super(view);
                this.mView = view;
                this.mContactsProfile = (ImageView) view.findViewById(R.id.mes_rv_profile);
                this.mContactsName = (TextView) view.findViewById(R.id.mes_rv_name);
                this.mContactsTime = (TextView) view.findViewById(R.id.mes_rv_date);
                this.mContactsMessage = view.findViewById(R.id.mes_cont_post_or_details);
                this.mContactsDeliveryStatus = (ImageView) view.findViewById(R.id.mes_cont_status);
                this.isOnlineDot = (ImageView) view.findViewById(R.id.isOnlineDot);
                this.mNewMesAlert = (TextView) view.findViewById(R.id.mes_alert);
                this.mHighlightView = (RelativeLayout) view.findViewById(R.id.highlighted_view);
            }
        }
    }
}
