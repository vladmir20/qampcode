package com.qamp.app.Activity;

import static com.qamp.app.MessagingModule.MesiboConfiguration.JOIN_HUDDLE_MESSAGE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;
import static com.qamp.app.MessagingModule.MesiboConfiguration.USERS_STRING_USERLIST_SEARCH;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboGroupProfile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.MessagingModule.Contact;
import com.qamp.app.MessagingModule.CreateNewGroupActivity;
import com.qamp.app.MessagingModule.LetterTitleProvider;
import com.qamp.app.MessagingModule.MesiboMessagingActivity;
import com.qamp.app.MessagingModule.MesiboUI;
import com.qamp.app.MessagingModule.MesiboUIManager;
import com.qamp.app.MessagingModule.MesiboUserListFragment;
import com.qamp.app.MessagingModule.RoundImageDrawable;
import com.qamp.app.MessagingModule.UserData;
import com.qamp.app.MessagingModule.UserListFragment;
import com.qamp.app.MessagingModule.Utils;
import com.qamp.app.Modal.ContactModel;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.QampConstants;
import com.qamp.app.qampcallss.api.MesiboCall;
import com.qamp.app.qampcallss.api.p000ui.MesiboDefaultCallActivity;

import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

public class QampContactScreen extends AppCompatActivity implements Mesibo.MessageListener, Mesibo.ConnectionListener,
        Mesibo.ProfileListener, Mesibo.SyncListener, Mesibo.GroupListener, Observer, View.OnClickListener {

    ArrayList<Contact> contactList = new ArrayList<>();
    ImageView backPress;
    private String mSearchQuery = null;
    private Boolean mIsMessageSearching = false;
    ImageView searchIcon;
    EditText searchQuery;
    TextView mEmptyView;
    TextView contactsCount;
    TextView createGroup;
    LinearLayout next_group;
    RecyclerView contactsRecycle;
    private WeakReference<MesiboUserListFragment.FragmentListener> mListener = null;
    private int mSelectionMode = 0;
    private ArrayList<MesiboProfile> mUserProfiles = null;
    private ArrayList<ContactModel> contactUserProfile = null;
    private ArrayList<MesiboProfile> mSearchResultList = null;
    public static ArrayList<MesiboProfile> slectedgtoup = new ArrayList<>();
    public static final String PERMISSION_DENIED_CONTACTS = "Permission denied to read your Contacts";
    public static final String MESSAGE_DELETED_STRING = "This message was deleted";
    public static final String MISSED_VIDEO_CALL = "Missed video call";
    public static final String MISSED_VOICE_CALL = "Missed voice call";
    public static final String FREQUENT_USERS_STRING = "Recent Users";
    public static int groupmaker = 0;
    private long mUiUpdateTimestamp = 0;
    private boolean isVisible = false;
    private boolean isHidden = false;
    LetterTitleProvider mLetterTileProvider = null;
    public long mForwardId = 0;
    MesiboUI.Config mMesiboUIOptions = null;

    MessageContactAdapter mAdapter = null;



    public static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private TextWatcher searchTextWatcher = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
            mSearchQuery = s.toString();
            if (mSearchQuery.isEmpty()) {
                mIsMessageSearching = false;
                mAdapter.notifyChangeInData();
                // Needed when press close then back it goes back so..
                mIsMessageSearching = true;
            } else {
                mIsMessageSearching = true;
                if (!TextUtils.isEmpty(mSearchQuery)) {
                    mAdapter.filter(mSearchQuery);
                }

                mAdapter.notifyChangeInData();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qamp_contact_screen);
        AppUtils.setStatusBarColor(QampContactScreen.this, R.color.colorAccent);

        backPress = findViewById(R.id.imageView53);
        searchIcon = findViewById(R.id.imageView3);
        searchQuery = findViewById(R.id.editTextTextPersonName5);
        contactsRecycle = findViewById(R.id.contacts);
        createGroup = findViewById(R.id.create_group);
        next_group = findViewById(R.id.next_group);
        mEmptyView = findViewById(R.id.emptyview_tex);
        mMesiboUIOptions = MesiboUI.getConfig();
        mSelectionMode = MesiboUserListFragment.MODE_MESSAGELIST;
        contactsCount = findViewById(R.id.textView66);


        checkPermissionAndFetchContacts();
        Bundle b = getIntent().getExtras();
        //Bundle b = this.getArguments();//getIntent().getBundleExtra(BUNDLE_CONTACT_DETAILS);
        if (null != b) {
            mSelectionMode = b.getInt(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_MESSAGELIST);
            //mReadQuery = b.getString("query", null);
        }
        mUserProfiles = new ArrayList<>();
        mUserProfiles = new ArrayList<MesiboProfile>();
        contactUserProfile = new ArrayList<ContactModel>();
        mSearchResultList = new ArrayList<MesiboProfile>();
        OpenContactRecycler(false);

        if (mMesiboUIOptions.useLetterTitleImage) {
            mLetterTileProvider = new LetterTitleProvider(getApplicationContext(), 60, mMesiboUIOptions.mLetterTitleColors);
        }


        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchQuery.getText().toString().isEmpty()) {
                    searchIcon.setImageDrawable(getResources().getDrawable(R.mipmap.search_box));


                }
                searchQuery.requestFocus();

                InputMethodManager inputMethodManager =
                        (InputMethodManager) QampContactScreen.this.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        searchIcon.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);

                searchQuery.setText("");
            }
        });


        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mSearchQuery = editable.toString();
                if (mSearchQuery.isEmpty()) {
                    mIsMessageSearching = false;
                    mAdapter.notifyChangeInData();
                    // Needed when press close then back it goes back so..
                    mIsMessageSearching = true;
                    searchIcon.setImageDrawable(getResources().getDrawable(R.mipmap.search_box));
                    searchQuery.clearFocus();
                } else {
                    mIsMessageSearching = true;
                    if (!TextUtils.isEmpty(mSearchQuery)) {
                        mAdapter.filter(mSearchQuery);
                    }
                    searchIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_close));

                    mAdapter.notifyChangeInData();
                }
            }
        });

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupmaker == 0) {
                    groupmaker = 1;
                    next_group.setVisibility(View.VISIBLE);
                    //Context contactsBottomSheetFragment = getApplicationContext();
                    OpenContactRecycler(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        createGroup.setBackgroundColor(QampContactScreen.this.getColor(R.color.colorPrimary));
                    }
                } else if (groupmaker == 1) {
                    groupmaker = 0;
                    next_group.setVisibility(View.GONE);
                    OpenContactRecycler(false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        createGroup.setBackgroundColor(QampContactScreen.this.getColor(R.color.white));
                    }
                }
            }
        });

        next_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserListFragment.mMemberProfiles.clear();
                if (slectedgtoup.size() >= 2) {
                    groupmaker = 1;
                    Iterator<MesiboProfile> it = slectedgtoup.iterator();
                    while (it.hasNext()) {
                        MesiboProfile d = it.next();
                        UserListFragment.mMemberProfiles.add(d);
                    }
                    //  MesiboUIManager.launchGroupActivity(ContactsBottomSheetFragment.this.getActivity(), (Bundle) null);
                    Intent intent = new Intent(QampContactScreen.this, CreateNewGroupActivity.class);
                    startActivity(intent);

                } else if (slectedgtoup.size() == 0 || slectedgtoup.size() == 1) {
                    Toast.makeText(getApplicationContext(), "Please Select atleast two members to create group", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void OpenContactRecycler(boolean isCreatingGroup) {

        /**if(mUserProfiles.size()>0){
            for(int i = 0;i<mUserProfiles.size();i++){
            //UserData data = UserData.getUserData(mUserProfiles.get(i));
            ContactModel contactModel=new ContactModel();
            if (null != mUserProfiles.get(i).getStatus() && mUserProfiles.get(i).getStatus().equals("0")){

                contactModel.setUserData(mUserProfiles.get(i));
                contactModel.setViewValue(View.VISIBLE);
                contactModel.setViewValueNot(View.GONE);
                contactModel.setIsGroup(View.GONE);
                contactUserProfile.add(contactModel);
            }else if (mUserProfiles.get(i).getAddress() != "" && mUserProfiles.get(i).isGroup() == true){
                contactModel.setUserData(mUserProfiles.get(i));
                contactModel.setViewValue(View.GONE);
                contactModel.setViewValueNot(View.VISIBLE);
                contactModel.setIsGroup(View.VISIBLE);
                contactUserProfile.add(contactModel);
            }else if (null != mUserProfiles.get(i).getStatus() && !mUserProfiles.get(i).getStatus().equals("0")){
                contactModel.setUserData(mUserProfiles.get(i));
                contactModel.setViewValue(View.GONE);
                contactModel.setViewValueNot(View.VISIBLE);
                contactModel.setIsGroup(View.GONE);
                contactUserProfile.add(contactModel);
            }else{
                contactModel.setUserData(mUserProfiles.get(i));
                contactModel.setViewValue(View.GONE);
                contactModel.setViewValueNot(View.GONE);
                contactModel.setIsGroup(View.GONE);
                contactUserProfile.add(contactModel);
            }


        }}*/
        contactsRecycle.setLayoutManager(new LinearLayoutManager(contactsRecycle.getContext()));
        mAdapter = new MessageContactAdapter(getApplicationContext(), QampContactScreen.this, mUserProfiles, mSearchResultList, isCreatingGroup);
        //Log.e("before Contats",new Gson().toJson(mUserProfiles));
        //mAdapter.setHasStableIds(true);
        contactsRecycle.setAdapter(mAdapter);
        contactsRecycle.setNestedScrollingEnabled(false);
        //mAdapter.notifyChangeInData();
        mAdapter.notifyDataSetChanged();
        contactsRecycle.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mesiboProfileArrayList.clear();
        mUserProfiles.clear();

        showUserList(MESIBO_INTITIAL_READ_USERLIST);
        Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());
        Utils.showServicesSuspendedAlert(getApplicationContext());
        OpenContactRecycler(false);
        //mAdapter.notifyChangeInData();
        //contactsRecycle.invalidate();

    }

    private void checkPermissionAndFetchContacts() {
        if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) == PermissionChecker.PERMISSION_GRANTED) {
            this.getContactList();
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to sync existing Huddle Contacts and to show all contact list. If denied you can change permission from Settings.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(QampContactScreen.this, new String[]{Manifest.permission.READ_CONTACTS}, 225);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            if (PermissionChecker.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(QampContactScreen.this, new String[]{Manifest.permission.READ_CONTACTS}, 225);
            }
        }
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





    public void getContactList() {
        ContentResolver cr = getApplicationContext().getContentResolver();
        contactList.removeAll(contactList);

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    number = number.replace("(", "");
                    number = number.replace(")", "");
                    number = number.replace("-", "");
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(new Contact(name, number));
                        mobileNoSet.add(number);
                        Log.d("hvy", "onCreateView  Phone Number: name = " + name
                                + " No = " + number);
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }

    public void showUserList(int readCount) {
        Log.d("showUserList", "showUserList");
        mUserProfiles.clear();
        setEmptyViewText();
        if (readCount == 0) {
        } else {
            mUserProfiles.clear();

            ArrayList<MesiboProfile> otherContactsList = new ArrayList<>();

            for (int j = 0; j < contactList.size(); j++) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Boolean state = true;
                try {
                    // phone must begin with '+'
                    Phonenumber.PhoneNumber numberProto = phoneUtil.parse(contactList.get(j).phoneNumber, "");
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                    state = false;
                }

                MesiboProfile userProfile = new MesiboProfile();
                userProfile.address = state == false ? "91" + contactList.get(j).phoneNumber : contactList.get(j).phoneNumber.substring(1);
                userProfile.setName(contactList.get(j).name);
                userProfile.setStatus("0");
                userProfile.draft = null;
                // userProfile.unread = 0;
                userProfile.groupid = 0;
                //userProfile.lastActiveTime = 0;
                userProfile.lookedup = false;
                userProfile.other = null;
                otherContactsList.add(userProfile);
            }
            contactsCount.setText(otherContactsList.size()+"contacts");

            for (int j = 0; j < Mesibo.getSortedUserProfiles().size(); j++) {
                for (int i = 0; i < otherContactsList.size(); i++) {
                    if (null != otherContactsList.get(i).address && otherContactsList.get(i).
                            address.equals(Mesibo.getSortedUserProfiles().get(j).address)) {
                        Mesibo.getSortedUserProfiles().get(j).setName(otherContactsList.get(i).getName());
                        otherContactsList.remove(i);
                        mUserProfiles.add(Mesibo.getSortedUserProfiles().get(j));
                        break;
                    }
                }
            }
            //Log.e("getSortedUserProfile",new Gson().toJson(Mesibo.getSortedUserProfiles()));
            //mUserProfiles.addAll(Mesibo.getSortedUserProfiles());

            //Log.e("own Contacts Only",new Gson().toJson(mUserProfiles));
            mUserProfiles.addAll(otherContactsList);

            MesiboProfile selfProfile = Mesibo.getSelfProfile();
            for (int i = mUserProfiles.size() - 1; i >= 0; i--) {
                MesiboProfile user = mUserProfiles.get(i);
                //if(null!= mUserProfiles.get(i)&& mUserProfiles.get(i).isGroup()){
                   // mUserProfiles.remove(i);
                //}
                //UserData userdata = UserData.getUserData(user);
                //contactUserProfile.add()
                if (selfProfile.address.equals(mUserProfiles.get(i).address)) {
                    mUserProfiles.remove(i);
                }
            }
            for (int i = mUserProfiles.size() - 1; i >= 0; i--) {
                MesiboProfile user = mUserProfiles.get(i);
                if(null!= mUserProfiles.get(i)&& mUserProfiles.get(i).isGroup()){
                    mUserProfiles.remove(i);
                }
            }
            otherContactsList.clear();
        }
        if (mUserProfiles.size() == 0) {
            //subtitle.setText("");
        } else if (mUserProfiles.size() == 1) {
            //subtitle.setText(String.valueOf(mUserProfiles.size() + " Contact"));
        } else {
            //subtitle.setText(String.valueOf(mUserProfiles.size() + " Contacts"));
        }

        mAdapter.notifyChangeInData();
    }


    public void setEmptyViewText() {
        if (mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST)
            mEmptyView.setText(getString(R.string.not_have_chat));
        else
            mEmptyView.setText(getResources().getString(R.string.no_contacts_found));
    }

    public boolean onClickUser(String address, long groupid, long forwardid) {
        MesiboUserListFragment.FragmentListener l = getListener();
        if (null == l)
            return false;

        return l.Mesibo_onClickUser(address, groupid, forwardid);
    }

    public MesiboUserListFragment.FragmentListener getListener() {
        if (null == mListener)
            return null;

        return mListener.get();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void Mesibo_onConnectionStatus(int i) {

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
    public void Mesibo_onGroupError(MesiboProfile mesiboProfile, long l) {

    }

    @Override
    public void Mesibo_onMessage(@NotNull MesiboMessage mesiboMessage) {

    }

    @Override
    public void Mesibo_onMessageStatus(@NotNull MesiboMessage mesiboMessage) {

    }

    @Override
    public void Mesibo_onMessageUpdate(@NotNull MesiboMessage mesiboMessage) {

    }

    @Override
    public void Mesibo_onProfileUpdated(MesiboProfile mesiboProfile) {
        if (null != mesiboProfile && null == mesiboProfile.other)
            return;

        if (Mesibo.isUiThread()) {
            updateContacts(mesiboProfile);
            return;
        }

        final MesiboProfile profile = mesiboProfile;
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                updateContacts(profile);
            }
        });
    }

    @Override
    public boolean Mesibo_onGetProfile(MesiboProfile mesiboProfile) {
        return false;
    }

    @Override
    public void Mesibo_onSync(int i) {
        final int c = i;
        if (i > 0) {
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    showUserList(MESIBO_INTITIAL_READ_USERLIST);
                }
            });

        }
    }



    @Override
    public void update(Observable observable, Object o) {

    }

    public class MessageContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Mesibo.ConnectionListener, MesiboCall.IncomingListener {
        public final static int SECTION_HEADER = 100;
        public final static int SECTION_CELLS = 300;
        public int mCountProfileMatched = 0;
        private int mBackground = 0;
        private Context mContext = null;
        private ArrayList<MesiboProfile> mDataList = null;
        private ArrayList<MesiboProfile> mUsers = null;
        private ArrayList<MesiboProfile> mSearchResults = null;
        //private ArrayList<ContactModel> mMainUserList = null;
        private QampContactScreen mHost;
        private SparseBooleanArray mSelectionItems;
        private boolean isGroup;


        public MessageContactAdapter(Context context, QampContactScreen host,
                                     ArrayList<MesiboProfile> list,
                                     ArrayList<MesiboProfile> searchResults, boolean isGroup) {
            //Log.e("Contact Lists", new Gson().toJson(list));
            this.mContext = context;
            mHost = host;
            mUsers = list;
            mSearchResults = searchResults;
            mDataList = list;
            //mMainUserList = contactUserProfile;
            mSelectionItems = new SparseBooleanArray();
            this.isGroup = isGroup;
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
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == SECTION_HEADER) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_header_title, parent, false);
                //return new ContactsBottomSheetFragment.MessageContactAdapter.SectionHeaderViewHolder(v);
                return new SectionHeaderViewHolder(v);

            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.contact_new_user_list, parent, false);
                return new SectionCellsViewHolder(view);
            }
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, @SuppressLint("RecyclerView") final int position) {


            if (vh.getItemViewType() == SECTION_HEADER) {
                ((MessageContactAdapter.SectionHeaderViewHolder) vh).mSectionTitle.setText(mDataList.get(position).getName());
            }

            else {

                final int pos = position;
                MesiboProfile user = mDataList.get(position);
                //final MesiboProfile userMain = mMainUserList.get(position).getUserData();

                MessageContactAdapter.SectionCellsViewHolder holder = (MessageContactAdapter.SectionCellsViewHolder) vh;
                //setHasStableIds(true);
                holder.link.setVisibility(View.GONE);
                holder.messageIcon.setVisibility(View.GONE);
                holder.audioCallIcon.setVisibility(View.GONE);
                holder.videoCallIcon.setVisibility(View.GONE);
                holder.isChecked.setVisibility(View.GONE);
                holder.position = position;
                //isHidden=false;
                //isVisible=false;
                //holder.messageIcon.setVisibility(View.GONE);
                //holder.link.setVisibility(View.GONE);
                //holder.link.setVisibility(View.GONE);
                //holder.messageIcon.setVisibility(View.GONE);
                //holder.audioCallIcon.setVisibility(View.GONE);
                //holder.videoCallIcon.setVisibility(View.GONE);
                //holder.isChecked.setVisibility(View.GONE);

                UserData userdata = UserData.getUserData(user);
                //userdata.setUser(user); // in case user is changed dynamically
                //userdata.setUserListPosition(position);

                UserData data = userdata;
                holder.mContactsName.setText(data.getUserName());
                String number = data.getPeer();
                holder.mContactsPhone.setText(AppUtils.getFormatedNumber(number));//(PhoneNumberUtils.formatNumber(number));


                /**int imageDrawableId = 0;
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
\                    padding = 0;
                }*/
                /**int view = mMainUserList.get(position).getViewValue();
                int viewNot =mMainUserList.get(position).getViewValueNot();
                int isGroupValue =mMainUserList.get(position).getIsGroup();
                holder.link.setVisibility(view);
                holder.messageIcon.setVisibility(viewNot);
                holder.audioCallIcon.setVisibility(viewNot);
                holder.videoCallIcon.setVisibility(viewNot);
                holder.isChecked.setVisibility(isGroupValue);*/

                if (null != user.getStatus() && user.getStatus().equals("0")) {

                    holder.link.setVisibility(View.VISIBLE);
                    holder.messageIcon.setVisibility(View.GONE);
                    holder.audioCallIcon.setVisibility(View.GONE);
                    holder.videoCallIcon.setVisibility(View.GONE);
                    holder.isChecked.setVisibility(View.GONE);

                } else if (user.getAddress() != "" && isGroup == true) {
                    holder.link.setVisibility(View.GONE);
                    holder.messageIcon.setVisibility(View.VISIBLE);
                    holder.audioCallIcon.setVisibility(View.VISIBLE);
                    holder.videoCallIcon.setVisibility(View.VISIBLE);
                    holder.isChecked.setVisibility(View.VISIBLE);}
                 else if(null != user.getStatus() && !user.getStatus().equals("0")) {
                    holder.link.setVisibility(View.GONE);
                    holder.messageIcon.setVisibility(View.VISIBLE);
                    holder.audioCallIcon.setVisibility(View.VISIBLE);
                    holder.videoCallIcon.setVisibility(View.VISIBLE);
                    holder.isChecked.setVisibility(View.GONE);
                }else{
                    holder.link.setVisibility(View.GONE);
                    holder.messageIcon.setVisibility(View.GONE);
                    holder.audioCallIcon.setVisibility(View.GONE);
                    holder.videoCallIcon.setVisibility(View.GONE);
                    holder.isChecked.setVisibility(View.GONE);
                }


                Bitmap b = data.getThumbnail(mLetterTileProvider);
                Drawable d = new com.mesibo.api.RoundImageDrawable(b);
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(b));

                if (mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                    if ((mDataList.get(position).uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                        holder.mHighlightView.setVisibility(View.VISIBLE);
                        //mHost.showForwardLayout();
                    } else {
                        holder.mHighlightView.setVisibility(View.GONE);
                    }
                }

                if (groupmaker == 0) {
//                    holder.invite.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (null != user.getStatus() && user.getStatus().equals("0")) {
//                                sendMessage(JOIN_HUDDLE_MESSAGE, user.address);
//                            }
//                        }
//                    });

                    holder.link.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String link = "https://play.google.com/store/apps/details?id=com.qamp.app";

                            Uri uri = Uri.parse(link);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_TEXT, link.toString());
                            intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.shareAppText));

                            startActivity(Intent.createChooser(intent, "Share Link"));
                        }
                    });


                    holder.mContactsName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (null != user.getStatus() && user.getStatus().equals("0")) {
                                sendMessage(JOIN_HUDDLE_MESSAGE, user.address);
                            } else {
                                if (mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                                    if ((user.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                                        user.uiFlags = user.uiFlags & ~MesiboProfile.FLAG_MARKED;
                                    } else {
                                        user.uiFlags = user.uiFlags | MesiboProfile.FLAG_MARKED;
                                    }
                                    notifyDataSetChanged();

                                    if (isForwardContactsSelected()) {
                                        //mHost.showForwardLayout();
                                    } else {
                                        //mHost.hideForwardLayout();
                                    }

                                } else {

                                    //TBD, it's checking user name, instead we should set flag
                                    if (null != user.getName() && null != getString(R.string.create_new_group) && user.getName().equals(getString(R.string.create_new_group)) && mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT) {
                                        MesiboUIManager.launchContactActivity(getApplicationContext(), 0, MesiboUserListFragment.MODE_SELECTGROUP, 0, false, false, null, "");
                                        QampContactScreen.this.finish();
                                        return;
                                    }

                                    // data.clearUnreadCount();
                                    Context context = view.getContext();

                                    boolean handledByApp = onClickUser(user.address, user.groupid, mHost.mForwardId);

                                    if (!handledByApp) {
                                        Intent intent = new Intent(QampContactScreen.this, MesiboMessagingActivity.class);
                                        intent.putExtra(MesiboUI.MESSAGE_ID, mHost.mForwardId);
                                        intent.putExtra(MesiboUI.PEER, user.address);
                                        intent.putExtra(MesiboUI.GROUP_ID, user.groupid);
                                        startActivity(intent);
                                        mHost.mForwardId = 0;
                                        if (mSelectionMode != MesiboUserListFragment.MODE_MESSAGELIST) {
                                            QampContactScreen.this.finish();
                                        }
                                    } else {
                                        mHost.mForwardId = 0;
                                    }
                                }
                            }
                        }
                    });


                    holder.messageIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != user.getStatus() && user.getStatus().equals("0")) {
                                sendMessage(JOIN_HUDDLE_MESSAGE, user.address);
                            } else {
                                if (mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP) {
                                    if ((user.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                                        user.uiFlags = user.uiFlags & ~MesiboProfile.FLAG_MARKED;
                                    } else {
                                        user.uiFlags = user.uiFlags | MesiboProfile.FLAG_MARKED;
                                    }
                                    notifyDataSetChanged();

                                    if (isForwardContactsSelected()) {
                                        //mHost.showForwardLayout();
                                    } else {
                                        //mHost.hideForwardLayout();
                                    }

                                } else {

                                    //TBD, it's checking user name, instead we should set flag
                                    if (null != user.getName() && null != getString(R.string.create_new_group) && user.getName().equals(getString(R.string.create_new_group)) && mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT) {
                                        MesiboUIManager.launchContactActivity(getApplicationContext(), 0, MesiboUserListFragment.MODE_SELECTGROUP, 0, false, false, null, "");
                                        QampContactScreen.this.finish();
                                        return;
                                    }

                                    // data.clearUnreadCount();
                                    Context context = v.getContext();

                                    boolean handledByApp = onClickUser(user.address, user.groupid, mHost.mForwardId);

                                    if (!handledByApp) {
                                        Intent intent = new Intent(QampContactScreen.this, MesiboMessagingActivity.class);
                                        intent.putExtra(MesiboUI.MESSAGE_ID, mHost.mForwardId);
                                        intent.putExtra(MesiboUI.PEER, user.address);
                                        intent.putExtra(MesiboUI.GROUP_ID, user.groupid);
                                        startActivity(intent);
                                        mHost.mForwardId = 0;
                                        if (mSelectionMode != MesiboUserListFragment.MODE_MESSAGELIST) {
                                            QampContactScreen.this.finish();
                                        }
                                    } else {
                                        mHost.mForwardId = 0;
                                    }
                                }
                            }
                        }
                    });

                    holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (mSelectionMode == MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD || mSelectionMode == MesiboUserListFragment.MODE_SELECTGROUP || mSelectionMode == MesiboUserListFragment.MODE_EDITGROUP)
                                return true;

                            if (!TextUtils.isEmpty(getString(R.string.create_new_group)) && user.getName().equalsIgnoreCase(getString(R.string.create_new_group)))
                                return true;

                            return true;
                        }
                    });
                    String destination = "destination";
                    //MesiboProfile mUserr = Mesibo.getProfile("peer");
                    //MesiboProfile mUserr = user;
                    //mesibo mesibo

                    String peer = user.address;
                    long groupId = user.groupid;
                    MesiboProfile mUserr;
                    if (groupId > 0) {
                        mUserr = Mesibo.getProfile(groupId);
                    } else {
                        mUserr = Mesibo.getProfile(peer);
                    }


                    holder.audioCallIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!MesiboCall.getInstance().callUi(getApplicationContext(), mUserr, false))
                                MesiboCall.getInstance().callUiForExistingCall(getApplicationContext());
