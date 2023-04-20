/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

package com.qamp.app.messaging;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.qampcallss.api.MesiboCall;
import com.qamp.app.qampcallss.api.p000ui.MesiboDefaultCallActivity;

public class MesiboMessagingActivity extends AppCompatActivity implements MesiboMessagingFragment.FragmentListener, Mesibo.ConnectionListener, MesiboCall.IncomingListener {

    static int FROM_MESSAGING_ACTIVITY = 1;
    /* access modifiers changed from: private */
    public ActionMode mActionMode = null;
    /* access modifiers changed from: private */
    public MesiboUI.Listener mMesiboUIHelperlistener = null;
    /* access modifiers changed from: private */
    public MesiboUI.Config mMesiboUIOptions = null;
    /* access modifiers changed from: private */
    public String mProfileImagePath = null;
    /* access modifiers changed from: private */
    public MesiboProfile mUser = null;
    public MesiboMessage mParameter = null;
    ImageView callButton, videoCallButton;
    MessagingFragment mFragment = null;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private ImageView mProfileImage = null;
    private Bitmap mProfileThumbnail = null;
    private TextView mTitle = null;
    private Toolbar mToolbar = null;
    private TextView mUserStatus = null;
    private ImageView isOnlineDot;

    /* access modifiers changed from: protected */
    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        MesiboMessagingActivity.super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();


        // Initializing Mesibo
        Mesibo mesibo = Mesibo.getInstance();
        mesibo.init(getApplicationContext());
        //mesibo.setAccessToken(token);
        boolean res = mesibo.setDatabase("callapp.db", 0);
        mesibo.addListener(this);
        Mesibo.start();

//     Initializing call

        MesiboCall.getInstance().init(this);


