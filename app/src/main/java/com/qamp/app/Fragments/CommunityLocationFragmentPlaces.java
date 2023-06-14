/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.qamp.app.Adapter.PlaceAutoSuggestAdapter;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;

import java.util.List;

public class CommunityLocationFragmentPlaces extends Fragment implements OnMapReadyCallback, Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    String channelTitle, channelDescription, channelTypeBusiness;
    String completeAddress, latitutude, longitude;
    private Button okay_button;
    private LatLng latLng;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.location_places, container, false);
        final AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autocomplete);
        okay_button = view.findViewById(R.id.okay_button);
        Bundle bundle = getArguments();
        if (bundle != null) {
            channelTitle = bundle.getString("ChannelName");
            channelDescription = bundle.getString("ChannelDescription");
            channelTypeBusiness = bundle.getString("ChannelBusinessType");
        }

        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getActivity(), android.R.layout.simple_list_item_1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address : ", autoCompleteTextView.getText().toString());
                latLng = getLatLngFromAddress(autoCompleteTextView.getText().toString());
                if (latLng != null) {
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    Address address = getAddressFromLatLng(latLng);
                    if (address != null) {
                        Log.d("Address : ", "" + address.toString());
                        Log.d("Address Line : ", "" + address.getAddressLine(0));
                        Log.d("Phone : ", "" + address.getPhone());
                        Log.d("Pin Code : ", "" + address.getPostalCode());
                        Log.d("Feature : ", "" + address.getFeatureName());
                        Log.d("More : ", "" + address.getLocality());
                        okay_button.setEnabled(true);
                    } else {
                        Log.d("Adddress", "Address Not Found");
                    }
                } else {
                    Log.d("Lat Lng", "Lat Lng Not Found");
                }

            }
        });

        okay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment communityLocationFragment = new CommunityLocationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ChannelName", channelTitle);
                bundle.putString("ChannelDescription", channelDescription);
                bundle.putString("ChannelBusinessType", channelTypeBusiness);
                bundle.putString("CompleteAddress", autoCompleteTextView.getText().toString());
                bundle.putString("Latitutude", String.valueOf(latLng.latitude));
                bundle.putString("Longitude", String.valueOf(latLng.longitude));
                bundle.putString("ButtonState", "true");
                System.out.println(channelTitle+channelDescription+channelTypeBusiness+autoCompleteTextView.getText().toString()
                +String.valueOf(latLng.latitude)+String.valueOf(latLng.longitude));
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                communityLocationFragment.setArguments(bundle);
                transaction.replace(R.id.frameLayout, communityLocationFragment, "CommunityLocationFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        backpressedlistener = this;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onPause() {
        backpressedlistener = null;

        super.onPause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onBackPressed() {

    }

    private LatLng getLatLngFromAddress(String address) {

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null) {
                Address singleaddress = addressList.get(0);
                LatLng latLng = new LatLng(singleaddress.getLatitude(), singleaddress.getLongitude());
                return latLng;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private Address getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if (addresses != null) {
                Address address = addresses.get(0);
                return address;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}

