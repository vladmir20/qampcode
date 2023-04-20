/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app;

import static com.qamp.app.messaging.MesiboConfiguration.CAMERA_VALUE;
import static com.qamp.app.messaging.MesiboConfiguration.GALLERY_VALUE;
import static com.qamp.app.messaging.MesiboConfiguration.REMOVE_VALUE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProfileBottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private LinearLayout cameraButton, galleryButton;
    private ImageView removeButton;
    private LinearLayout delete_layout;
    private boolean isPhoto;

    public ProfileBottomSheetFragment() {
        // Required empty public constructor
    }

    public ProfileBottomSheetFragment(boolean isPhoto) {
        this.isPhoto = isPhoto;
    }


    public static ProfileBottomSheetFragment newInstance(String param1, String param2) {
        ProfileBottomSheetFragment fragment = new ProfileBottomSheetFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_bottom_sheet, container, false);
        Utilss.setLanguage(getActivity());
        cameraButton = (LinearLayout) v.findViewById(R.id.camera_container);
        galleryButton = (LinearLayout) v.findViewById(R.id.gallery_container);
        removeButton = (ImageView) v.findViewById(R.id.remove_button);
        delete_layout = (LinearLayout) v.findViewById(R.id.delete_layout);

        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
        delete_layout.setVisibility(View.VISIBLE);

        if (isPhoto) {
            delete_layout.setVisibility(View.VISIBLE);
        } else {
            delete_layout.setVisibility(View.GONE);
        }
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        Utilss.setLanguage(getActivity());
    }
    @Override
    public void onStart() {
        super.onStart();
        Utilss.setLanguage(getActivity());
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent();

        switch (view.getId()) {
            case R.id.camera_container:
                intent.putExtra("buttonTapped", CAMERA_VALUE);
                this.dismiss();
                ((ProfileFragment)getParentFragmentManager().getFragments().get(0)).openCamera();
                break;
            case R.id.gallery_container:
                intent.putExtra("buttonTapped", GALLERY_VALUE);
                this.dismiss();
                ((ProfileFragment)getParentFragmentManager().getFragments().get(0)).openGallery();
                break;
            case R.id.remove_button:
                intent.putExtra("buttonTapped", REMOVE_VALUE);
                this.dismiss();
                ((ProfileFragment) getParentFragmentManager().getFragments().get(0)).removeProfilePic();

                break;
        }
    }
}