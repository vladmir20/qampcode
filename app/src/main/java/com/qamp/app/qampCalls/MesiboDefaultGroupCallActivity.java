package com.qamp.app.qampCalls;


import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.R;


public class MesiboDefaultGroupCallActivity extends QampCallActivity {
    private boolean mInit = false;
    private long mGid = 0L;
    private boolean mVideo = true;
    private boolean mAudio = true;
    private MesiboDefaultGroupCallFragment mFragment = null;
    private boolean mShowDemoNotice = false;

    public MesiboDefaultGroupCallActivity() {
    }

    public void onCreate(Bundle var1) {
        this.setGroupCallActivity();
        super.onCreate(var1);
        if ((var1 = this.getIntent().getExtras()) != null) {
            this.mGid = var1.getLong("gid");
            this.mAudio = var1.getBoolean("audio", true);
            this.mVideo = var1.getBoolean("video", true);
        }

        if (this.mGid < 1000L) {
            this.finish();
        } else {
            this.setContentView(R.layout.activity_mesibocalll); //=========
            int var2;
            if ((var2 = this.checkPermissions(true)) < 0) {
                this.finish();
            } else if (var2 <= 0) {
                this.initCall();
            }
        }
    }

    public void onRequestPermissionsResult(int var1, String[] var2, int[] var3) {
        super.onRequestPermissionsResult(var1, var2, var3);
        if (var3.length > 0 && var3[0] == 0) {
            this.initCall();
        } else {
            this.finish();
        }
    }

    public void initCall() {
        if (!this.mInit) {
            this.mInit = true;
            this.mFragment = new MesiboDefaultGroupCallFragment();
//            this.mFragment.setGroup(this.mGid, this.mVideo, this.mAudio);
            FragmentTransaction var1;
            (var1 = this.getSupportFragmentManager().beginTransaction()).replace(R.id.top_fragment_container, this.mFragment); //==========
            var1.commitAllowingStateLoss();//============
        }
    }

    public void onBackPressed() {
        this.mFragment.onBackPressed();
        this.finish();
    }
}

