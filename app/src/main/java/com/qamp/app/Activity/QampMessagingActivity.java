package com.qamp.app.Activity;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qamp.app.CustomClasses.OnlineStatusImageView;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

public class QampMessagingActivity extends AppCompatActivity {

    OnlineStatusImageView imageViewProfile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qamp_messaging);
        AppUtils.setStatusBarColor(QampMessagingActivity.this, R.color.colorAccent);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        imageViewProfile.setOnlineStatus(true);
    }
}