        /* set profile so that it is visible in call screen */
        MesiboProfile u = new MesiboProfile();
        u.setName("Mabel Bay");
        u.address = "destination";
        if (args != null) {
            if (!Mesibo.isReady()) {
                finish();
                return;
            }
            this.mMesiboUIHelperlistener = MesiboUI.getListener();
            this.mMesiboUIOptions = MesiboUI.getConfig();
            String peer = args.getString(MesiboUI.PEER);
            long groupId = args.getLong(MesiboUI.GROUP_ID);
            if (groupId > 0) {
                this.mUser = Mesibo.getProfile(groupId);
            } else {
                this.mUser = Mesibo.getProfile(peer);
            }
            if (this.mUser == null) {
                finish();
                return;
            }
            //mParameter = new MesiboMessageProperties(peer, groupId, Mesibo.FLAG_DEFAULT, 0);
            mParameter = new MesiboMessage();
            mParameter.setPeer("peer");
            //=new MesiboMessage("peer",groupId,3L,0);

            // mParameter.set
            //mParameter.setP
            //mParameter = new MesiboMessage();
            //this.mParameter = new MesiboMessageProperties(peer,groupId,3L,0);
            MesiboUIManager.enableSecureScreen(this);
            setContentView(R.layout.activity_messaging_new);

            Utils.setActivityStyle(this, this.mToolbar);


            findViewById(R.id.chat_back).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MesiboMessagingActivity.this.onBackPressed();
                }
            });
            this.mUserStatus = (TextView) findViewById(R.id.chat_profile_subtitle);
            this.isOnlineDot = (ImageView) findViewById(R.id.isOnlineDot);
            Utils.setTextViewColor(this.mUserStatus, MesiboUI.getConfig().mToolbarTextColor);
            this.mProfileImage = (ImageView) findViewById(R.id.chat_profile_pic);
            String name = this.mUser.getName();
            if (TextUtils.isEmpty(name)) {
                name = this.mUser.address;
            }
            if (this.mProfileImage != null) {
                final String name_final = name;
                this.mProfileImage.setOnClickListener(new View.OnClickListener() {
                    /* JADX WARNING: type inference failed for: r0v3, types: [android.content.Context, com.qamp.app.messaging.MesiboMessagingActivity] */
                    public void onClick(View v) {
                        if (MesiboMessagingActivity.this.mUser != null) {
                            String unused = MesiboMessagingActivity.this.mProfileImagePath = MesiboMessagingActivity.this.mUser.getImageOrThumbnailPath();
                            MesiboUIManager.launchPictureActivity(MesiboMessagingActivity.this, name_final, MesiboMessagingActivity.this.mProfileImagePath);
                        }
                    }
                });
            }
            RelativeLayout nameLayout = (RelativeLayout) findViewById(R.id.name_tite_layout);
            this.mTitle = (TextView) findViewById(R.id.chat_profile_title);
            this.mTitle.setText(name);
            Utils.setTextViewColor(this.mTitle, MesiboUI.getConfig().mToolbarTextColor);
            if (this.mTitle != null) {
                nameLayout.setOnClickListener(new View.OnClickListener() {
                    /* JADX WARNING: type inference failed for: r1v0, types: [android.content.Context, com.qamp.app.messaging.MesiboMessagingActivity] */
                    public void onClick(View v) {
                        if (MesiboMessagingActivity.this.mMesiboUIHelperlistener != null) {
                            MesiboMessagingActivity.this.mMesiboUIHelperlistener.MesiboUI_onShowProfile(MesiboMessagingActivity.this, MesiboMessagingActivity.this.mUser);
                        }
                    }
                });
            }
            startFragment(savedInstanceState);
        }
        this.callButton = findViewById(R.id.action_call);
        this.videoCallButton = findViewById(R.id.action_videocall);
        String destination = "destination";
        this.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/**
 //    if (view.getId() == R.id.imageView2) { // For Calling
 // if (0 == mParameter.groupid) {
 if (!MesiboCall.getInstance().callUi(MesiboMessagingActivity.this, mUser.address, false))
 //    MesiboCall.getInstance().callUiForExistingCall(MesiboMessagingActivity.this);
 //launchCustomCallActivity(destination, true, false);
 //} else {
 //  MesiboCall.getInstance().groupCallUi(MesiboMessagingActivity.this, Mesibo.getProfile(mParameter.groupid), false, true);
 Log.e("Aditya","Aditya");

 Intent intent = new Intent(MesiboMessagingActivity.this, QampDefaultCallActivity.class);
 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
 intent.putExtra("video", true);
 intent.putExtra("address", destination);
 intent.putExtra("incoming", false);
 startActivity(intent);*/
                Log.e("Aditya", "reached");
                if (!MesiboCall.getInstance().callUi(getApplicationContext(), mUser, false)) {

                    Log.e("Arr", String.valueOf(MesiboCall.getInstance().callUi(getApplicationContext(), mUser, false)));
                    MesiboCall.getInstance().callUiForExistingCall(getApplicationContext());
                }
                //launchCustomCallActivity(destination, true, false);

            }


        });

        this.videoCallButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View view) {
                Log.e("Aditya", "reached1");
                /**if (view.getId() == R.id.imageView4) {
                 //if(0 == mParameter.groupid) {
                 if(!MesiboCall.getInstance().callUi(MesiboMessagingActivity.this, mParameter.profile.address, true))
                 //launchCustomCallActivity(destination, true, false);
                 MesiboCall.getInstance().callUiForExistingCall(MesiboMessagingActivity.this);
                 //} else {
                 //  MesiboCall.getInstance().groupCallUi(MesiboMessagingActivity.this, Mesibo.getProfile(mParameter.groupid), true, true);

                 }*/

                if (!MesiboCall.getInstance().callUi(getApplicationContext(), mUser, true))
                    //MesiboCall.getInstance().callUiForExistingCall(getApplicationContext());
                    launchCustomCallActivity(destination, true, false);
            }
        });
    }

    private void setProfilePicture() {
    }


    protected void launchCustomCallActivity(String destination, boolean video, boolean incoming) {
        Intent intent = new Intent(this, MesiboDefaultCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("video", video);
        intent.putExtra("address", destination);
        intent.putExtra("incoming", incoming);
        startActivity(intent);
    }


    private void startFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container) != null && savedInstanceState == null) {
            this.mFragment = new MessagingFragment();
            this.mFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, this.mFragment).commit();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        MesiboMessagingActivity.super.onStart();
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.qamp.app.messaging.MesiboMessagingActivity] */
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.mMesiboUIHelperlistener != null) {
            this.mMesiboUIHelperlistener.MesiboUI_onGetMenuResourceId(this, FROM_MESSAGING_ACTIVITY, this.mUser, menu);
        }
        return true;
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [android.content.Context, com.qamp.app.messaging.MesiboMessagingActivity, androidx.appcompat.app.AppCompatActivity] */
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == 16908332) {
            finish();
            return true;
        }
        this.mMesiboUIHelperlistener.MesiboUI_onMenuItemSelected(this, FROM_MESSAGING_ACTIVITY, this.mUser, id);
        return MesiboMessagingActivity.super.onOptionsItemSelected(item);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        MesiboMessagingActivity.super.onDestroy();
    }

    public void onBackPressed() {
        MessagingFragment f = this.mFragment;

        if (f == null || !f.Mesibo_onBackPressed()) {
            MesiboMessagingActivity.super.onBackPressed();
            Intent mainActivity = new Intent(MesiboMessagingActivity.this, MesiboUserListActivityNew.class);
            startActivity(mainActivity);
            finish();
        }
//        else {
//            Intent mainActivity = new Intent(MesiboMessagingActivity.this, MesiboUserListActivityNew.class);
//            startActivity(mainActivity);
//            finish();
//            Toast.makeText(this, "that", Toast.LENGTH_SHORT).show();
//
//        }
    }


    public void Mesibo_onUpdateUserPicture(MesiboProfile profile, Bitmap thumbnail, String picturePath) {
        this.mProfileThumbnail = thumbnail;
        this.mProfileImagePath = picturePath;
        this.mProfileImage.setImageDrawable(new RoundImageDrawable(this.mProfileThumbnail));
        String name = this.mUser.getName();
        if (TextUtils.isEmpty(name)) {
            name = this.mUser.address;
        }
        if (name.length() > 16) {
            name = name.substring(0, 14) + "...";
        }
        this.mTitle.setText(name);
    }

    public void Mesibo_onUpdateUserOnlineStatus(MesiboProfile profile, String status) {
        if (status == null) {
            this.mUserStatus.setVisibility(View.GONE);
            this.isOnlineDot.setVisibility(View.GONE);
            return;
        }
        if (!profile.isGroup() && (status.equals(getResources().getString(R.string.online_text)) ||
                status.equals(getResources().getString(R.string.online_text))) &&
                (AppUtils.isNetWorkAvailable(MesiboMessagingActivity.this))) {
            this.isOnlineDot.setVisibility(View.VISIBLE);
        }
        this.mUserStatus.setVisibility(View.VISIBLE);
        this.mUserStatus.setText(status);
    }


    public void Mesibo_onShowInContextUserInterface() {
        this.mActionMode = startSupportActionMode(this.mActionModeCallback);
    }

    public void Mesibo_onHideInContextUserInterface() {
        this.mActionMode.finish();
    }

    public void Mesibo_onContextUserInterfaceCount(int count) {
        if (this.mActionMode != null) {
            this.mActionMode.setTitle(String.valueOf(count));
            this.mActionMode.invalidate();
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [android.content.Context, com.qamp.app.messaging.MesiboMessagingActivity] */
    public void Mesibo_onError(int type, String title, String message) {
        Utils.showAlert(this, title, message);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.mFragment.Mesibo_onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        MesiboMessagingActivity.super.onActivityResult(requestCode, resultCode, data);
        if (this.mFragment != null) {
            this.mFragment.Mesibo_onActivityResult(requestCode, resultCode, data);
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        MesiboMessagingActivity.super.onResume();
        MesiboUIManager.setMessagingActivity(this);
        setProfilePicture();
    }

    @Override
    public void Mesibo_onConnectionStatus(int i) {
        Log.d("Mesibo", "Connection status: " + i);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public MesiboCall.CallProperties MesiboCall_OnIncoming(MesiboProfile var1, boolean var2) {
        MesiboCall.CallProperties cc = MesiboCall.getInstance().createCallProperties(var2);
        cc.parent = getApplicationContext();
        cc.user = var1;
        cc.className = MesiboDefaultCallActivity.class;
        return cc;
    }

    @Override
    public boolean MesiboCall_OnShowUserInterface(MesiboCall.Call var1, MesiboCall.CallProperties var2) {
        launchCustomCallActivity(var2.user.address, var2.video.enabled, true);
        return true;
    }

    @Override
    public void MesiboCall_OnError(MesiboCall.CallProperties var1, int var2) {

    }

    @Override
    public boolean MesiboCall_onNotify(int var1, MesiboProfile var2, boolean var3) {
        return false;
    }

    private class ActionModeCallback implements ActionMode.Callback {
        private final String TAG;

        private ActionModeCallback() {
            this.TAG = ActionModeCallback.class.getSimpleName();
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.clear();
            mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            menu.findItem(R.id.menu_reply).setShowAsAction(2);
            menu.findItem(R.id.menu_star).setShowAsAction(2);
            menu.findItem(R.id.menu_resend).setShowAsAction(2);
            menu.findItem(R.id.menu_copy).setShowAsAction(2);
            menu.findItem(R.id.menu_forward).setShowAsAction(2);
            menu.findItem(R.id.menu_forward).setVisible(MesiboMessagingActivity.this.mMesiboUIOptions.enableForward);
            menu.findItem(R.id.menu_forward).setEnabled(MesiboMessagingActivity.this.mMesiboUIOptions.enableForward);
            menu.findItem(R.id.menu_remove).setShowAsAction(2);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            boolean z;
            boolean z2;
            boolean z3 = false;
            if (MesiboMessagingActivity.this.mFragment == null) {
                return false;
            }
            int enabled = MesiboMessagingActivity.this.mFragment.Mesibo_onGetEnabledActionItems();
            MenuItem findItem = menu.findItem(R.id.menu_resend);
            if ((enabled & 4) > 0) {
                z = true;
            } else {
                z = false;
            }
            findItem.setVisible(z);
            MenuItem findItem2 = menu.findItem(R.id.menu_copy);
            if ((enabled & 16) > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
            findItem2.setVisible(z2);
            MenuItem findItem3 = menu.findItem(R.id.menu_reply);
            if ((enabled & 2) > 0) {
                z3 = true;
            }
            findItem3.setVisible(z3);
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int mesiboItemId = 0;
            if (item.getItemId() == R.id.menu_remove) {
                mesiboItemId = 8;
            } else if (item.getItemId() == R.id.menu_copy) {
                mesiboItemId = 16;
            } else if (item.getItemId() == R.id.menu_resend) {
                mesiboItemId = 4;
            } else if (item.getItemId() == R.id.menu_forward) {
                mesiboItemId = 1;
            } else if (item.getItemId() == R.id.menu_star) {
                mesiboItemId = 32;
            } else if (item.getItemId() == R.id.menu_reply) {
                mesiboItemId = 2;
            }
            if (mesiboItemId <= 0) {
                return false;
            }
            MesiboMessagingActivity.this.mFragment.Mesibo_onActionItemClicked(mesiboItemId);
            mode.finish();
            MesiboMessagingActivity.this.mFragment.Mesibo_onInContextUserInterfaceClosed();
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            MesiboMessagingActivity.this.mFragment.Mesibo_onInContextUserInterfaceClosed();
            ActionMode unused = MesiboMessagingActivity.this.mActionMode = null;
        }
    }

}
