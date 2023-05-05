/*
 * *
 *  * Created by Shivam Tiwari on 30/04/23, 4:13 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 30/04/23, 4:13 PM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Utils.AppUtils;

public class CommunityDashboard extends AppCompatActivity {

    LinearLayout chatLayout,feedlayout,pinlayout,aboutlayout;
    FrameLayout frameLayout;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(CommunityDashboard.this);
        setContentView(R.layout.activity_community_dashboard);
        AppUtils.setStatusBarColor(CommunityDashboard.this, R.color.grey_5);
        initViews();
        setFrame("chatLayout");
        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("chatLayout");
            }
        });
        feedlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("feedlayout");
            }
        });
        pinlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("pinlayout");
            }
        });
        aboutlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFrame("aboutlayout");
            }
        });
    }


    private void initViews() {
        chatLayout = findViewById(R.id.chatLayout);
        feedlayout = findViewById(R.id.feedlayout);
        pinlayout = findViewById(R.id.pinlayout);
        aboutlayout = findViewById(R.id.aboutlayout);
        frameLayout = findViewById(R.id.frameLayout);
    }

    private void setFrame(String layout) {
        if (layout.equals("chatLayout")){
            chatLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            feedlayout.setBackgroundColor(getResources().getColor(R.color.white));
            pinlayout.setBackgroundColor(getResources().getColor(R.color.white));
            aboutlayout.setBackgroundColor(getResources().getColor(R.color.white));
            ChatLayoutFragment chatLayoutFragment = new ChatLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, chatLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (layout.equals("feedlayout")) {
            feedlayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.white));
            pinlayout.setBackgroundColor(getResources().getColor(R.color.white));
            aboutlayout.setBackgroundColor(getResources().getColor(R.color.white));
            FeedLayoutFragment feedLayoutFragment = new FeedLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, feedLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("pinlayout")) {
            pinlayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.white));
            feedlayout.setBackgroundColor(getResources().getColor(R.color.white));
            aboutlayout.setBackgroundColor(getResources().getColor(R.color.white));
            PinLayoutFragment pinLayoutFragment = new PinLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, pinLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("aboutlayout")) {
            aboutlayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.white));
            feedlayout.setBackgroundColor(getResources().getColor(R.color.white));
            pinlayout.setBackgroundColor(getResources().getColor(R.color.white));
            AboutLayoutFragment aboutLayoutFragment = new AboutLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayout, aboutLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

}
