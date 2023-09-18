package com.qamp.app.Activity;


import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.CustomClasses.OnlineStatusImageView;
import com.qamp.app.Fragment.ChatFragment;
import com.qamp.app.Fragment.QampMessagingFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

public class QampMessagingActivity extends AppCompatActivity {

    FrameLayout fragment_container;
    OnlineStatusImageView imageViewProfile;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qamp_messaging);
        AppUtils.setStatusBarColor(QampMessagingActivity.this, R.color.colorAccent);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        fragment_container = findViewById(R.id.fragment_container);
        imageViewProfile.setOnlineStatus(true);
        final QampMessagingFragment qampMessagingFragment = new QampMessagingFragment();
        switchFragment(qampMessagingFragment);
    }
    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
