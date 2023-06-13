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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.util.Util;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.textfield.TextInputLayout;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.Utils;

public class CommunityLocationFragment extends Fragment implements OnMapReadyCallback, Backpressedlistener {

    Button next;
    ImageView cancel;
    GoogleMap map;
    MapView mapView;
    EditText location;
    ImageView navigate;
    TextView channelName;
    String lat, lng,channelTitle , channelDescription,channelTypeBusiness;
    String completeAddress,latitutude,longitude;
    Button skip;
    TextInputLayout locationLayout;
    public static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public static Backpressedlistener backpressedlistener;
    private String buttonState = "false";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_two, container, false);
        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        mapView = view.findViewById(R.id.mapView);
        location = view.findViewById(R.id.location_1);
        locationLayout = view.findViewById(R.id.locationLayout);
        // navigate = view.findViewById(R.id.navigate);
        channelName =  view.findViewById(R.id.channelName);
        skip = view.findViewById(R.id.skip);

        Bundle bundle = getArguments();
        if(bundle!= null){
            channelTitle = bundle.getString("ChannelName");
            channelName.setText(channelTitle);
            channelDescription = bundle.getString("ChannelDescription");
            channelTypeBusiness = bundle.getString("ChannelBusinessType");
            completeAddress = bundle.getString("CompleteAddress");
            latitutude = bundle.getString("Latitutude");
            buttonState = bundle.getString("ButtonState");
            longitude = bundle.getString("Longitude");
            location.setText(completeAddress);
        }



        Bundle mapViewBundle = null;
        if(savedInstanceState!= null)
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment locationPlacesFragment = new CommunityLocationFragmentPlaces();
                Bundle bundle = new Bundle();
                bundle.putString("ChannelName", channelTitle);
                bundle.putString("ChannelDescription", channelDescription);
                bundle.putString("ChannelBusinessType", channelTypeBusiness);
                System.out.println(channelTitle+channelDescription+channelTypeBusiness);
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                locationPlacesFragment.setArguments(bundle);
                transaction.replace(R.id.frameLayout, locationPlacesFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        if (buttonState.equals("false")){
            Utils.setButtonState(next,false);
        }else{
            Utils.setButtonState(next,true);
        }
        location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (location.getText().toString().isEmpty()){
                    Utils.setButtonState(next,false);
                }else{
                    Utils.setButtonState(next,true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (location.getText().toString().isEmpty()){
                    Utils.setButtonState(next,false);
                }else{
                    Utils.setButtonState(next,true);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createCommunityThree = new CreateCommunityThree();
                Bundle bundle1 = new Bundle();
                bundle1.putString("Latitude",latitutude);
                bundle1.putString("Longitude",longitude);
                bundle1.putString("ChannlName",channelTitle);
                bundle1.putString("ChannelDescription",channelDescription);
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                createCommunityThree.setArguments(bundle1);
                transaction.replace(R.id.frameLayout, createCommunityThree);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment createCommunityThree = new CreateCommunityThree();
                Bundle bundle1 = new Bundle();
                bundle1.putString("Latitude","0.0");
                bundle1.putString("Longitude","0.0");
                bundle1.putString("ChannlName",channelTitle);
                bundle1.putString("ChannelDescription",channelDescription);
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                createCommunityThree.setArguments(bundle1);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        backpressedlistener=this;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onPause() {
        backpressedlistener=null;
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {

    }


}

