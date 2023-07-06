/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.MessagingModule;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.libraries.places.api.Places;
import com.google.gson.Gson;
import com.mesibo.api.Mesibo;
import com.mesibo.api.Mesibo.ConnectionListener;
import com.mesibo.api.Mesibo.MessageFilter;
import com.mesibo.api.Mesibo.MessageListener;
import com.mesibo.api.Mesibo.PresenceListener;
import com.mesibo.api.Mesibo.SyncListener;
import com.mesibo.api.MesiboFile;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboMessageProperties;
import com.mesibo.api.MesiboPresence;
import com.mesibo.api.MesiboProfile;
import com.mesibo.api.MesiboProfile.Listener;
import com.mesibo.api.MesiboReadSession;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.emojiview.EmojiconGridView;
import com.mesibo.emojiview.EmojiconTextView;
import com.mesibo.emojiview.EmojiconsPopup;
import com.mesibo.emojiview.emoji.Emojicon;
import com.mesibo.mediapicker.MediaPicker;
import com.mesibo.mediapicker.MediaPicker.ImageEditorListener;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Utils.NotificationSendClass;
import com.qamp.app.R;
import com.qamp.app.MessagingModule.AllUtils.LetterTileProvider;
import com.qamp.app.MessagingModule.AllUtils.TextToEmoji;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class MessagingFragment extends BaseFragment implements MessageListener, PresenceListener, ConnectionListener, SyncListener, OnClickListener, MessageViewHolder.ClickListener, OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ImageEditorListener, Listener, MessageAdapter.MessagingAdapterListener, MessagingActivityListener, MesiboMapScreenshot.Listener {
    public static final int MESIBO_MESSAGECONTEXTACTION_FORWARD = 1;
    public static final int MESIBO_MESSAGECONTEXTACTION_REPLY = 2;
    public static final int MESIBO_MESSAGECONTEXTACTION_RESEND = 4;
    public static final int MESIBO_MESSAGECONTEXTACTION_DELETE = 8;
    public static final int MESIBO_MESSAGECONTEXTACTION_COPY = 16;
    public static final int MESIBO_MESSAGECONTEXTACTION_FAVORITE = 32;
    static final int MESSAGING_PERMISSION_CODE = 101;
    private static final String TAG = "MessagingFragment";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static boolean isSharingOptionsOpen = false;
    static Bitmap mMapBitmap = null;
    private static int ONLINE_TIME = 60000;
    private static int UPDATE_INTERVAL = 10000;
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 10;
    private static int PLACE_PICKER_REQUEST = 199;
    boolean hidden = true;
    ImageButton ib_gallery = null;
    ImageButton ib_contacts = null;
    ImageButton ib_location = null;
    ImageButton ib_video = null;
    ImageButton ib_audio = null;
    ImageButton ib_upload = null;
    ImageButton ib_send = null;
    ImageButton ib_cam = null;
    ImageButton ib_showattach = null;
    ImageButton ib_closeattach = null;
    boolean mPressed = false;
    private ArrayList<MessageData> mMessageList = new ArrayList();
    private RecyclerView mRecyclerView = null;
    private MessageAdapter mAdapter = null;
    private LinearLayoutManager mLayoutManager = null;
    private LinearLayout showMessage = null;
    private HashMap<Long, MessageData> mMessageMap = new HashMap();
    private WeakReference<MesiboMessagingFragment.FragmentListener> mListener = null;
    private String mName = null;
    private String mPeer = null;
    private long mGroupId = 0L;
    private long mForwardId = 0L;
    private MesiboProfile mUser = null;
    private String mGroupStatus = null;
    private boolean mReadOnly = false;
    private LetterTileProvider mLetterTitler = null;
    private boolean read_flag = false;
    private MesiboPresence mPresence = null;
    private boolean showLoadMore = true;
    private int mLastReadCount = 0;
    private int mLastMessageCount = 0;
    private int mLastMessageStatus = -1;
    private ImageView mEmojiButton = null;
    private Map<String, String> mEmojiMap;
    private Toolbar toolbar = null;
    private MessageFilter mMessageFilter = Mesibo.getMessageFilter();
    private boolean mMediaHandled = true;
    private UserData mUserData = null;
    private LocationRequest mLocationRequest = null;
    private View mEditLayout;
    private View mAttachLayout;
    private View mBottomLayout;
    private TextView mReplyName;
    private EmojiconTextView mReplyText;
    private ImageView mReplyImage;
    private ImageView mReplyCancel;
    private RelativeLayout mReplyLayout;
    private EmojiconEditText mEmojiEditText;
    private MessageData mReplyMessage = null;
    private Boolean mReplyEnabled = false;
    private MesiboUI.Listener mMesiboUIHelperlistener = null;
    private MesiboUI.Config mMesiboUIOptions = null;
    private RelativeLayout mMessageViewBackgroundImage;
    private boolean mPlayServiceAvailable = false;
    private int mNonDeliveredCount = 0;
    private boolean mSelectionMode = false;
    private boolean mShowMissedCalls = true;
    private MesiboRecycleViewHolder.Listener mCustomViewListener = null;
    private MesiboRecycleViewHolder mHeaderView = null;
    private MesiboReadSession mReadSession = null;
    private FrameLayout mCustomLayout = null;
    private View mParentView = null;
    private Handler mScrollHandler = new Handler();
    private long uptime = Mesibo.getTimestamp();
    private boolean mFirstLayout = true;
    private MesiboMapScreenshot mMapScreenshot = new MesiboMapScreenshot();
    private int mMediaButtonClicked = -1;
    private GoogleApiClient mGoogleApiClient = null;
    private boolean mPlaceInitialized = false;
    private int mMessageOffset = 0;
    private boolean mLoading = false;
    private long mLoadTs = 0L;
    private boolean mGoogleApiClientChecked = false;

    public MessagingFragment() {
    }

    protected static void onMapBitmap(Bitmap bitmap) {
        mMapBitmap = bitmap;
    }

    public int getLayout() {
        return MesiboUI.getConfig().mMessagingFragmentLayout != 0 ? MesiboUI.getConfig().mMessagingFragmentLayout : R.layout.fragment_messaging;
    }

    public void onCreateCustomView(View parent, FrameLayout frame, String message) {
        if (parent != null) {
        }

    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == container) {
            return null;
        } else {
            this.setListener();
            if (null == this.getListener()) {
                this.finish();
                return null;
            } else {
                if (this instanceof MesiboRecycleViewHolder.Listener) {
                    this.mCustomViewListener = (MesiboRecycleViewHolder.Listener) this;
                }

                Bundle args = this.getArguments();
                String peer = args.getString("peer");
                long gid = args.getLong("groupid", 0L);
                if (null == this.mMessageList || null != peer && null != this.mPeer && 0 != this.mPeer.compareToIgnoreCase(peer) || gid > 0L && this.mGroupId > 0L && gid != this.mGroupId) {
                    this.read_flag = false;
                    if (null == this.mMessageList || this.mMessageList.size() > 0) {
                        this.mMessageList = new ArrayList();
                    }
                }

                this.mPeer = peer;
                this.mGroupId = gid;
                long forwardid = args.getLong("mid", 0L);
                this.mReadOnly = args.getBoolean("readonly", false);
                boolean createProfile = args.getBoolean("createprofile", false);
                boolean hideReply = args.getBoolean("hidereply", false);
                this.mShowMissedCalls = args.getBoolean("missedcalls", true);
                if (this.mGroupId > 0L) {
                    this.mUser = Mesibo.getProfile(this.mGroupId);
                    this.mLetterTitler = new LetterTileProvider(this.getActivity(), 60, (int[]) null);
                    if (null != this.mUser) {
                        this.mGroupStatus = this.mUser.getStatus();
                    }
                } else {
                    this.mUser = Mesibo.getProfile(this.mPeer);
                }

                this.mUser.addListener(this);
                View view = inflater.inflate(this.getLayout(), container, false);
                this.mParentView = view;
                if (Mesibo.isReady() && null != this.mUser) {
                    this.mUser.getImagePath();
                    this.mPresence = this.mUser.newPresence();
                    this.mRecyclerView = (RecyclerView) view.findViewById(R.id.chat_list_view);
                    //this.mRecyclerView.setBackgroundColor(MesiboUI.getConfig().messagingBackgroundColor);
                    this.mCustomLayout = (FrameLayout) view.findViewById(R.id.customLayout);
                    this.onCreateCustomView(view, this.mCustomLayout, (String) null);
                    this.mLayoutManager = new LinearLayoutManager(this.getActivity());
                    this.mLayoutManager.setStackFromEnd(true);
                    this.mRecyclerView.setLayoutManager(this.mLayoutManager);
                    this.mAdapter = new MessageAdapter(this.getActivity(), this, this.mMessageList, this, this.mCustomViewListener);
                    this.mRecyclerView.setAdapter(this.mAdapter);
                    this.ib_cam = (ImageButton) view.findViewById(R.id.cameraButton);
                    this.ib_cam.setOnClickListener(this);
                    this.showMessage = (LinearLayout) view.findViewById(R.id.messageLayout);
                    if (this.mAdapter.getItemCount() != 0 && this.mLayoutManager.findLastCompletelyVisibleItemPosition() == this.mAdapter.getItemCount() - 1) {
                        this.showMessgeVisible();
                    }

                    this.showMessage.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            MessagingFragment.this.showMessage.setVisibility(View.INVISIBLE);
                            MessagingFragment.this.loadFromDB(50);
                        }
                    });
                    View activityRootView;
                    if (hideReply) {
                        activityRootView = view.findViewById(R.id.reply_layout_id);
                        if (null != activityRootView) {
                            activityRootView.setVisibility(View.GONE);
                        }
                    }

                    this.mEmojiEditText = (EmojiconEditText) view.findViewById(R.id.chat_edit_text1);
                    this.setupTextWatcher(this.mEmojiEditText);
                    this.mEmojiEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                        }
                    });
                    this.ib_send = (ImageButton) view.findViewById(R.id.sendmessage);
                    this.ib_send.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            MessagingFragment.this.onSend();
                        }
                    });
                    this.ib_audio = (ImageButton) view.findViewById(R.id.audio);
                    this.ib_upload = (ImageButton) view.findViewById(R.id.document_btn);
                    this.ib_gallery = (ImageButton) view.findViewById(R.id.gallery);
                    this.ib_location = (ImageButton) view.findViewById(R.id.location);
                    this.ib_video = (ImageButton) view.findViewById(R.id.video);
                    this.ib_audio.setOnClickListener(this);
                    this.ib_upload.setOnClickListener(this);
                    this.ib_gallery.setOnClickListener(this);
                    this.ib_location.setOnClickListener(this);
                    this.ib_video.setOnClickListener(this);
                    this.ib_showattach = (ImageButton) view.findViewById(R.id.showAttachment);
                    this.ib_showattach.setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            MessagingFragment.this.mEditLayout.setVisibility(View.GONE);
                            MessagingFragment.this.mAttachLayout.setVisibility(View.VISIBLE);
                            isSharingOptionsOpen = true;
                        }
                    });
                    this.ib_showattach.setVisibility(this.mMediaHandled ? View.VISIBLE : View.GONE);
                    this.showLoadMore = false;
                    if (null == this.mUser.other) {
                        this.mUser.other = new UserData(this.mUser);
                    }

                    this.mUserData = (UserData) this.mUser.other;
                    this.mMediaHandled = Mesibo.isFileTransferEnabled();
                    if (!this.mMediaHandled) {
                        this.ib_cam.setClickable(false);
                        this.ib_cam.setVisibility(View.GONE);
                        this.ib_send.setClickable(true);
                        this.ib_send.setVisibility(View.VISIBLE);
                        this.ib_showattach.setClickable(false);
                        this.ib_showattach.setVisibility(View.GONE);
                    }

                    MesiboImages.init(this.getActivity());
                    this.mEmojiMap = TextToEmoji.getEmojimap();
                    this.mName = this.mUserData.getUserName();
                    Mesibo.addListener(this);
                    this.mMesiboUIHelperlistener = MesiboUI.getListener();
                    this.mMesiboUIOptions = MesiboUI.getConfig();
                    this.mBottomLayout = view.findViewById(R.id.bottomlayout);
                    this.mMessageViewBackgroundImage = (RelativeLayout) view.findViewById(R.id.chat_layout);
                    if (null != this.mMesiboUIOptions.messagingBackground) {
                        Drawable drawable = new BitmapDrawable(this.getResources(), this.mMesiboUIOptions.messagingBackground);
                        if (Build.VERSION.SDK_INT >= 16) {
                            this.mMessageViewBackgroundImage.setBackground(drawable);
                        } else {
                            this.mMessageViewBackgroundImage.setBackgroundDrawable(drawable);
                        }
                    }

                    this.mReplyLayout = (RelativeLayout) view.findViewById(R.id.reply_layout);
                    this.mReplyCancel = (ImageView) view.findViewById(R.id.reply_cancel);
                    this.mReplyCancel.setVisibility(View.VISIBLE);
                    this.mReplyCancel.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            MessagingFragment.this.mReplyLayout.setVisibility(View.GONE);
                            MessagingFragment.this.mReplyEnabled = false;
                        }
                    });
                    this.mReplyLayout.setVisibility(View.GONE);
                    this.mReplyImage = (ImageView) view.findViewById(R.id.reply_image);
                    this.mReplyName = (TextView) view.findViewById(R.id.reply_name);
                    this.mReplyText = (EmojiconTextView) view.findViewById(R.id.reply_text);
                    activityRootView = view.findViewById(R.id.chat_root_layout);
                    if (forwardid > 0L) {
                        MesiboMessage msg = this.mUser.newMessage();
                        msg.setForwarded(forwardid);
                        msg.send();
                        forwardid = 0L;
                    }

                    RelativeLayout rootView = (RelativeLayout) view.findViewById(R.id.chat_layout);
                    this.mEditLayout = view.findViewById(R.id.edit_layout);
                    this.mAttachLayout = view.findViewById(R.id.attachLayout);
                    final EmojiconsPopup popup = new EmojiconsPopup(rootView, this.getActivity());
                    popup.setSizeForSoftKeyboard();
                    this.mEmojiButton = (ImageView) view.findViewById(R.id.mojiButton);
                    this.mEmojiButton.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (!popup.isShowing()) {
                                if (popup.isKeyBoardOpen()) {
                                    popup.showAtBottom();
                                    MessagingFragment.this.changeEmojiKeyboardIcon(MessagingFragment.this.mEmojiButton, MesiboConfiguration.KEYBOARD_ICON);

                                } else {
                                    MessagingFragment.this.mEmojiEditText.setFocusableInTouchMode(true);
                                    MessagingFragment.this.mEmojiEditText.requestFocus();
                                    popup.showAtBottomPending();
                                    InputMethodManager inputMethodManager = (InputMethodManager) MessagingFragment.this.myActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(MessagingFragment.this.mEmojiEditText, 1);
                                    MessagingFragment.this.changeEmojiKeyboardIcon(MessagingFragment.this.mEmojiButton, MesiboConfiguration.KEYBOARD_ICON);
                                }
                            } else {
                                popup.dismiss();
                            }

                        }
                    });
                    popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        public void onDismiss() {
                            MessagingFragment.this.changeEmojiKeyboardIcon(MessagingFragment.this.mEmojiButton, MesiboConfiguration.EMOJI_ICON);
                        }
                    });
                    popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
                        public void onKeyboardOpen(int keyBoardHeight) {
                        }

                        public void onKeyboardClose() {
                            if (popup.isShowing()) {
                                popup.dismiss();
                            }

                        }
                    });
                    popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
                        public void onEmojiconClicked(Emojicon emojicon) {
                            if (MessagingFragment.this.mEmojiEditText != null && emojicon != null) {
                                int start = MessagingFragment.this.mEmojiEditText.getSelectionStart();
                                int end = MessagingFragment.this.mEmojiEditText.getSelectionEnd();
                                if (start < 0) {
                                    MessagingFragment.this.mEmojiEditText.append(emojicon.getEmoji());
                                } else {
                                    MessagingFragment.this.mEmojiEditText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
                                }

                            }
                        }
                    });
                    popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {
                        public void onEmojiconBackspaceClicked(View v) {
                            KeyEvent event = new KeyEvent(0L, 0L, 0, 67, 0, 0, 0, 0, 6);
                            MessagingFragment.this.mEmojiEditText.dispatchKeyEvent(event);
                        }
                    });
                    this.Mesibo_onConnectionStatus(Mesibo.getConnectionStatus());
                    String packageName = this.myActivity().getPackageName();
                    if (packageName.equalsIgnoreCase("com.qamp.app") && Mesibo.getAppId() != 1L) {
                        this.mMesiboUIOptions.mGoogleApiKey = "";
                    }

                    if (null == this.mMesiboUIOptions.mGoogleApiKey) {
                        this.mMesiboUIOptions.mGoogleApiKey = "";

                        try {
                            ApplicationInfo app = this.myActivity().getPackageManager().getApplicationInfo(this.myActivity().getPackageName(), PackageManager.GET_META_DATA);
                            Bundle bundle = app.metaData;
                            if (null != bundle) {
                                this.mMesiboUIOptions.mGoogleApiKey = bundle.getString("com.google.android.geo.API_KEY");
                            }
                        } catch (Exception var19) {
                            this.mMesiboUIOptions.mGoogleApiKey = "";
                        }
                    }

                    this.enableLocationServices();
                    return view;
                } else {
                    this.finish();
                    return view;
                }
            }
        }
    }

    public boolean onMapScreenshot(MesiboMessage msg, Bitmap bmp) {
        return false;
    }

    public void onStart() {
        super.onStart();
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }

    }

    public void onStop() {
        if (null != this.mGoogleApiClient && this.mGoogleApiClient.isConnected()) {
            this.mGoogleApiClient.disconnect();
        }

        if (null != this.mUserData && null != this.mReadSession) {
            this.mReadSession.enableReadReceipt(false);
        }

        super.onStop();
    }

    public void onDestroy() {
        if (null != this.mReadSession) {
            this.mReadSession.stop();
        }

        super.onDestroy();
    }

    public void setListener() {
        this.mListener = new WeakReference((MesiboMessagingFragment.FragmentListener) this.getActivity());
    }

    public MesiboMessagingFragment.FragmentListener getListener() {
        return null == this.mListener ? null : (MesiboMessagingFragment.FragmentListener) this.mListener.get();
    }

    public void onResume() {
        super.onResume();
        Utils.showServicesSuspendedAlert(this.getActivity());
        this.setListener();
        this.mNonDeliveredCount = 0;
        this.blockedUserView();
        Activity activity = this.myActivity();
        if (null != activity) {
            Mesibo.setForegroundContext(activity, 1, true);
            this.setProfilePicture();
            Mesibo.addListener(this);
            if (null == this.mUser) {
                this.finish();
            } else {
                if (!TextUtils.isEmpty(this.mUser.draft)) {
                    this.mEmojiEditText.setText(this.mUser.draft);
                }

                if (null != this.mUserData) {
                    if (null == this.mReadSession) {
                        this.mReadSession = this.mUser.createReadSession(this);
                        this.mReadSession.enableReadReceipt(true);
                        this.mReadSession.enableMissedCalls(this.mShowMissedCalls);
                        this.mReadSession.start();
                    } else {
                        this.mReadSession.enableReadReceipt(true);
                    }

                    this.addHeaderMessage();
                    if (!this.read_flag) {
                        if (null != this.mCustomViewListener) {
                        }

                        this.read_flag = true;
                        this.loadFromDB(50);
                    } else {
                        this.mAdapter.notifyDataSetChanged();
                    }

                    if (0L == this.mGroupId) {
                        this.mPresence.sendJoined();
                    }

                    if (this.mUser.groupid > 0L) {
                        this.mGroupStatus = this.mUser.getStatus();
                    }

                    if (!TextUtils.isEmpty(this.mGroupStatus)) {
                        this.updateUserStatus(this.mGroupStatus, 0L);
                    } else {
                        this.updateUserActivity((MesiboMessageProperties) null, 1);
                    }

                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        this.mUser.draft = this.mEmojiEditText.getText().toString();
        if (0L == this.mGroupId) {
            this.mPresence.sendLeft();
        }

    }

    private void finish() {
        FragmentActivity a = this.getActivity();
        if (null != a) {
            a.finish();
        }

    }

    private void blockedUserView() {
        if (!this.mReadOnly && (this.mUser.groupid <= 0L || this.mUser.isActive() || null == this.mBottomLayout) && !this.mUser.isMessageBlocked()) {
            this.mBottomLayout.setVisibility(View.VISIBLE);
        } else {
            this.mBottomLayout.setVisibility(View.GONE);
        }

    }

    public void onMediaButtonClicked(int buttonId) {
        if (buttonId == R.id.cameraButton) {
            MediaPicker.launchPicker(this.myActivity(), MediaPicker.TYPE_CAMERAIMAGE, Mesibo.getTempFilesPath());
        } else if (buttonId == R.id.audio) {
            MediaPicker.launchPicker(this.myActivity(), MediaPicker.TYPE_AUDIO);
        } else if (buttonId == R.id.document_btn) {
            MediaPicker.launchPicker(this.myActivity(), MediaPicker.TYPE_FILE);
        } else if (buttonId == R.id.location) {
            try {
                this.displayPlacePicker();
            } catch (GooglePlayServicesNotAvailableException var4) {
                var4.printStackTrace();
            } catch (GooglePlayServicesRepairableException var5) {
                var5.printStackTrace();
            }
        } else if (buttonId == R.id.video) {
            CharSequence[] Options = new CharSequence[]{MesiboUI.getConfig().videoFromRecorderTitle, MesiboUI.getConfig().videoFromGalleryTitle};
            AlertDialog.Builder builder = new AlertDialog.Builder(this.myActivity());
            builder.setTitle(MesiboUI.getConfig().videoSelectTitle);
            builder.setItems(Options, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        MediaPicker.launchPicker(MessagingFragment.this.myActivity(), MediaPicker.TYPE_CAMERAVIDEO, Mesibo.getTempFilesPath());
                    } else if (which == 1) {
                        MediaPicker.launchPicker(MessagingFragment.this.myActivity(), MediaPicker.TYPE_FILEVIDEO);
                    }

                }
            });
            builder.show();
        } else if (buttonId == R.id.gallery) {
            MediaPicker.launchPicker(this.myActivity(), MediaPicker.TYPE_FILEIMAGE);
        }

    }

    public void onClick(View v) {
        this.showAttachments(false);
        this.mPressed = false;
        this.hidden = true;
        this.mMediaButtonClicked = v.getId();
        List<String> permissions = new ArrayList();
        if (this.mMediaButtonClicked != R.id.location) {
            permissions.add("android.permission.WRITE_EXTERNAL_STORAGE");
        }

        if (this.mMediaButtonClicked == R.id.cameraButton || this.mMediaButtonClicked == R.id.video) {
            permissions.add("android.permission.CAMERA");
        }

        if (this.mMediaButtonClicked == R.id.location) {
            permissions.add("android.permission.ACCESS_FINE_LOCATION");
        }

        if (Utils.aquireUserPermissions(this.getActivity(), permissions, 101)) {
            this.onMediaButtonClicked(this.mMediaButtonClicked);
            this.mMediaButtonClicked = -1;
        }

    }


    public boolean Mesibo_onBackPressed() {
        if (this.mAttachLayout.getVisibility() == View.VISIBLE) {
            this.showAttachments(false);
            this.mPressed = false;
            this.hidden = true;
            showAttachments(false);
            return true;
        } else {
            return false;
        }
    }

    private void setProfilePicture() {
        LetterTileProvider tileProvider = new LetterTileProvider(this.myActivity(), 60, this.mMesiboUIOptions.mLetterTitleColors);
        Bitmap thumbnail = this.mUserData.getThumbnail(tileProvider);
        MesiboMessagingFragment.FragmentListener l = this.getListener();
        if (null != l) {
            l.Mesibo_onUpdateUserPicture(this.mUser, thumbnail, this.mUserData.getImagePath());
        }
    }

    protected void createLocationRequest() {
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval((long) UPDATE_INTERVAL);
        this.mLocationRequest.setFastestInterval((long) FATEST_INTERVAL);
        this.mLocationRequest.setPriority(100);
        this.mLocationRequest.setSmallestDisplacement((float) DISPLACEMENT);
    }

    private void displayPlacePicker() throws GooglePlayServicesNotAvailableException, GooglePlayServicesRepairableException {
        MesiboUI.Listener listener = MesiboUI.getListener();
        if (null == listener || !listener.MesiboUI_onShowLocation(this.getActivity(), this.mUser)) {
            this.enableLocationServices();
            if (this.mGoogleApiClient != null && this.mGoogleApiClient.isConnected() && this.mPlaceInitialized) {
                try {
                    mMapBitmap = null;
                    Intent mapIntent = new Intent(this.myActivity(), MesiboMapActivity.class);
                    this.myActivity().startActivityForResult(mapIntent, PLACE_PICKER_REQUEST);
                } catch (Exception var3) {
                    Toast.makeText(this.myActivity(), "Google Play Services exception.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private int updateUserStatus(String status, long duration) {
        if (null == status) {
            if (TextUtils.isEmpty(this.mGroupStatus)) {
                this.getListener().Mesibo_onUpdateUserOnlineStatus(this.mUser, (String) null);
            } else {
                this.getListener().Mesibo_onUpdateUserOnlineStatus(this.mUser, this.mGroupStatus);
            }
            return 0;
        } else {
            this.getListener().Mesibo_onUpdateUserOnlineStatus(this.mUser, status);
            return 0;
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private int updateUserActivity(MesiboMessageProperties params, int activity) {
        int connectionStatus = Mesibo.getConnectionStatus();
        if (6 == connectionStatus) {
            return this.updateUserStatus(this.mMesiboUIOptions.connectingIndicationTitle, 0L);
        } else if (10 == connectionStatus) {
            return this.updateUserStatus(this.mMesiboUIOptions.suspendIndicationTitle, 0L);
        } else if (8 == connectionStatus) {
            return this.updateUserStatus(getActivity().getResources().
                            getString(R.string.no_network_text),
                    0L);
        } else if (7 == connectionStatus) {
            return this.updateUserStatus(this.mMesiboUIOptions.offlineIndicationTitle, 0L);
        } else if (1 != connectionStatus) {
            return this.updateUserStatus(this.mMesiboUIOptions.offlineIndicationTitle, 0L);
        } else if (0 == activity) {
            return 0;
        } else {
            MesiboProfile profile = this.mUser;
            long groupid = 0L;
            if (null != params) {
                groupid = params.groupid;
                if (null != params.profile) {
                    profile = params.profile;
                }
            }

            String status = null;
            if (profile.isTyping(groupid)) {
                status = "";
                if (groupid > 0L) {
                    status = profile.getName() + " is ";
                }

                //status = status + getContext().getResources().getString(R.string.typing_text);
                status = "Typing";
            } else if (profile.isChatting() && 0L == groupid) {
                status = this.mMesiboUIOptions.joinedIndicationTitle;
            } else if (profile.isOnline() && 0L == groupid) {
//                if (getContext().getResources().getString(R.string.online_text)!=null)
               // status = getContext().getResources().getString(R.string.online_text);
                status = "Online";
            }

            return this.updateUserStatus(status, 0L);
        }
    }

    public boolean isMoreMessage() {
        return this.showLoadMore;
    }

    private boolean isForMe(MesiboMessageProperties msg) {
        if (msg.isRealtimeMessage()) {
            this.updateUserActivity(msg, 0);
        }

        return msg.isDestinedFor(this.mUser);
    }

    public void Mesibo_onSync(int count) {
        if (count > 0) {
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    MessagingFragment.this.loadFromDB(count);
                }
            });
        }

    }

    private void loadFromDB(int count) {
        this.mLastMessageCount = this.mMessageList.size();
        this.showLoadMore = false;
        this.mLastReadCount = this.mReadSession.read(count);
        if (this.mLastReadCount == count) {
            this.showLoadMore = true;
        } else {
            if (0 == this.mLastReadCount && this.mMessageList.size() == 0) {
                this.updateUiIfLastMessage((MesiboMessage) null);
            }

            this.mReadSession.sync(count, this);
        }

    }

    private boolean deleteTimestamp(MesiboMessage m) {
        if (this.mMessageList.size() <= this.mMessageOffset) {
            return true;
        } else if (null == m) {
            int n = this.mMessageList.size() - 1;
            MesiboMessage p = ((MessageData) this.mMessageList.get(n)).getMesiboMessage();
            if (null != p && p.isDate()) {
                this.mMessageList.remove(n);
                return true;
            } else {
                return false;
            }
        } else {
            MesiboMessage p = ((MessageData) this.mMessageList.get(this.mMessageOffset)).getMesiboMessage();
            if (p.isDate() && p.date.daysElapsed == m.date.daysElapsed) {
                this.mMessageList.remove(this.mMessageOffset);
                return true;
            } else {
                return false;
            }
        }
    }

    private void addTimestampToList(MesiboMessage m) {
        MesiboMessage d = new MesiboMessage(m.date);
        MessageData data = new MessageData(this.myActivity(), d);
        if (m.isRealtimeMessage()) {
            this.mMessageList.add(data);
        } else {
            this.mMessageList.add(this.mMessageOffset, data);
        }

    }

    private boolean addTimestamp(MesiboMessage m, boolean forced) {
        if (null == m) {
            return false;
        } else if (forced) {
            this.addTimestampToList(m);
            return true;
        } else {
            MesiboMessage p;
            if (this.mMessageList.size() > this.mMessageOffset && !m.isRealtimeMessage()) {
                p = ((MessageData) this.mMessageList.get(this.mMessageOffset)).getMesiboMessage();
                this.addTimestampToList(p);
                return true;
            } else {
                p = null;
                if (this.mMessageList.size() > this.mMessageOffset) {
                    int n = this.mMessageList.size() - 1;
                    p = ((MessageData) this.mMessageList.get(n)).getMesiboMessage();
                }

                if (null != p && p.date.daysElapsed == m.date.daysElapsed) {
                    return false;
                } else {
                    this.addTimestampToList(m);
                    return true;
                }
            }
        }
    }

    private boolean addTimestamp(MesiboMessage m) {
        return this.addTimestamp(m, false);
    }

    private void addMessage(MesiboMessage m) {
        MessageData data = new MessageData(this.myActivity(), m);
        this.mMessageMap.put(m.mid, data);
        if (!m.isRealtimeMessage()) {
            this.deleteTimestamp(m);
            this.mMessageList.add(this.mMessageOffset, data);
            this.addTimestamp(m);
        } else {
            boolean dateAdded = this.addTimestamp(m);
            this.mMessageList.add(data);
            this.mAdapter.addRow();
            if (dateAdded) {
                this.mAdapter.notifyItemRangeInserted(this.mMessageList.size() - 2, 2);
            } else {
                this.mAdapter.notifyItemInserted(this.mMessageList.size() - 1);
            }

            if (m.isEndToEndEncryptionStatus()) {
                return;
            }

            this.mRecyclerView.smoothScrollToPosition(this.mAdapter.getItemCount() - 1);
        }

    }

    MessageData findMessage(long id) {
        return (MessageData) this.mMessageMap.get(id);
    }

    public void Mesibo_onPresence(MesiboPresence params) {
        if (this.isForMe(params)) {
            if (this!=null)
            this.updateUserActivity(params, (int) params.presence);
        }
    }

    public void Mesibo_onPresenceRequest(MesiboPresence mesiboPresence) {
    }

    public void addHeaderMessage() {
        if (!TextUtils.isEmpty(MesiboUI.getConfig().headerTitle)) {
            if (this.mMessageOffset <= 0) {
                this.mMessageOffset = 1;
                MesiboMessage msg = new MesiboMessage();
                msg.setStatus(37);
                msg.message = MesiboUI.getConfig().headerTitle;
                this.mMessageList.add(0, new MessageData(this.myActivity(), msg));
            }
        }
    }

    public void updateUiIfLastMessage(MesiboMessage msg) {
        if (null != msg) {
            if (msg.isRealtimeMessage()) {
                return;
            }

            if (!msg.isLastMessage()) {
                return;
            }
        }

        if (this.mMessageList.size() > this.mMessageOffset) {
        }

        this.mAdapter.notifyItemRangeInserted(this.mLastMessageCount, this.mMessageList.size() - this.mLastMessageCount);
        this.mLoading = false;
    }

    public void Mesibo_onMessage(MesiboMessage msg) {
        if (!msg.isIncomingCall() && !msg.isOutgoingCall()) {
            if (this.isForMe(msg)) {
                if (msg.isEndToEndEncryptionStatus()) {
                    if (!Mesibo.e2ee().isEnabled()) {
                        return;
                    }

                    if (TextUtils.isEmpty(MesiboUI.getConfig().e2eeActive)) {
                        return;
                    }
                }

                MessageData m = this.findMessage(msg.mid);
                if (null == m) {
                    this.addMessage(msg);
                    this.updateUiIfLastMessage(msg);
                    MesiboFile f = msg.getFile();
                    if ((null == f || f.type == 200) && msg.hasLocation() && null == msg.getThumbnail() && !msg.isFileTransferFailed()) {
                        msg.replaceThumbnailWithLocation(16, this.mMesiboUIOptions.mGoogleApiKey);
                    }

                }
            }
        } else {
            this.updateUiIfLastMessage(msg);
        }
    }

    public void Mesibo_onMessageUpdate(MesiboMessage msg) {
        if (this.isForMe(msg)) {
            MessageData m = this.findMessage(msg.mid);
            if (null != m) {
                if (msg.isFileTransferInProgress()) {
                    MessageViewHolder vh = (MessageViewHolder) m.getViewHolder();
                    if (null != vh) {
                        vh.updateThumbnail(m);
                        return;
                    }
                }

                int position = m.getPosition();
                if (position >= 0) {
                    this.mAdapter.updateStatus(position);
                }

            }
        }
    }

    public void Mesibo_onMessageStatus(MesiboMessage params) {
        if (null != params && 0L != params.mid) {
            if (this.isForMe(params)) {
                MessageData m;
                int position;
                if (params.isDeleted()) {
                    m = this.findMessage(params.mid);
                    if (null != m) {
                        m.setDeleted(true);
                        position = m.getPosition();
                        if (position >= 0) {
                            this.mAdapter.updateStatus(position);
                        }
                    }

                } else {
                    this.mLastMessageStatus = params.getStatus();
                    ++this.mNonDeliveredCount;
                    if (params.isReadByPeer() || params.isDelivered()) {
                        this.updateUserActivity((MesiboMessageProperties) null, 0);
                        this.mNonDeliveredCount = 0;
                    }

                    if (0 != this.mMessageList.size()) {
                        if (params.isFailed()) {
                        }

                        if (this.mUser.groupid > 0L && 131 == params.getStatus()) {
                            this.getListener().Mesibo_onError(2, "Invalid group", "You are not a member of this group or not allowed to send message to this group");
                        }

                        if (!params.isReadByPeer()) {
                            m = this.findMessage(params.mid);
                            if (null != m) {
                                m.setStaus(params.getStatus());
                                position = m.getPosition();
                                if (position >= 0) {
                                    this.mAdapter.updateStatus(position);
                                }
                            }

                        } else {
                            int i = this.mMessageList.size();
                            boolean found = false;

                            while (i > 0) {
                                MessageData cm = (MessageData) this.mMessageList.get(i - 1);
                                if (cm.getMesiboMessage() == params || cm.getMesiboMessage().mid == params.mid) {
                                    found = true;
                                }

                                if (!found) {
                                    --i;
                                } else {
                                    if (null == cm) {
                                        return;
                                    }

                                    if (cm.getStatus() == 3 && cm.getMesiboMessage() != params) {
                                        break;
                                    }

                                    if (cm.getStatus() == 2 || cm.getStatus() == 1) {
                                        cm.setStaus(params.getStatus());
                                    }

                                    --i;
                                }
                            }

                            this.mAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    public void Mesibo_onConnectionStatus(int status) {
        if (status == 22) {
            this.finish();
        } else {
            if (status == 10) {
                Utils.showServicesSuspendedAlert(this.getActivity());
            }

            if (1 == status) {
                this.updateUserActivity((MesiboMessageProperties) null, 1);
            }

            this.updateUserActivity((MesiboMessageProperties) null, 0);
        }
    }

    public void MesiboProfile_onUpdate(MesiboProfile userProfile) {
        if (Mesibo.isUiThread()) {
            if (userProfile.isDeleted()) {
                this.finish();
            } else {
                this.setProfilePicture();
                this.blockedUserView();
            }
        } else {
            (new Handler(Looper.getMainLooper())).post(new Runnable() {
                public void run() {
                    if (userProfile.isDeleted()) {
                        MessagingFragment.this.finish();
                    } else {
                        MessagingFragment.this.setProfilePicture();
                        MessagingFragment.this.blockedUserView();
                    }
                }
            });
        }
    }

    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int status) {
    }

    public void showMessgeVisible() {
        this.showMessage.setVisibility(View.VISIBLE);
    }

    public synchronized void loadMoreMessages() {
        if (!this.mLoading) {
            long ts = Mesibo.getTimestamp();
            if (ts - this.mLoadTs >= 2000L) {
                this.mLoadTs = ts;
                this.mLoading = true;
                this.loadFromDB(50);
            }
        }
    }

    public void showMessageInvisible() {
        if (this.showMessage.getVisibility() == View.VISIBLE) {
            this.showMessage.setVisibility(View.GONE);
        }

    }

    private void showAttachments(boolean show) {
        boolean isVisible = this.mAttachLayout.getVisibility() == View.VISIBLE;
        if (show != isVisible) {
            this.mEditLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            this.mAttachLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            isSharingOptionsOpen = false;
        }
    }

    public void onImageEdit(int type, String caption, String filePath, Bitmap bmp, int result) {
        if (0 == result) {
            this.sendFile(type, caption, filePath, bmp, result);
        }
    }

    private void sendFile(int type, String caption, String filePath, Bitmap bmp, int result) {
        if (0 == result) {
            int temp = this.mMessageList.size();
            MesiboMessage m = this.mUser.newMessage();
            if (null == bmp || MediaPicker.TYPE_FILEIMAGE != type && MediaPicker.TYPE_CAMERAIMAGE != type) {
                m.setContent(filePath);
            } else {
                m.setContent(bmp);
            }

            m.message = caption;
            this.mReplyEnabled = false;
            this.mReplyLayout.setVisibility(View.GONE);
            m.send();
        }
    }

    public void onItemClicked(int position) {
        if (this.mSelectionMode) {
            this.toggleSelection(position);
        } else {
            MessageData d = (MessageData) this.mMessageList.get(position);
            MesiboMessage m = d.getMesiboMessage();
            if (m.isRichMessage()) {
                if (!m.isFileTransferRequired() && !m.isFileTransferInProgress()) {
                    MesiboFile f = m.getFile();
                    if (null != f && f.type != 200) {
                        if (!TextUtils.isEmpty(f.path) && !m.openExternally) {
                            if (1 == f.type) {
                                MesiboUIManager.launchPictureActivity(this.myActivity(), this.mUser.getName(), f.path);
                            } else {
                                Mesibo.launchFile(this.myActivity(), f.path);
                            }
                        } else {
                            Mesibo.launchUrl(this.myActivity(), f.url);
                        }
                    } else {
                        if (m.hasLocation()) {
                            Mesibo.launchLocation(this.myActivity(), m);
                        }

                    }
                } else {
                    m.toggleFileTransfer(1);
                }
            }
        }
    }

    public boolean onItemLongClicked(int position) {
        if (!this.mSelectionMode) {
            this.getListener().Mesibo_onShowInContextUserInterface();
            this.mSelectionMode = true;
        }

        this.toggleSelection(position);
        return true;
    }

    private void toggleSelection(int position) {
        int gPosition = this.mAdapter.globalPosition(position);
        this.mAdapter.toggleSelection(gPosition);
        this.mAdapter.notifyItemChanged(position);
        int count = this.mAdapter.getSelectedItemCount();
        if (count == 0) {
            this.getListener().Mesibo_onHideInContextUserInterface();
        } else {
            this.getListener().Mesibo_onContextUserInterfaceCount(count);
        }

    }

    public void sendTextMessage(String newText) {
        if (0 != newText.length()) {
            MesiboMessage msg = this.mUser.newMessage();
            msg.message = newText;
            if (this.mReplyEnabled && null != this.mReplyMessage) {
                msg.refid = this.mReplyMessage.getMid();
            }
            this.mReplyEnabled = false;
            this.mReplyLayout.setVisibility(View.GONE);
            msg.send();
            MesiboProfile profile = getProfile();
            profile.addListener(this);
            profile = getProfile();
            if (profile.isGroup()==false){
                NotificationSendClass.pushNotifications(getContext(), mUser.getAddress()
                        , ""+profile.getName(), ""+msg.message.toString(),"MESSAGE",profile.getAddress());
                Log.e("userToken",profile.getAddress() );
                Log.e(("UserToken"),mUser.getAddress());
                Log.e("name",profile.getName());
            }
            else{
                NotificationSendClass.pushNotificationsGroup(getContext(),String.valueOf(profile.getGroupId()),""+profile.getName(),""+msg.message.toString());
                    Log.e("groupId", new Gson().toJson(profile.getGroupId()));
                Log.e("senderName", new Gson().toJson(Mesibo.getSelfProfile().getName()));
                    Log.e("userToken", AppConfig.getConfig().token);
            }
             this.mEmojiEditText.getText().clear();
        }
    }

    public MesiboProfile getProfile() {
        if (mGroupId > 0) return Mesibo.getProfile(mGroupId);
        return Mesibo.getSelfProfile();
    }

    private void onSend() {
        String newStr = this.mEmojiEditText.getText().toString();
        String newText = newStr.trim();
        this.mEmojiEditText.getText().clear();
        this.mEmojiEditText.setText("");
        Entry entry;
        if (MesiboUI.getConfig().mConvertSmilyToEmoji) {
            for (Iterator iitr = this.mEmojiMap.entrySet().iterator(); iitr.hasNext();
                 newText = newText.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue())) {
                entry = (Entry) iitr.next();
            }
        }

        this.sendTextMessage(newText);
    }

    private void setupTextWatcher(EmojiconEditText et) {
        et.setRawInputType(16385);
        et.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                String newStr = MessagingFragment.this.mEmojiEditText.getText().toString().trim();
                if (newStr.length() > 0) {
                    MessagingFragment.this.mPresence.sendTyping();
                }
            }

            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            public void afterTextChanged(Editable s) {
                if (MessagingFragment.this.mMediaHandled) {
                    TranslateAnimation animate;
                    if (s.length() == 0) {
                        if (MessagingFragment.this.ib_cam.getVisibility() == View.GONE) {
                            animate = new TranslateAnimation(15.0F, 0.0F, 0.0F, 0.0F);
                            animate.setDuration(100L);
                            MessagingFragment.this.ib_cam.startAnimation(animate);
                            MessagingFragment.this.ib_send.startAnimation(animate);
                            if (MessagingFragment.this.mMediaHandled) {
                                MessagingFragment.this.ib_cam.setVisibility(View.VISIBLE);
                                MessagingFragment.this.ib_send.setVisibility(View.GONE);
                            }
                        }
                    } else if (MessagingFragment.this.ib_cam.getVisibility() == View.VISIBLE) {
                        animate = new TranslateAnimation(0.0F, 15.0F, 0.0F, 0.0F);
                        animate.setDuration(100L);
                        MessagingFragment.this.ib_cam.startAnimation(animate);
                        MessagingFragment.this.ib_send.startAnimation(animate);
                        MessagingFragment.this.ib_cam.setVisibility(View.GONE);
                        MessagingFragment.this.ib_send.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    private void enableLocationServices() {
        if (null == this.mGoogleApiClient || !this.mGoogleApiClientChecked || !this.mPlaceInitialized) {
            Context context = this.myActivity();
            if (null != context) {
                if (!this.mGoogleApiClientChecked) {
                    this.mGoogleApiClientChecked = true;
                    if (Utils.checkPlayServices(this.myActivity(), 1000)) {
                        this.buildGoogleApiClient();
                        this.createLocationRequest();
                        this.mPlayServiceAvailable = true;
                    }
                }

                if (TextUtils.isEmpty(this.mMesiboUIOptions.mGoogleApiKey)) {
                    Log.e("mesibo", "Google map support API key is not defined in AndroidManifest.xml");
                } else {
                    this.mPlaceInitialized = true;
                    if (!Places.isInitialized()) {
                        Places.initialize(this.myActivity(), this.mMesiboUIOptions.mGoogleApiKey, Locale.US);
                    }

                }
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = (new GoogleApiClient.Builder(this.getActivity())).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    public void onConnected(@Nullable Bundle bundle) {
    }

    public void onConnectionSuspended(int i) {
    }

    public void onLocationChanged(Location location) {
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    public void onMapReady(GoogleMap googleMap) {
    }

    public void Mesibo_onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (101 == requestCode) {
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == -1) {
                    this.getListener().Mesibo_onError(1, "Permission Denied", "One or more required permission was denied by you! Change the permission from settings and try again");
                    return;
                }
            }

            this.onMediaButtonClicked(this.mMediaButtonClicked);
            this.mMediaButtonClicked = -1;
        }
    }

    public boolean Mesibo_onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PLACE_PICKER_REQUEST && requestCode < MediaPicker.getBaseTypeValue() && requestCode > MediaPicker.getMaxTypeValue()) {
            return false;
        } else if (-1 != resultCode) {
            return true;
        } else {
            String ext;
            if (requestCode == PLACE_PICKER_REQUEST) {
                Bundle b = data.getExtras();
                ext = b.getString("name");
                String addressOfLocation = b.getString("address");
                int temp = this.mMessageList.size();
                MesiboMessage msg = this.mUser.newMessage();
                msg.title = ext;
                msg.message = addressOfLocation;
                msg.latitude = (double) ((float) b.getDouble("lat"));
                msg.longitude = (double) ((float) b.getDouble("lon"));
                msg.setContentType(200);
                if (null != mMapBitmap) {
                    msg.setThumbnail(mMapBitmap);
                }

                if (this.mReplyEnabled && null != this.mReplyMessage) {
                    msg.refid = this.mReplyMessage.getMid();
                }

                this.mReplyEnabled = false;
                this.mReplyLayout.setVisibility(View.GONE);
                msg.send();
                return true;
            } else {
                String filePath = MediaPicker.processOnActivityResult(this.myActivity(), requestCode, resultCode, data);
                if (null == filePath) {
                    return true;
                } else {
                    if (MediaPicker.TYPE_FILE == requestCode) {
                        ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
                        if (ext.length() > 3) {
                            ext = ext.substring(0, 3);
                        }

                        if (!ext.equalsIgnoreCase("jpg") && !ext.equalsIgnoreCase("jpe") && !ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("gif")) {
                            if (ext.equalsIgnoreCase("mp4") || ext.equalsIgnoreCase("avi") || ext.equalsIgnoreCase("3g") || ext.equalsIgnoreCase("3gp")) {
                                requestCode = MediaPicker.TYPE_FILEVIDEO;
                            }
                        } else {
                            requestCode = MediaPicker.TYPE_FILEIMAGE;
                        }
                    }

                    int drawableid = -1;
                    if (MediaPicker.TYPE_AUDIO == requestCode) {
                        // drawableid = drawable.file_audio;
                    } else if (MediaPicker.TYPE_FILE == requestCode) {
                        drawableid = MesiboImages.getFileDrawable(filePath);
                    }

                    MesiboUIManager.launchImageEditor(this.myActivity(), requestCode, drawableid, (String) null, filePath, true, true, false, false, 1280, this);
                    return true;
                }
            }
        }
    }

    public int Mesibo_onGetEnabledActionItems() {
        int enabled = 41;
        List<Integer> selection = this.mAdapter.getSelectedItems();
        boolean hideResend = true;
        Iterator var4 = selection.iterator();

        while (var4.hasNext()) {
            Integer i = (Integer) var4.next();
            MessageData cm = (MessageData) this.mMessageList.get(i);
            if (cm.getMesiboMessage().isFailed()) {
                hideResend = false;
            }
        }

        if (!hideResend) {
            enabled |= 4;
        }

        if (selection.size() == 1) {
            enabled |= 18;
        }

        return enabled;
    }

    public boolean Mesibo_onActionItemClicked(int item) {
        List selection;
        int j;
        Iterator var19;
        Integer i;
        if (8 == item) {
            selection = this.mAdapter.getSelectedItems();
            this.mAdapter.clearSelections();
            Collections.reverse(selection);
            j = Mesibo.getMessageRetractionInterval();
            boolean deleteRemote = true;
            var19 = selection.iterator();

            while (var19.hasNext()) {
                i = (Integer) var19.next();
                MessageData m = (MessageData) this.mMessageList.get(i);
                if (m.getStatus() > 3 || m.isDeleted() || (Mesibo.getTimestamp() - m.getMesiboMessage().ts) / 1000L > (long) j) {
                    deleteRemote = false;
                    break;
                }
            }

            if (deleteRemote) {
                this.promptAndDeleteMessage(selection);
            } else {
                this.deleteSelectedMessages(selection, false);
            }

            return true;
        } else if (16 == item) {
            String st = this.mAdapter.copyData();
            if (TextUtils.isEmpty(st)) {
                return true;
            } else {
                st = st.trim();
                ClipboardManager clipboard = (ClipboardManager) this.myActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy", st);
                clipboard.setPrimaryClip(clip);
                return true;
            }
        } else {
            MessageData cm;
            Iterator var11;
            Integer i1;
            int index;
            if (4 == item) {
                selection = this.mAdapter.getSelectedItems();
                this.mAdapter.clearSelections();
                var11 = selection.iterator();

                while (var11.hasNext()) {
                    i1 = (Integer) var11.next();
                    index = i1;
                    cm = (MessageData) this.mMessageList.get(i1);
                    if (cm.getMesiboMessage().isFailed()) {
                        Mesibo.resend(cm.getMesiboMessage().mid);
                    }
                }

                return true;
            } else if (1 == item) {
                selection = this.mAdapter.getSelectedItems();
                this.mAdapter.clearSelections();
                j = 0;
                long[] mids = new long[selection.size()];
                var19 = selection.iterator();

                while (true) {
                    MessageData cm1;
                    MesiboMessage m;
                    do {
                        if (!var19.hasNext()) {
                            Log.e("here","here");
                            Toast.makeText(getContext(),"Forwarding Message is under developement",Toast.LENGTH_LONG).show();
                            //MesiboUIManager.launchContactActivity(this.myActivity(), MesiboUserListFragment.MODE_SELECTCONTACT_FORWARD, mids);
                            return true;
                        }

                        i = (Integer) var19.next();
                        int index1 = i;
                        cm1 = (MessageData) this.mMessageList.get(i);
                        m = cm1.getMesiboMessage();
                    } while (m.isRichMessage() && m.isFileTransferRequired());

                    mids[j++] = cm1.getMid();
                }
            } else if (32 != item) {
                if (2 == item) {
                    selection = this.mAdapter.getSelectedItems();
                    this.mAdapter.clearSelections();
                    this.mReplyEnabled = true;
                    var11 = selection.iterator();
                    if (var11.hasNext()) {
                        i = (Integer) var11.next();
                        index = i;
                        this.mReplyMessage = (MessageData) this.mMessageList.get(i);
                        String username = "You";
                        if (this.mReplyMessage.getMesiboMessage().isIncoming()) {
                            username = this.mReplyMessage.getUsername();
                        }

                        this.mReplyName.setTextColor(this.mReplyMessage.getNameColor());
                        this.mReplyName.setText(username);
                        this.mReplyText.setText(this.mReplyMessage.getDisplayMessage());
                        this.mReplyImage.setVisibility(View.GONE);
                        Bitmap image = this.mReplyMessage.getImage();
                        if (image != null) {
                            this.mReplyImage.setVisibility(View.VISIBLE);
                            this.mReplyImage.setImageBitmap(image);
                        }

                        this.mReplyLayout.setVisibility(View.VISIBLE);
                    }

                    return true;
                } else {
                    return false;
                }
            } else {
                selection = this.mAdapter.getSelectedItems();
                this.mAdapter.clearSelections();
                Boolean setFlag = false;
                Iterator var4 = selection.iterator();

                Integer i2;
                while (var4.hasNext()) {
                    i2 = (Integer) var4.next();
                    cm = (MessageData) this.mMessageList.get(i2);
                    setFlag = cm.getFavourite();
                    if (setFlag) {
                        break;
                    }
                }

                setFlag = !setFlag;
                var4 = selection.iterator();

                while (var4.hasNext()) {
                    i = (Integer) var4.next();
                    cm = (MessageData) this.mMessageList.get(i);
                    if (cm.getFavourite() != setFlag) {
                        cm.setFavourite(setFlag);
                        this.mAdapter.updateStatus(i);
                    }
                }

                return true;
            }
        }
    }

    public void deleteSelectedMessages(List<Integer> selection, boolean remote) {
        List<Long> mids = new ArrayList();
        List<Integer> deleted = new ArrayList();
        Integer maxDeleted = -1;
        Integer minDeleted = 100000000;
        Iterator var7 = selection.iterator();

        while (var7.hasNext()) {
            Integer i = (Integer) var7.next();
            int index = i;
            MessageData md = (MessageData) this.mMessageList.get(i);
            if (md != null) {
                md.setDeleted(true);
            }

            mids.add(((MessageData) this.mMessageList.get(i)).getMid());
            if (remote) {
                this.mAdapter.notifyItemChanged(i);
            } else {
                if (i > 0) {
                    MessageData p = (MessageData) this.mMessageList.get(i - 1);
                    MessageData n = null;
                    if (i < this.mMessageList.size() - 1) {
                        n = (MessageData) this.mMessageList.get(i + 1);
                    }

                    if (p.getMesiboMessage().isDate() && (n == null || n.getMesiboMessage().isDate())) {
                        p.setVisible(false);
                        deleted.add(i - 1);
                        if (minDeleted > i - 1) {
                            minDeleted = i - 1;
                        }
                    }
                }

                deleted.add(i);
                if (minDeleted > i) {
                    minDeleted = i;
                }

                if (maxDeleted < i) {
                    maxDeleted = i;
                }

                md.setVisible(false);
            }
        }

        long[] m = new long[mids.size()];

        int i;
        for (i = 0; i < mids.size(); ++i) {
            m[i] = (Long) mids.get(i);
        }

        if (!remote) {
            Mesibo.deleteMessages(m);
        } else {
            Mesibo.wipeAndRecallMessages(m);
        }

        if (deleted.size() == 1) {
            i = (Integer) deleted.get(0);
            this.mMessageList.remove(i);
            this.mAdapter.notifyDataSetChanged();
        } else if (deleted.size() > 1) {
            for (i = maxDeleted; i >= minDeleted; --i) {
                if (!((MessageData) this.mMessageList.get(i)).isVisible()) {
                    this.mMessageList.remove(i);
                }
            }

            this.deleteTimestamp((MesiboMessage) null);
            this.mAdapter.notifyDataSetChanged();
        }

    }

    public void promptAndDeleteMessage(final List<Integer> selection) {
        MesiboUI.Config opts = MesiboUI.getConfig();
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
        builder.setTitle(getActivity().getResources().getString(R.string.delete_messages));
        String[] items = new String[]{getActivity().getResources()
                .getString(R.string.delete_for_everyone_text),
                getActivity().getResources().getString(R.string.delete_for_me)};
//                getActivity().getResources().getString(R.string.cancel_text)};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (2 != which) {
                    boolean remote = false;
                    if (0 == which) {
                        remote = true;
                    }
                    MessagingFragment.this.deleteSelectedMessages(selection, remote);
                }
            }
        });
        builder.show();
    }

    public void Mesibo_onInContextUserInterfaceClosed() {
        this.mAdapter.clearSelections();
        this.mSelectionMode = false;
    }
}
