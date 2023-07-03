package com.qamp.app.Fragments.ChannelFragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.qamp.app.Adapter.PlaceAutoSuggestAdapter;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BusinessLocationSearch extends Fragment implements Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    ImageView backBtn;
    AutoCompleteTextView locationText;

    String buttonState, businessTitleString, businessDescriptionString, businessTypeString,
            businessHaveGeoLocation, businessLatitude, businessLangitutde, businessEmailID, businessMobileNumber,
            businessDomain, businessTypeStringBusiness, businessInvitationType, completeAddress, location;
    ListView suggestionListView;
    public BusinessLocationSearch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_location_search, container, false);
        initViews(view);

        Bundle bundle = getArguments();
        if (bundle != null)
            updateVariablesIfHaveValues(bundle);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveValuesToPreviousFragment();
            }
        });
        PlaceAutoSuggestAdapter adapter = new PlaceAutoSuggestAdapter(getActivity(), R.layout.search_location_item);
        locationText.setAdapter(adapter);
        suggestionListView.setAdapter(adapter);
        suggestionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position);
                locationText.setText(selectedItem);
                LatLng latLng = null;
                latLng = getLatLngFromAddress(locationText.getText().toString());
                if (latLng != null) {
                    completeAddress = locationText.getText().toString();
                    businessLatitude = String.valueOf(latLng.latitude);
                    businessLangitutde = String.valueOf(latLng.longitude);
                    Log.d("Lat Lng : ", " " + latLng.latitude + " " + latLng.longitude);
                    moveValuesToPreviousFragment();
                }
            }
        });
        locationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }


    private void moveValuesToPreviousFragment() {
        Fragment businessDataCreation = new BusinessLocationData();
        Bundle bundle = new Bundle();
        bundle.putString("businessTitleString",businessTitleString);
        bundle.putString("businessDescriptionString",businessDescriptionString);
        bundle.putString("businessTypeString",businessTypeString);
        bundle.putString("businessHaveGeoLocation",businessHaveGeoLocation);
        bundle.putString("businessLangitutde",businessLangitutde);
        bundle.putString("businessLatitude",businessLatitude);
        bundle.putString("businessEmailID",businessEmailID);
        bundle.putString("businessMobileNumber",businessMobileNumber);
        bundle.putString("businessDomain",businessDomain);
        bundle.putString("businessTypeStringBusiness",businessTypeString);
        bundle.putString("businessInvitationType",businessInvitationType);
        bundle.putString("completeAddress",completeAddress);
        bundle.putString("location",location);
        if (!locationText.getText().toString().isEmpty()){
            bundle.putString("buttonState","true");
        }else{
            bundle.putString("buttonState","false");
        }
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        businessDataCreation.setArguments(bundle);
        transaction.replace(R.id.frameLayout, businessDataCreation, "BusinessLocationData");
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void updateVariablesIfHaveValues(Bundle bundle) {
        buttonState = bundle.getString("buttonState");
        businessTitleString = bundle.getString("businessTitleString");
        businessDescriptionString = bundle.getString("businessDescriptionString");
        businessTypeString = bundle.getString("businessTypeString");
        businessHaveGeoLocation = bundle.getString("businessHaveGeoLocation");
        businessLangitutde = bundle.getString("businessLangitutde");
        businessLatitude = bundle.getString("businessLatitude");
        businessEmailID = bundle.getString("businessEmailID");
        businessMobileNumber = bundle.getString("businessMobileNumber");
        businessDomain = bundle.getString("businessDomain");
        businessTypeStringBusiness = bundle.getString("businessTypeStringBusiness");
        businessInvitationType = bundle.getString("businessInvitationType");
        completeAddress = bundle.getString("completeAddress");
        location = bundle.getString("location");
        locationText.setText(completeAddress);
    }


    private void initViews(View view) {
        backBtn = view.findViewById(R.id.back_btn);
        locationText = view.findViewById(R.id.locationText);
        locationText.setDropDownHeight(0);
        suggestionListView =view.findViewById(R.id.suggestionListView);
        locationText.requestFocus();
        locationText.setEnabled(true);
        locationText.setFocusable(true);
        locationText.setFocusableInTouchMode(true);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(locationText, InputMethodManager.SHOW_IMPLICIT);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(locationText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
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

    @Override
    public void onPause () {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume () {
        super.onResume();
        backpressedlistener = this;
    }

    @Override
    public void onBackPressed() {
        moveValuesToPreviousFragment();
    }

}
