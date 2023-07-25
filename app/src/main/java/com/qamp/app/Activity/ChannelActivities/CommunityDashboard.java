package com.qamp.app.Activity.ChannelActivities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Fragments.FeedLayoutFragment;
import com.qamp.app.Fragments.AboutLayoutFragment;
import com.qamp.app.Fragments.ChatLayoutFragment;
import com.qamp.app.Fragments.PinLayoutFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;
import com.qamp.app.MessagingModule.MesiboUserListActivityNew;

public class CommunityDashboard extends AppCompatActivity {

    LinearLayout chatLayout,feedlayout,pinlayout,aboutlayout;
    FrameLayout frameLayout;
    LinearLayout alertLayout;
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
        alertLayout = findViewById(R.id.alertLayout);
    }

    private void setFrame(String layout) {
        if (layout.equals("chatLayout")){
            chatLayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            feedlayout.setBackgroundColor(getResources().getColor(R.color.white));
            pinlayout.setBackgroundColor(getResources().getColor(R.color.white));
            aboutlayout.setBackgroundColor(getResources().getColor(R.color.white));
            alertLayout.getBackground().setColorFilter(Color.parseColor("#76CA66"), PorterDuff.Mode.SRC_ATOP);
            ChatLayoutFragment chatLayoutFragment = new ChatLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, chatLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (layout.equals("feedlayout")) {
            feedlayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.white));
            pinlayout.setBackgroundColor(getResources().getColor(R.color.white));
            aboutlayout.setBackgroundColor(getResources().getColor(R.color.white));
            alertLayout.getBackground().setColorFilter(Color.parseColor("#76CA66"), PorterDuff.Mode.SRC_ATOP);
            FeedLayoutFragment feedLayoutFragment = new FeedLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, feedLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("pinlayout")) {
            pinlayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.white));
            feedlayout.setBackgroundColor(getResources().getColor(R.color.white));
            aboutlayout.setBackgroundColor(getResources().getColor(R.color.white));
            alertLayout.getBackground().setColorFilter(Color.parseColor("#FE5858"), PorterDuff.Mode.SRC_ATOP);
            PinLayoutFragment pinLayoutFragment = new PinLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, pinLayoutFragment,layout);
            transaction.addToBackStack(null);
            transaction.commit();
        }else if (layout.equals("aboutlayout")) {
            aboutlayout.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));
            chatLayout.setBackgroundColor(getResources().getColor(R.color.white));
            feedlayout.setBackgroundColor(getResources().getColor(R.color.white));
            pinlayout.setBackgroundColor(getResources().getColor(R.color.white));
            alertLayout.getBackground().setColorFilter(Color.parseColor("#FE5858"), PorterDuff.Mode.SRC_ATOP);
            AboutLayoutFragment aboutLayoutFragment = new AboutLayoutFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.no_animation);
            transaction.replace(R.id.frameLayout, aboutLayoutFragment,layout);
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
        Intent mainActivity = new Intent(CommunityDashboard.this, MesiboUserListActivityNew.class);
        startActivity(mainActivity);
        // }
    }

}