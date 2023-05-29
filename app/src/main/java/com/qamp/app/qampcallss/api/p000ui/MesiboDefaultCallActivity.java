/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 3:15 PM
 *
 */

package com.qamp.app.qampcallss.api.p000ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Activity.ShowProfileActivityNew;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.qampcallss.api.CallManager;
import com.qamp.app.qampcallss.api.MesiboCall;


public class MesiboDefaultCallActivity extends QampCallsActivity {
    MesiboCall.Call mCall = null;
    private boolean mInit = false;

    /* JADX WARNING: type inference failed for: r3v0, types: [com.mesibo.calls.api.ui.MesiboDefaultCallActivity, android.content.Context, com.mesibo.calls.api.MesiboCallActivity] */


    public void onBackPressed() {
        finish();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_mesibocalll);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
        }
        //AppUtils.setStatusBarColor(MesiboDefaultCallActivity.this, R.color.colorAccent);
        Log.e("Aditya","reached22");
        int var1 = this.checkPermissions(this.mCp.video.enabled);
        if (var1 < 0) {
            finish();
        } else if (var1 == 0) {
            initCall();
        }
    }

    private void initCall() {
        if (!this.mInit) {
            this.mInit = true;
            CallManager.getInstance().initCallStateListener();
            this.mCall = MesiboCall.getInstance().getActiveCall();
            if (this.mCall == null) {
                if (this.mCp.parent == null) {
                    this.mCp.parent = this;
                }
                this.mCp.activity = this;
                this.mCall = MesiboCall.getInstance().call(this.mCp);
                if (this.mCall == null || !this.mCall.isCallInProgress()) {
                    finish();
                    return;
                }
            }
            super.initCall(this.mCall);
            MesiboDefaultCallFragment mesiboDefaultCallFragment = new MesiboDefaultCallFragment();
            if (!this.mCp.video.enabled && this.mCp.isIncoming() && this.mCall.isCallInProgress()) {
                this.mCall.isAnswered();
            }
            mesiboDefaultCallFragment.MesiboCall_OnSetCall(this, this.mCall);
            FragmentTransaction beginTransaction = getSupportFragmentManager().beginTransaction();
            beginTransaction.replace(R.id.top_fragment_container, mesiboDefaultCallFragment);
            beginTransaction.commitAllowingStateLoss();
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        if (this.mInit) {
            this.mCall = MesiboCall.getInstance().getActiveCall();
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr.length <= 0 || iArr[0] != 0) {
            finish();
        } else {
            initCall();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mInit) {
            this.mCall = MesiboCall.getInstance().getActiveCall();
            if (this.mCall == null) {
                finish();
            }
        }
    }
}
