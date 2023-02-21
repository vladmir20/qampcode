package com.qamp.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Utils.AppUtils;

public class ShowProfileActivityNew extends AppCompatActivity implements MesiboProfile.Listener {

    ImageView mUsermageView;
    MesiboProfile mUserProfile;

    long mGroupId = 0;
    String mPeer = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_profile_new);
        AppUtils.setStatusBarColor(ShowProfileActivityNew.this, R.color.colorAccent);
        Bundle args = getIntent().getExtras();
        if (null == args) {
            return;
        }

        mPeer = args.getString("peer");
        mGroupId = args.getLong("groupid");

        mUserProfile = null;

        if (mGroupId > 0) {
            mUserProfile = Mesibo.getProfile(mGroupId);
        } else {
            mUserProfile = Mesibo.getProfile(mPeer);
        }

        mUserProfile.addListener(ShowProfileActivityNew.this);

        mUsermageView = (ImageView) findViewById(R.id.up_image_profile);

        Mesibo.addListener(this);

        mUsermageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIManager.launchImageViewer(ShowProfileActivityNew.this, mUserProfile.getImageOrThumbnailPath());
            }

        });

        TextView userName = (TextView) findViewById(R.id.up_user_name);
        TextView userstatus = (TextView) findViewById(R.id.up_current_status);

        userName.setText(mUserProfile.getName());
        long lastSeen = mUserProfile.getLastSeen();
        userstatus.setVisibility(View.VISIBLE);
        if (0 == lastSeen) {
            userstatus.setText("Online");
        } else if (lastSeen < 0) {
            // never seen or group
            userstatus.setVisibility(View.GONE);
        } else {
            String seenStatus = "";
            if (lastSeen >= 2 * 3600 * 24) {
                seenStatus = (int) (lastSeen / (3600 * 24)) + " days ago";
            } else if (lastSeen >= 24 * 3600) {
                seenStatus = "yesterday";
            } else if (lastSeen >= 2 * 3600) {
                seenStatus = (int) (lastSeen / (3600)) + " hours ago";
            } else if (lastSeen >= 3600) {
                seenStatus = "an hour ago";
            } else if (lastSeen >= 120) {
                seenStatus = (int) (lastSeen / 60) + " minutes ago";
            } else {
                seenStatus = "a few moments ago";
            }

            userstatus.setText("Last seen " + seenStatus);
        }


    }

    private void setUserPicture() {
        Bitmap b = mUserProfile.getImageOrThumbnail();
        if (null != b) {
            mUsermageView.setImageBitmap(b);
        }
    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile userProfile) {

        if (Mesibo.isUiThread()) {
            setUserPicture();
            return;
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                setUserPicture();
            }
        });
    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    @Override
    public void onResume() {
        super.onResume();

        Mesibo.setForegroundContext(this, 0x102, true);

        if (mUserProfile.groupid > 0) {
            TextView userName = (TextView) findViewById(R.id.up_user_name);
            if (null != mUserProfile.getName())
                userName.setText(mUserProfile.getName());
        }

        setUserPicture();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.setForegroundContext(this, 0x102, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Mesibo.setForegroundContext(this, 0x102, false);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
