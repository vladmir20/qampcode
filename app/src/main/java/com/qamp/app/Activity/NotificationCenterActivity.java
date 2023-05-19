/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Fragments.ChannelNotificationCenter;
import com.qamp.app.Fragments.GroupNotificationCenter;
import com.qamp.app.R;
import com.qamp.app.Fragments.SettingNotificationCenter;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;
import com.qamp.app.MessagingModule.MesiboUserListActivityNew;

public class NotificationCenterActivity extends AppCompatActivity {

    TextView layout_title;
    LinearLayout groupLayout, channelLayout, settingsLayout;

    FrameLayout frameLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(NotificationCenterActivity.this);
        setContentView(R.layout.activity_notification_center);
        AppUtils.setStatusBarColor(NotificationCenterActivity.this, R.color.colorAccent);
        initViews();
        setFrames("GroupLayout");
        groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrames("GroupLayout");
            }
        });
        channelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrames("ChannelLayout");
            }
        });
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrames("SettingLayout");
            }
        });
    }


    private void initViews() {
        layout_title = findViewById(R.id.layout_title);
        groupLayout = findViewById(R.id.groupLayout);
        channelLayout = findViewById(R.id.channelLayout);
        settingsLayout = findViewById(R.id.settingsLayout);
        frameLayout = findViewById(R.id.frameLayout);
    }

    private void setFrames(String selectedLayout) {
        if (selectedLayout.equals("GroupLayout")) {
            groupLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            channelLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            settingsLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            GroupNotificationCenter groupNotificationCenter = new GroupNotificationCenter();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, groupNotificationCenter,selectedLayout);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (selectedLayout.equals("ChannelLayout")) {
            channelLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            groupLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            settingsLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ChannelNotificationCenter channelNotificationCenter = new ChannelNotificationCenter();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, channelNotificationCenter,selectedLayout);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (selectedLayout.equals("SettingLayout")) {
            settingsLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            groupLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            channelLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            SettingNotificationCenter settingNotificationCenter = new SettingNotificationCenter();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, settingNotificationCenter,selectedLayout);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //        if (getSupportFragmentManager().findFragmentByTag("chatLayout") != null &&
//                getSupportFragmentManager().findFragmentByTag("chatLayout").isVisible()) {
//            ChatLayoutFragment.backpressedlistener.onBackPressed();
//        }else if (getSupportFragmentManager().findFragmentByTag("feedlayout") != null &&
//                getSupportFragmentManager().findFragmentByTag("feedlayout").isVisible()) {
//            FeedLayoutFragment.backpressedlistener.onBackPressed();
//        }else if (getSupportFragmentManager().findFragmentByTag("pinlayout") != null &&
//                getSupportFragmentManager().findFragmentByTag("pinlayout").isVisible()) {
//            PinLayoutFragment.backpressedlistener.onBackPressed();
//        }else if (getSupportFragmentManager().findFragmentByTag("aboutlayout") != null &&
//                getSupportFragmentManager().findFragmentByTag("aboutlayout").isVisible()) {
//            AboutLayoutFragment.backpressedlistener.onBackPressed();
//        }else{
        Intent mainActivity = new Intent(NotificationCenterActivity.this, MesiboUserListActivityNew.class);
        startActivity(mainActivity);
//        }
    }
}
