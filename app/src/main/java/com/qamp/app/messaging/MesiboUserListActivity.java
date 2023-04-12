package com.qamp.app.messaging;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;
import com.qamp.app.R;

public class MesiboUserListActivity extends AppCompatActivity implements MesiboUserListFragment.FragmentListener {
    public static final String TAG = "MesiboMainActivity";
    TextView contactsSubTitle = null;
    TextView contactsTitle = null;
    Bundle mEditGroupBundle = null;
    long mForwardId = 0;
    long[] mForwardIds;
    boolean mHideHomeBtn = false;
    boolean mKeepRunning = false;
    MesiboUI.Config mMesiboUIOptions = null;
    int mMode = 0;

    /* JADX WARNING: type inference failed for: r14v0, types: [android.content.Context, com.qamp.app.messaging.MesiboUserListActivity, com.qamp.app.messaging.MesiboUserListFragment$FragmentListener, androidx.appcompat.app.AppCompatActivity] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MesiboUserListActivity.super.onCreate(savedInstanceState);
        if (!Mesibo.isReady()) {
            finish();
            return;
        }
        MesiboImages.init(this);
        this.mMode = getIntent().getIntExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_MESSAGELIST);
        this.mForwardId = getIntent().getLongExtra("mid", 0);
        this.mForwardIds = getIntent().getLongArrayExtra(MesiboUI.MESSAGE_IDS);
        String forwardMessage = getIntent().getStringExtra("message");
        boolean forwardAndClose = getIntent().getBooleanExtra(MesiboUI.FORWARD_AND_CLOSE, false);
        this.mKeepRunning = getIntent().getBooleanExtra(MesiboUI.KEEP_RUNNING, false);
        if (getIntent().getBooleanExtra(MesiboUI.START_IN_BACKGROUND, false)) {
            moveTaskToBack(true);
        }
        if (this.mMode == MesiboUserListFragment.MODE_EDITGROUP) {
            this.mEditGroupBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);
        }
        MesiboUIManager.enableSecureScreen(this);
        setContentView(R.layout.activity_messages);
        this.mMesiboUIOptions = MesiboUI.getConfig();
        Toolbar toolbar = findViewById(R.id.message_toolbar);
        this.contactsSubTitle = (TextView) findViewById(R.id.contacts_subtitle);
        this.contactsTitle = (TextView) findViewById(R.id.contacts_title);
        Utils.setTextViewColor(this.contactsTitle, MesiboUI.getConfig().mToolbarTextColor);
        Utils.setTextViewColor(this.contactsSubTitle, MesiboUI.getConfig().mToolbarTextColor);
        Utils.setActivityStyle(this, toolbar);
        setSupportActionBar(toolbar);
        Utils.setActivityStyle(this, toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = this.mMesiboUIOptions.messageListTitle;
        if (TextUtils.isEmpty(title)) {
            title = Mesibo.getAppName();
        }
        this.contactsTitle.setText(title);
        if (this.mMode == MesiboUserListFragment.MODE_MESSAGELIST) {
            this.contactsSubTitle.setText(this.mMesiboUIOptions.offlineIndicationTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(this.mMesiboUIOptions.enableBackButton);
        }
        if (savedInstanceState == null) {
            UserListFragment userListFragment = new UserListFragment();
            userListFragment.setListener(this);
            Bundle bl = new Bundle();
            bl.putInt(MesiboUserListFragment.MESSAGE_LIST_MODE, this.mMode);
            bl.putLong("mid", this.mForwardId);
            if (!TextUtils.isEmpty(forwardMessage)) {
                bl.putString("message", forwardMessage);
            }
            bl.putLongArray(MesiboUI.MESSAGE_IDS, this.mForwardIds);
            if (this.mMode == MesiboUserListFragment.MODE_EDITGROUP) {
                bl.putBundle(MesiboUI.BUNDLE, this.mEditGroupBundle);
            }
            bl.putBoolean(MesiboUI.FORWARD_AND_CLOSE, forwardAndClose);
            userListFragment.setArguments(bl);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.userlist_fragment, userListFragment, "null");
            ft.addToBackStack("userListFragment");
            ft.commit();
        }
    }

    public void Mesibo_onUpdateTitle(String title) {
        this.contactsTitle.setText(title);
    }

    public void Mesibo_onUpdateSubTitle(String title) {
        if (title == null) {
            this.contactsSubTitle.setVisibility(8);
            return;
        }
        this.contactsSubTitle.setVisibility(0);
        this.contactsSubTitle.setText(title);
    }

    public boolean Mesibo_onClickUser(String address, long groupid, long forwardid) {
        return false;
    }

    public boolean Mesibo_onUserListFilter(MesiboMessage params) {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            default:
                return MesiboUserListActivity.super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (this.mKeepRunning) {
            moveTaskToBack(true);
            Toast.makeText(this, "this", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "that", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.qamp.app.messaging.MesiboUserListActivity, androidx.appcompat.app.AppCompatActivity] */
    public void onResume() {
        MesiboUserListActivity.super.onResume();
        Mesibo.setForegroundContext(this, 0, true);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        MesiboUserListActivity.super.onPause();
    }
}
