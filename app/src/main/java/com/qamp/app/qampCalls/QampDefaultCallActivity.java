package com.qamp.app.qampCalls;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.R;


public class QampDefaultCallActivity extends QampCallActivity  {
    private boolean mInit = false;
    MesiboCall.Call mCall = null;

    public QampDefaultCallActivity() {
    }

    public void onCreate(Bundle var1) {
//        Log.e("AdityaCall",String.valueOf(this.mCp.video.enabled));
        //super.onCreate(var1);
        setContentView(R.layout.activity_mesibocalll);//==========
        int var2=this.checkPermissions(this.mCp.video.enabled);

        if (var2 < 0) {
            this.finish();
        } else if (var2 == 0) {

            this.initCall();
        }
    }

    public void onRequestPermissionsResult(int var1, String[] var2, int[] var3) {
        super.onRequestPermissionsResult(var1, var2, var3);
        if (var3.length > 0 && var3[0] == PackageManager.PERMISSION_GRANTED) {
            this.initCall();
        } else {
            this.finish();
        }
    }

    private void initCall() {
        if (!this.mInit) {
            this.mInit = true;
            this.mCall = MesiboCall.getInstance().getActiveCall();
            if (this.mCall == null) {
                if (null == this.mCp.parent) {
                    this.mCp.parent = (QampDefaultCallActivity)this;
                }

                this.mCp.activity = this;
                this.mCall = MesiboCall.getInstance().call(this.mCp);
                if (this.mCall == null || !this.mCall.isCallInProgress()) {
                    this.finish();
                    return;
                }
            }

            super.initCall(this.mCall);
            QampMesiboDefaultFragment var1 = new QampMesiboDefaultFragment();
            if (!this.mCp.video.enabled && this.mCp.isIncoming() && this.mCall.isCallInProgress()) {
                this.mCall.isAnswered();
            }
            Log.e("MesiboCall.Csll", String.valueOf(this.mCall));
            System.out.println(this.mCall);
            var1.MesiboCall_OnSetCall(this, this.mCall);
            FragmentTransaction var2;
            (var2 = this.getSupportFragmentManager().beginTransaction()).replace(R.id.top_fragment_container, var1);//====
            var2.commitAllowingStateLoss();
        }
    }

    public void onBackPressed() {
        this.finish();
    }

    public void onResume() {
        super.onResume();
        if (this.mInit) {
            this.mCall = MesiboCall.getInstance().getActiveCall();
            if (this.mCall == null) {
                this.finish();
            }
        }
    }

    protected void onPause() {
        super.onPause();
        if (this.mInit) {
            this.mCall = MesiboCall.getInstance().getActiveCall();
        }
    }
}

