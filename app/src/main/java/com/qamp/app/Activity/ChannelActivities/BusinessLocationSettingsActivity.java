package com.qamp.app.Activity.ChannelActivities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class BusinessLocationSettingsActivity extends AppCompatActivity {

    FrameLayout frameLayout;
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utilss.setLanguage(BusinessLocationSettingsActivity.this);
        setContentView(R.layout.business_location_edit_layout);
        AppUtils.setStatusBarColor(BusinessLocationSettingsActivity.this, R.color.colorAccent);
        initViews();
    }

    private void initViews() {
        frameLayout = findViewById(R.id.frameLayout);
     }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent mainActivity = new Intent(BusinessLocationSettingsActivity.this, ChannelAdminSettingsActivity.class);
        startActivity(mainActivity);
    }
}