//                            Toast.makeText(mContext, "Call Function", Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.videoCallIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!MesiboCall.getInstance().callUi(getApplicationContext(), mUserr, true))
                                //MesiboCall.getInstance().callUiForExistingCall(getApplicationContext());
                                launchCustomCallActivity(destination, true, false);//                            Toast.makeText(mContext, "Video Call Function", Toast.LENGTH_SHORT).show();

                        }
                    });

                } else if (groupmaker == 1) {
                    holder.isChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if (b) {
                                slectedgtoup.add(mDataList.get(position));
                            } else if (!b) {
                                slectedgtoup.remove(mDataList.get(position));
                            }
                        }
                    });
                }

            }
// Toast.makeText(getActivity(), ""+Mesibo.getUserProfiles().size(), Toast.LENGTH_SHORT).show();
// Toast.makeText(getActivity(), ""+Mesibo.getSortedUserProfiles().size(), Toast.LENGTH_LONG).show();
            //         Log.e("Latest /contacts",new Gson().toJson(Mesibo.getUserProfiles()));
            //Log.e("Latest /contacts/toString",new Gson().toJson(Mesibo.getUserProfiles().toString()));
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (!(holder instanceof MessageContactAdapter.SectionCellsViewHolder)) {
                return;
            }

            MessageContactAdapter.SectionCellsViewHolder vh = (MessageContactAdapter.SectionCellsViewHolder) holder;
            //vh.clearData();
        }

        @Override
        public int getItemCount() {
            handleEmptyUserList(mDataList.size());
            return mDataList.size();
        }

        public void sendMessage(String message, String contact) {
            String url = null;
            try {
                url = QampConstants.whatsappURL + contact + "&text=" + URLEncoder.encode(message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }

        public void handleEmptyUserList(int userListsize) {
            if (userListsize == 0) {
                contactsRecycle.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                contactsRecycle.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
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
            //mDataList.clear();
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

        public void filter(String text) {
            mSearchQuery = text;
            mCountProfileMatched = 0;
            mSearchResults.clear();
//            mIsMessageSearching = false;
            if (TextUtils.isEmpty(text)) {
                mDataList = mUsers;
            } else {
                mDataList = mSearchResults;
                text = text.toLowerCase();

                for (MesiboProfile item : mUsers) {
                    if (item.getName().toLowerCase().contains(text) || item.getName().equals(QampContactScreen.this.getResources().getString(R.string.all_users)) ||
                            item.getName().equals(FREQUENT_USERS_STRING) ||
                            item.getName().equals(QampContactScreen.this.getResources().getString(R.string.group_members))) {
                        mSearchResults.add(item);
                    }
                    if (item.getAddress().toLowerCase().contains(text) ||
                            item.getAddress().equals(QampContactScreen.this.getResources().getString(R.string.all_users)) ||
                            item.getAddress().equals(FREQUENT_USERS_STRING) ||
                            item.getAddress().equals(QampContactScreen.this.getResources().getString(R.string.group_members))) {
                        mSearchResults.add(item);
                    }
                }
                if (mSearchResults.size() > 0 && mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {

                    MesiboProfile tempUserProfile = new MesiboProfile();
                    mCountProfileMatched = mSearchResults.size();
                    tempUserProfile.setName(String.valueOf(mSearchResults.size()) + " " + USERS_STRING_USERLIST_SEARCH);
                    mSearchResults.add(0, tempUserProfile);
                }

                mDataList = mSearchResults;
                setEmptyViewText();

                if (mSelectionMode == MesiboUserListFragment.MODE_MESSAGELIST) {
                    mEmptyView.setText(getResources().getString(R.string.no_result_found));
                    if (!TextUtils.isEmpty(text)) {
                        mIsMessageSearching = true;
//                        Mesibo.ReadDbSession rbd = new Mesibo.ReadDbSession(null, 0, text, (Mesibo.MessageListener) this);
//                        rbd.read(MESIBO_SEARCH_READ_USERLIST);
                    }
                }
            }
        }

        @Override
        public void Mesibo_onConnectionStatus(int i) {
            Log.d("Qamp", "Connection status: " + i);
        }

        @Override
        public void MesiboCall_OnError(MesiboCall.CallProperties callProperties, int i) {

        }

        @Override
        public MesiboCall.CallProperties MesiboCall_OnIncoming(MesiboProfile mesiboProfile, boolean z) {
            MesiboCall.CallProperties cc = MesiboCall.getInstance().createCallProperties(z);
            cc.parent = getApplicationContext();
            cc.user = mesiboProfile;
            cc.className = MesiboDefaultCallActivity.class;
            return cc;
        }

        @Override
        public boolean MesiboCall_OnShowUserInterface(MesiboCall.Call call, MesiboCall.CallProperties callProperties) {
            launchCustomCallActivity(callProperties.user.address, callProperties.video.enabled, true);
            return true;
        }

        protected void launchCustomCallActivity(String destination, boolean video, boolean incoming) {
            Intent intent = new Intent(getApplicationContext(), MesiboDefaultCallActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("video", video);
            intent.putExtra("address", destination);
            intent.putExtra("incoming", incoming);
            startActivity(intent);
        }

        @Override
        public boolean MesiboCall_onNotify(int i, MesiboProfile mesiboProfile, boolean z) {
            return false;
        }

        public class SectionHeaderViewHolder extends RecyclerView.ViewHolder {
            public TextView mSectionTitle = null;

            public SectionHeaderViewHolder(View itemView) {
                super(itemView);
                mSectionTitle = (TextView) itemView.findViewById(R.id.section_header);
            }
        }

        /*public void createNewGroup (){
            mMemberProfiles.clear();
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED )==MesiboProfile.FLAG_MARKED) {
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(ContactList.this, null);
            ContactList.this.finish();
            return ;
        }*/

        /*public void modifyGroupDetail (){
            mMemberProfiles.clear();
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED )==MesiboProfile.FLAG_MARKED) {
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }
            MesiboUIManager.launchGroupActivity(ContactList.this, mGroupEditBundle);
            ContactList.this.finish();
            return ;
        }*/

        /*public void forwardMessageToContacts () {
            mMemberProfiles.clear();
            int i = 0;
            for(MesiboProfile d : mDataList ) {
                if((d.uiFlags & MesiboProfile.FLAG_MARKED) == MesiboProfile.FLAG_MARKED) {
                    d.uiFlags &= ~MesiboProfile.FLAG_MARKED;
                    if(!mMemberProfiles.contains(d))
                        mMemberProfiles.add(d);
                }
            }

            if(mMemberProfiles.size() == 0) {
                return;
            }

            for(i=0; i< mMemberProfiles.size(); i++) {
                MesiboProfile user = mMemberProfiles.get(i);
                UserData data = (UserData)(user).other;
                Mesibo.MessageParams messageParams = new Mesibo.MessageParams(data.getPeer(), data.getGroupId(), Mesibo.FLAG_DEFAULT, 0);

                for(int j=0; null != mForwardMessageIds && j <= mForwardMessageIds.length-1; j++ ) {
                    if (mForwardMessageIds[j] > 0) {
                        long mId = Mesibo.random();
                        Mesibo.forwardMessage(messageParams, mId, mForwardMessageIds[j]);
                    }
                }

                if(!TextUtils.isEmpty(mForwardedMessage)) {
                    long mId = Mesibo.random();
                    Mesibo.sendMessage(messageParams, mId, mForwardedMessage);
                }
            }


            if(!mCloseAfterForward && mMemberProfiles.size() ==1 ) {
                MesiboProfile user = mMemberProfiles.get(0);
                UserData data = (UserData)(user).other;
                data.clearUnreadCount();
                boolean handledByApp = onClickUser(user.address, user.groupid, 0);
                if(!handledByApp) {
                    Intent intent = new Intent(ContactList.this, HuddleMessagingActivityNew.class);
                    intent.putExtra(MesiboUI.MESSAGE_ID, 0);
                    intent.putExtra(MesiboUI.PEER, user.address);
                    intent.putExtra(MesiboUI.GROUP_ID, user.groupid);
                    startActivity(intent);
                }
            }

            ContactList.this.finish();
            mForwardId = 0;
            return ;
        }*/

        public class SectionCellsViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString = null;
            public View mView = null;
            public ImageView mContactsProfile = null;
            //public ImageView invite = null, link = null;
            public TextView link = null;
            public ImageView messageIcon = null, videoCallIcon = null, audioCallIcon = null;
            public TextView mContactsName = null, mContactsPhone = null;
            public MenuPopupHelper PopupMenu = null;
            public RelativeLayout mHighlightView = null;
            public int position = 0;
            public CheckBox isChecked;


            public SectionCellsViewHolder(View view) {
                super(view);
                mView = view;
                mContactsProfile = (ImageView) view.findViewById(R.id.mes_rv_profile);
                //invite = (ImageView) view.findViewById(R.id.invite);
                link = (TextView) view.findViewById(R.id.link);
                isChecked = (CheckBox) view.findViewById(R.id.isChecked);
                messageIcon = (ImageView) view.findViewById(R.id.message);
                videoCallIcon = (ImageView) view.findViewById(R.id.video_call);
                audioCallIcon = (ImageView) view.findViewById(R.id.audio_call);
                mContactsName = (TextView) view.findViewById(R.id.mes_rv_name);
                mContactsPhone = (TextView) view.findViewById(R.id.mes_rv_phone);
                mHighlightView = (RelativeLayout) view.findViewById(R.id.highlighted_view);
            }
        }
    }
}