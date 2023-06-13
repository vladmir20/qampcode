/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.util.Util;
import com.google.android.material.textfield.TextInputEditText;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.Utils;


public class CreateCommunityFragment extends Fragment implements Backpressedlistener {


    Button next,skip;
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
        initViews(view);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!channelName.getText().toString().isEmpty()) &&
                        (!channelDescr.getText().toString().isEmpty())&&
                        (!channelTypeBusiness.getText().toString().isEmpty())) {
                    Fragment communityLocationFragment = new CommunityLocationFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ChannelName", channelName.getText().toString());
                    bundle.putString("ChannelDescription", channelDescr.getText().toString());
                    bundle.putString("ChannelBusinessType", channelTypeBusiness.getText().toString());
                    bundle.putString("ButtonState", "false");
                    final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    communityLocationFragment.setArguments(bundle);
                    transaction.replace(R.id.frameLayout, communityLocationFragment,"CommunityLocationFragment");
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), "" + getActivity().getResources().getString(R.string.EmptychannelNameandDescription), Toast.LENGTH_SHORT).show();
                }

            }
        });

        channelName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (channelName.getText().toString().isEmpty()||channelTypeBusiness.getText().toString().isEmpty())
                    Utils.setButtonState(next,false);
                else
                    Utils.setButtonState(next,true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        channelTypeBusiness.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (channelName.getText().toString().isEmpty()||channelTypeBusiness.getText().toString().isEmpty())
                    Utils.setButtonState(next,false);
                else
                    Utils.setButtonState(next,true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void initViews(View view) {
        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        skip = view.findViewById(R.id.skip);
        channelName = view.findViewById(R.id.editTextTextPersonName);
        channelDescr = view.findViewById(R.id.editTextTextPersonName2);
        channelTypeBusiness = view.findViewById(R.id.businessType);
        Utils.setButtonState(next,false);
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