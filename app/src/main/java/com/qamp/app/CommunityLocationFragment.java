/*
 * *
 *  * Created by Shivam Tiwari on 28/04/23, 8:10 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 28/04/23, 8:10 PM
 *
 */

package com.qamp.app;

import static com.qamp.app.qampcallss.api.CallStateReceiver.context;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.anirudh.locationfetch.EasyLocationFetch;
import com.anirudh.locationfetch.GeoLocationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.qamp.app.messaging.MesiboUserListActivityNew;

import java.io.IOException;
import java.util.List;

public class CommunityLocationFragment extends Fragment implements OnMapReadyCallback, Backpressedlistener {

    Button next;
    ImageView cancel;
    GoogleMap map;
    MapView mapView;
    EditText location;
    ImageView navigate;
    TextView channelName;
    String lat, lng,channelTitle , channelDescription;
    Button skip;
    TextInputLayout locationLayout;
    public static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    public static Backpressedlistener backpressedlistener;


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
        }

        Bundle mapViewBundle = null;
        if(savedInstanceState!= null)
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                GeoLocationModel geoLocationModel = new EasyLocationFetch(getContext(),GoogleApiKey).
//                        getLocationData();
                GeoLocationModel geoLocationModel = new EasyLocationFetch(getContext()).getLocationData();
//                geoLocationModel.getLattitude();
//                geoLocationModel.getLongitude();
                lat = String.valueOf(geoLocationModel.getLattitude());
                lng = String.valueOf(geoLocationModel.getLongitude());
                location.setText(geoLocationModel.getLattitude()+","+geoLocationModel.getLongitude());
            }
        });

//        navigate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String loc = location.getText().toString();
//                List<Address> addressList = null;
//                Geocoder geocoder = new Geocoder(getActivity());
//
//                if(loc!=null || !loc.equals("")){
//                    try {
//                        addressList = geocoder.getFromLocationName(loc,1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Address address = addressList.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
//                    lat = String.valueOf(address.getLatitude());
//                    lng = String.valueOf(address.getLongitude());
//                    map.addMarker(new MarkerOptions().position(latLng).title(loc));
//                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
//                }
//            }
//        });





        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment createCommunityThree = new CreateCommunityThree();
                Bundle bundle1 = new Bundle();
                bundle1.putString("Latitude",lat);
                bundle1.putString("Longitude",lng);
                bundle1.putString("ChannlName",channelTitle);
                bundle1.putString("ChannelDescription",channelDescription);
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                createCommunityThree.setArguments(bundle1);
                transaction.replace(R.id.frameLayout, createCommunityThree);
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

