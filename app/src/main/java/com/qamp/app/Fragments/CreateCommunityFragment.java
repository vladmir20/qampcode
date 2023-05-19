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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;


public class CreateCommunityFragment extends Fragment implements Backpressedlistener {


    Button next;
    ImageView cancel;
    TextInputEditText channelName;
    TextInputEditText channelDescr;
    TextInputEditText channelTypeBusiness;

    public static Backpressedlistener backpressedlistener;


    public CreateCommunityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.create_community, container, false);

        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.editTextTextPersonName);
        channelDescr = view.findViewById(R.id.editTextTextPersonName2);
        channelTypeBusiness = view.findViewById(R.id.businessType);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!channelName.getText().toString().isEmpty()) ||
                        (!channelDescr.getText().toString().isEmpty())||
                        (!channelTypeBusiness.getText().toString().isEmpty())) {
                    Fragment communityLocationFragment = new CommunityLocationFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ChannelName", channelName.getText().toString());
                    bundle.putString("ChannelDescription", channelDescr.getText().toString());
                    final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    communityLocationFragment.setArguments(bundle);
                    transaction.replace(R.id.frameLayout, communityLocationFragment,"CommunityLocationFragment");
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Fragment communityLocationFragment = new CommunityLocationFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ChannelName", channelName.getText().toString());
                    bundle.putString("ChannelDescription", channelDescr.getText().toString());
                    final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    communityLocationFragment.setArguments(bundle);
                    transaction.replace(R.id.frameLayout, communityLocationFragment,"CommunityLocationFragment");
                    transaction.addToBackStack(null);
                    transaction.commit();
                    Toast.makeText(getActivity(), "" + getActivity().getResources().getString(R.string.EmptychannelNameandDescription), Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
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