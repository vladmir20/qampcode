/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;


public class CreateCommunityThree extends Fragment implements Backpressedlistener {

    Button next;
    ImageView cancel;
    TextView channelName;
    String channelTitle, lat, lng, channelDescription;

    CardView openToAllLayout,byInviteLayout,byRequestLayout;
    TextView openToAllDes,byInviteDes,byRequestDes;
    CheckBox openToAllLayoutCheck,byInviteCheck,byRequestCheck;
    public static Backpressedlistener backpressedlistener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_three, container, false);

        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);

        openToAllLayout = view.findViewById(R.id.openToAllLayout);
        byInviteLayout = view.findViewById(R.id.byInviteLayout);
        byRequestLayout = view.findViewById(R.id.byRequestLayout);

        openToAllDes = view.findViewById(R.id.openToAllDes);
        byInviteDes = view.findViewById(R.id.byInviteDes);
        byRequestDes = view.findViewById(R.id.byRequestDes);

        openToAllLayoutCheck = view.findViewById(R.id.openToAllLayoutCheck);
        byInviteCheck = view.findViewById(R.id.byInviteCheck);
        byRequestCheck = view.findViewById(R.id.byRequestCheck);

        setCommunityPermissionLayout("openToAllLayout",view);

        openToAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommunityPermissionLayout("openToAllLayout",view);
            }
        });

        byInviteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommunityPermissionLayout("byInviteLayout",view);
            }
        });

        byRequestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommunityPermissionLayout("byRequestLayout",view);
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            channelTitle = bundle.getString("ChannlName");
            lat = bundle.getString("Latitude");
            lng = bundle.getString("Longitude");
            channelDescription = bundle.getString("ChannelDescription");
            channelName.setText(channelTitle);
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createCommunityFour = new CreateCommunityFour();
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle1 = new Bundle();
                bundle1.putString("ChannelName",channelTitle);
                bundle1.putString("Latitude",lat);
                bundle1.putString("Longitude",lng);
                bundle1.putString("ChannelDescription", channelDescription);
                createCommunityFour.setArguments(bundle1);
                transaction.replace(R.id.frameLayout, createCommunityFour);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        return view;
    }

    private void setCommunityPermissionLayout(String string, View view) {
        if (string.equals("openToAllLayout")){
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setChecked(true);
            openToAllLayoutCheck.setVisibility(View.VISIBLE);
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteCheck.setVisibility(View.GONE);
            byInviteDes.setText("Closed community where people with invitation can join");
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestCheck.setVisibility(View.GONE);
            byRequestDes.setText("Join request needs to be approved by community manger");
        } else if (string.equals("byInviteLayout")) {
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            byInviteCheck.setChecked(true);
            byInviteCheck.setVisibility(View.VISIBLE);
            byInviteDes.setText("Closed community where people with invitation \ncan join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            openToAllLayoutCheck.setVisibility(View.GONE);
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestCheck.setVisibility(View.GONE);
            byRequestDes.setText("Join request needs to be approved by community manger");
        }else if (string.equals("byRequestLayout")) {
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            byRequestCheck.setChecked(true);
            byRequestCheck.setVisibility(View.VISIBLE);
            byRequestDes.setText("Join request needs to be approved by community \nmanger");
            byInviteDes.setText("Closed community where people with invitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            openToAllLayoutCheck.setVisibility(View.GONE);
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteCheck.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
     }
    @Override
    public void onPause() {
        backpressedlistener=null;
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener=this;
    }
}