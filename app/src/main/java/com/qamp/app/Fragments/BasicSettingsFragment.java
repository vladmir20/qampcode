/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */
package com.qamp.app.Fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.emojiview.EmojiconTextView;
import com.qamp.app.Activity.SettingsActivity;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.R;
import com.qamp.app.MesiboApiClasses.SampleAPI;
import com.qamp.app.MessagingModule.RoundImageDrawable;


public class BasicSettingsFragment extends Fragment {


    private EmojiconTextView mUserName;
    private EmojiconTextView mUserStatus;
    private ImageView mUserImage;
    private MesiboProfile mUser = Mesibo.getSelfProfile();

    public BasicSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_basic_settings, container, false);
        final ActionBar ab = ((AppCompatActivity) (getActivity())).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Settings");

        if (null == mUser) {
            getActivity().finish();
            //TBD show alert
            return v;
        }

        mUserName = (EmojiconTextView) v.findViewById(R.id.set_self_user_name);
        mUserStatus = (EmojiconTextView) v.findViewById(R.id.set_self_status);
        mUserImage = (ImageView) v.findViewById(R.id.set_user_image);


        LinearLayout profileLayout = (LinearLayout) v.findViewById(R.id.set_picture_name_status_layout);
        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditProfileFragment RegFragment = new EditProfileFragment();
                ((SettingsActivity) getActivity()).setRequestingFragment(RegFragment);
                RegFragment.activateInSettingsMode();
                FragmentManager fm = ((AppCompatActivity) (getActivity())).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.settings_fragment_place, RegFragment, "null");
                ft.addToBackStack("profile");
                ft.commit();

            }
        });

        LinearLayout DataUsageLayout = (LinearLayout) v.findViewById(R.id.set_data_layout);
        DataUsageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUsageFragment dataFragment = new DataUsageFragment();
                FragmentManager fm = ((AppCompatActivity) (getActivity())).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.settings_fragment_place, dataFragment, "null");
                ft.addToBackStack("datausage");
                ft.commit();

            }
        });

        LinearLayout aboutLayout = (LinearLayout) v.findViewById(R.id.set_about_layout);
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutFragment aboutFragment = new AboutFragment();
                FragmentManager fm = ((AppCompatActivity) (getActivity())).getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.settings_fragment_place, aboutFragment, "null");
                ft.addToBackStack("about");
                ft.commit();

            }
        });

        LinearLayout logoutLayout = (LinearLayout) v.findViewById(R.id.set_logout_layout);
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConfig.getConfig().reset();
                SampleAPI.startLogout();
                getActivity().finish();
            }
        });

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        mUser = Mesibo.getSelfProfile();
        Bitmap image = mUser.getImageOrThumbnail();
        if (null != image) {
            mUserImage.setImageDrawable(new RoundImageDrawable(image));
        }

        mUserName.setText(mUser.getName());

        if (!TextUtils.isEmpty(mUser.getStatus())) {
            mUserStatus.setText(mUser.getStatus());
        } else {
            mUserStatus.setText("");
        }
    }

}
