package com.qamp.app.messaging;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;

public class MesiboMessagingActivity extends AppCompatActivity implements MesiboMessagingFragment.FragmentListener {
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
    MessagingFragment mFragment = null;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private ImageView mProfileImage = null;
    private Bitmap mProfileThumbnail = null;
    private TextView mTitle = null;
    private Toolbar mToolbar = null;
    private TextView mUserStatus = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MesiboMessagingActivity.super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
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
                Toast.makeText(this, "" + groupId, Toast.LENGTH_SHORT).show();
            } else {
                this.mUser = Mesibo.getProfile(peer);
                Toast.makeText(this, "" + groupId, Toast.LENGTH_SHORT).show();
            }
            if (this.mUser == null) {
                finish();
                 return;
            }
            MesiboUIManager.enableSecureScreen(this);
            setContentView(R.layout.activity_messaging_new);
            this.mToolbar = findViewById(R.id.toolbar);
            Utils.setActivityStyle(this, this.mToolbar);
        //    setSupportActionBar(this.mToolbar);
            ActionBar supportActionBar = getSupportActionBar();
            findViewById(R.id.chat_back).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    MesiboMessagingActivity.this.onBackPressed();
                }
            });
            this.mUserStatus = (TextView) findViewById(R.id.chat_profile_subtitle);
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
    }

    private void setProfilePicture() {
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
            //  Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
            MesiboMessagingActivity.super.onBackPressed();
        }
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
            this.mUserStatus.setVisibility(8);
            return;
        }
        this.mUserStatus.setVisibility(0);
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
