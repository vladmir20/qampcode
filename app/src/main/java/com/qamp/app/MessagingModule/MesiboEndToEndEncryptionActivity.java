/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.R;

public class MesiboEndToEndEncryptionActivity extends AppCompatActivity {
    MesiboEndToEndEncryptionFragment mFragment;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        MesiboProfile profile;
        MesiboEndToEndEncryptionActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e2e_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Utils.setActivityStyle(this, toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            String peer = args.getString(MesiboUI.PEER);
            long groupid = args.getLong(MesiboUI.GROUP_ID);
            TextView st = (TextView) findViewById(R.id.e2e_subtitle);
            if (!TextUtils.isEmpty(peer) || groupid > 0) {
                profile = Mesibo.getProfile(peer, groupid);
                st.setText("You and " + profile.getFirstNameOrAddress("+"));
            } else {
                st.setVisibility(8);
                profile = Mesibo.getSelfProfile();
            }
            if (profile == null) {
                finish();
                return;
            }
            Utils.setTextViewColor(st, MesiboUI.getConfig().mToolbarTextColor);
            String title = MesiboUI.getConfig().e2eeTitle;
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            Utils.setTitleAndColor(ab, title);
            this.mFragment = new MesiboEndToEndEncryptionFragment();
            this.mFragment.setProfile(profile);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_place, this.mFragment, "null");
            ft.commit();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (this.mFragment.Mesibo_onBackPressed()) {
            return MesiboEndToEndEncryptionActivity.super.onOptionsItemSelected(item);
        }
        finish();
        return true;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.mFragment.Mesibo_onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        if (!this.mFragment.Mesibo_onBackPressed()) {
            MesiboEndToEndEncryptionActivity.super.onBackPressed();
        }
    }
}
