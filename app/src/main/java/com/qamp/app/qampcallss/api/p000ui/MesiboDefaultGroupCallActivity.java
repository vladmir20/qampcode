package com.qamp.app.qampcallss.api.p000ui;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.mesibo.calls.api.CallManager;
import com.mesibo.calls.api.MesiboCallActivity;
import com.mesibo.calls.api.PeerConnectionClient;
import com.mesibo.calls.api.R;

/* renamed from: com.mesibo.calls.api.ui.MesiboDefaultGroupCallActivity */
public class MesiboDefaultGroupCallActivity extends MesiboCallActivity {
    private boolean mAudio = true;
    private MesiboDefaultGroupCallFragment mFragment = null;
    private long mGid = 0;
    private boolean mInit = false;
    private boolean mShowDemoNotice = false;
    private boolean mVideo = true;

    public void initCall() {
        if (!this.mInit) {
            this.mInit = true;
            CallManager.getInstance().initCallStateListener();
            this.mFragment = new MesiboDefaultGroupCallFragment();
            this.mFragment.setGroup(this.mGid, this.mVideo, this.mAudio);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.top_fragment_container, this.mFragment);
            beginTransaction.commitAllowingStateLoss();
        }
    }

    public void onBackPressed() {
        this.mFragment.onBackPressed();
        finish();
    }

    public void onCreate(Bundle bundle) {
        setGroupCallActivity();
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.mGid = extras.getLong("gid");
            this.mAudio = extras.getBoolean("audio", true);
            this.mVideo = extras.getBoolean(PeerConnectionClient.VIDEO_TRACK_TYPE, true);
        }
        if (this.mGid < 1000) {
            finish();
            return;
        }
        setContentView(R.layout.activity_mesibocall);
        int checkPermissions = checkPermissions(true);
        if (checkPermissions < 0) {
            finish();
        } else if (checkPermissions <= 0) {
            initCall();
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (iArr.length <= 0 || iArr[0] != 0) {
            finish();
        } else {
            initCall();
        }
    }
}
