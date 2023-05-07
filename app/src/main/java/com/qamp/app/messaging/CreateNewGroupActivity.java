/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.messaging;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.qamp.app.R;

public class CreateNewGroupActivity extends AppCompatActivity {
    Bundle mGroupEditBundle = null;
    int mGroupMode;
    Fragment mRequestingFragment;
    FragmentTransaction ft;
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        CreateNewGroupActivity.super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        this.mGroupMode = getIntent().getIntExtra(MesiboUI.GROUP_MODE, 0);
        this.mGroupEditBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);
        Toolbar toolbar = findViewById(R.id.nugroup_toolbar);
        Utils.setActivityStyle(this, toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        String title = MesiboUI.getConfig().createGroupTitle;
        if (this.mGroupEditBundle != null) {
            title = MesiboUI.getConfig().modifyGroupTitle;
        }
        if (TextUtils.isEmpty(title)) {
            title = "";
        }
        Utils.setTitleAndColor(ab, title);
        CreateNewGroupFragment createNewGroupFragment = CreateNewGroupFragment.newInstance(this.mGroupEditBundle);
        this.mRequestingFragment = createNewGroupFragment;
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nugroup_fragment_place, createNewGroupFragment, "null");
        ft.commit();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            default:
                return CreateNewGroupActivity.super.onOptionsItemSelected(item);
        }
    }


//    @Override
//    public void onBackPressed() {
//        if(CreateNewGroupFragment.backpressedlistener!=null){
//            CreateNewGroupFragment.backpressedlistener.onBackPressed();
//            Intent intent = new Intent(CreateNewGroupActivity.this,MesiboUserListActivityNew.class);
////            ft.remove(this.mRequestingFragment).commit();
//            startActivity(intent);
//         }
//    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CreateNewGroupActivity.super.onActivityResult(requestCode, resultCode, data);
        InputMethodManager imm = (InputMethodManager) getSystemService("input_method");
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if ((fragment instanceof CreateNewGroupFragment) && fragment.isVisible()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CreateNewGroupActivity.super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (this.mRequestingFragment != null) {
            this.mRequestingFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, androidx.appcompat.app.AppCompatActivity, com.qamp.app.messaging.CreateNewGroupActivity] */
    public void onResume() {
        CreateNewGroupActivity.super.onResume();
        Mesibo.setForegroundContext(this, 2, true);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        CreateNewGroupActivity.super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

ContactsBottomSheetFragment.slectedgtoup.clear();

    }
}
