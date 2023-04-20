/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.mesibo.mediapicker.MediaPicker;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.messaging.MesiboUserListActivityNew;

public class EditProfileActivityNew extends AppCompatActivity implements MediaPicker.ImageEditorListener, View.OnClickListener {

    Fragment mRequestingFragment;
    private Button saveButton;
    OnItemTapClickListener searchListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(EditProfileActivityNew.this);
        setContentView(R.layout.activity_edit_profile_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppUtils.setActivityStyle(this, toolbar);
        AppUtils.setStatusBarColor(EditProfileActivityNew.this, R.color.colorAccent);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        Bundle args = getIntent().getExtras();
        if (null == args) {
            return;
        }

        saveButton = (Button) findViewById(R.id.register_profile_save);
        saveButton.setOnClickListener(this);

        long groupid = args.getLong("groupid", 0);
        boolean launchMesibo = args.getBoolean("launchMesibo", false);

        ProfileFragment registerNewProfileFragment = new ProfileFragment();
        registerNewProfileFragment.setGroupId(groupid);
        registerNewProfileFragment.setLaunchMesibo(launchMesibo);
        mRequestingFragment = registerNewProfileFragment;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.register_new_profile_fragment_place, registerNewProfileFragment, "null");
        ft.addToBackStack("registerNewProfileFragment");
//        searchListener = (OnItemTapClickListener) mRequestingFragment;
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilss.setLanguage(EditProfileActivityNew.this);
        Mesibo.setForegroundContext(this, 0x101, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Mesibo.setForegroundContext(this, 0x101, false);
    }

    @Override
    public void onBackPressed() {
        EditProfileActivityNew.this.finish();
        startActivity(new Intent(EditProfileActivityNew.this, MesiboUserListActivityNew.class));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof ProfileFragment && fragment.isVisible())
                fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mRequestingFragment != null)
            mRequestingFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onImageEdit(int i, String s, String filePath, Bitmap bitmap, int status) {
        if (0 != status) {
            return;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_profile_save:
                searchListener.OnSearchClick();
                break;
        }
    }

}


