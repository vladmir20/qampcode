/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

package com.qamp.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Fragments.CommunitiesAdminSettingsFragment;
import com.qamp.app.Fragments.EventsAdminSettingsFragement;
import com.qamp.app.Fragments.FilesAdminSettingsFragment;
import com.qamp.app.Fragments.MediaAdminSettingsFragment;
import com.qamp.app.Fragments.PinnedLayoutAdminSettingsFragment;
import com.qamp.app.Fragments.PostLayoutAdminSettingsFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class ChannelAdminSettingsActivity extends AppCompatActivity {
    LinearLayout pinnedLayout,postLayout,mediaLayout,filesLayout,eventsLayout,communitiesLayout;
    FrameLayout frameLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utilss.setLanguage(ChannelAdminSettingsActivity.this);
        setContentView(R.layout.channel_admin_settings_activity);
        AppUtils.setStatusBarColor(ChannelAdminSettingsActivity.this, R.color.colorAccent);
        initViews();
        setFrame("postLayout");
        pinnedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("pinnedLayout");
            }
        });
        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("postLayout");
            }
        });
        mediaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("mediaLayout");
            }
        });
        filesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("filesLayout");
            }
        });
        eventsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("eventsLayout");
            }
        });
        communitiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("communitiesLayout");
            }
        });
    }

    private void initViews() {
        pinnedLayout = findViewById(R.id.pinnedLayout);
        postLayout = findViewById(R.id.postLayout);
        mediaLayout = findViewById(R.id.mediaLayout);
        filesLayout = findViewById(R.id.filesLayout);
        eventsLayout = findViewById(R.id.eventsLayout);
        communitiesLayout = findViewById(R.id.communitiesLayout);
        frameLayout = findViewById(R.id.frameLayout);
    }

    private void setFrame(String layout) {
        if (layout.equals("pinnedLayout")){
            pinnedLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            postLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mediaLayout.setBackgroundColor(getResources().getColor(R.color.white));
            filesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            eventsLayout.setBackgroundColor(getResources().getColor(R.color.white));
            communitiesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            PinnedLayoutAdminSettingsFragment pinnedLayoutAdminSettingsFragment = new PinnedLayoutAdminSettingsFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, pinnedLayoutAdminSettingsFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (layout.equals("postLayout")) {
            postLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            pinnedLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mediaLayout.setBackgroundColor(getResources().getColor(R.color.white));
            filesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            eventsLayout.setBackgroundColor(getResources().getColor(R.color.white));
            communitiesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            PostLayoutAdminSettingsFragment postLayoutAdminSettingsFragment = new PostLayoutAdminSettingsFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, postLayoutAdminSettingsFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("mediaLayout")) {
            mediaLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            pinnedLayout.setBackgroundColor(getResources().getColor(R.color.white));
            postLayout.setBackgroundColor(getResources().getColor(R.color.white));
            filesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            eventsLayout.setBackgroundColor(getResources().getColor(R.color.white));
            communitiesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            MediaAdminSettingsFragment mediaAdminSettingsFragment = new MediaAdminSettingsFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, mediaAdminSettingsFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("filesLayout")) {
            filesLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            pinnedLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mediaLayout.setBackgroundColor(getResources().getColor(R.color.white));
            postLayout.setBackgroundColor(getResources().getColor(R.color.white));
            eventsLayout.setBackgroundColor(getResources().getColor(R.color.white));
            communitiesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            FilesAdminSettingsFragment filesAdminSettingsFragment = new FilesAdminSettingsFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, filesAdminSettingsFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("eventsLayout")) {
            eventsLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            pinnedLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mediaLayout.setBackgroundColor(getResources().getColor(R.color.white));
            postLayout.setBackgroundColor(getResources().getColor(R.color.white));
            filesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            communitiesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            EventsAdminSettingsFragement eventsAdminSettingsFragement = new EventsAdminSettingsFragement();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, eventsAdminSettingsFragement,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("communitiesLayout")) {
            communitiesLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            pinnedLayout.setBackgroundColor(getResources().getColor(R.color.white));
            mediaLayout.setBackgroundColor(getResources().getColor(R.color.white));
            postLayout.setBackgroundColor(getResources().getColor(R.color.white));
            eventsLayout.setBackgroundColor(getResources().getColor(R.color.white));
            filesLayout.setBackgroundColor(getResources().getColor(R.color.white));
            CommunitiesAdminSettingsFragment communitiesAdminSettingsFragment = new CommunitiesAdminSettingsFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, communitiesAdminSettingsFragment,layout);
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
        Intent mainActivity = new Intent(ChannelAdminSettingsActivity.this, CommunityDashboard.class);
        startActivity(mainActivity);
//        }
    }
}
