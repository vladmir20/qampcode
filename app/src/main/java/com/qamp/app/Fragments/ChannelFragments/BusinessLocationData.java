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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BusinessLocationData extends Fragment implements Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    TextView businessName;
    ImageView closeBtn;
    AutoCompleteTextView location_1;
    Button nextFragment, skipBtn;
    LinearLayout backBtn;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private Geocoder geocoder;
    private ProgressBar progressBar;

    String buttonState, businessTitleString, businessDescriptionString, businessTypeString,
            businessHaveGeoLocation, businessLatitude, businessLangitutde, businessEmailID, businessMobileNumber,
            businessDomain, businessTypeStringBusiness, businessInvitationType, completeAddress, location;

    public BusinessLocationData() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_location_data, container, false);
        initViews(view);
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
        Bundle bundle = getArguments();
        if (bundle != null)
            updateVariablesIfHaveValues(bundle);
        setValuesInUI();
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveValuesToNextFragment("NoLocation");
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveValuesToPreviousFragment();
            }
        });
        nextFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!location_1.getText().toString().isEmpty())) {
                    moveValuesToNextFragment("WithLocation");
                }
            }
        });
        location_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (location_1.getRight() - location_1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Right drawable clicked
                        getCurrentLocation();
                        return true;
                    }
                }
                return false;
            }
        });
        location_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (location_1.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment,false);
                else{
                    Utils.setButtonState(nextFragment,true);
                    completeAddress = location_1.getText().toString();
                    businessHaveGeoLocation = "true";
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (location_1.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment,false);
                else{
                    Utils.setButtonState(nextFragment,true);
                    completeAddress = location_1.getText().toString();
                    businessHaveGeoLocation = "true";
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (location_1.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment,false);
                else{
                    Utils.setButtonState(nextFragment,true);
                    completeAddress = location_1.getText().toString();
                    businessHaveGeoLocation = "true";
                }
            }
        });
        location_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBusinessLocationSearchFragment();
            }
        });
        return view;
    }


    private void openBusinessLocationSearchFragment() {
        Fragment businessLocationSearch = new BusinessLocationSearch();
        Bundle bundle = new Bundle();
        bundle.putString("buttonState","false");
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
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        businessLocationSearch.setArguments(bundle);
        transaction.replace(R.id.frameLayout, businessLocationSearch, "BusinessLocationSearch");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        location_1.setEnabled(false);

        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String addressLine = addresses.get(0).getAddressLine(0);
                        location_1.setText(addressLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);
                location_1.setEnabled(true);
                businessLangitutde = String.valueOf(location.getLongitude());
                businessLatitude = String.valueOf(location.getLatitude());
                businessHaveGeoLocation = "true";
                //Toast.makeText(requireContext(), "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                progressBar.setVisibility(View.GONE);
                location_1.setEnabled(true);
                Toast.makeText(requireContext(), "Location provider disabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        }, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveValuesToNextFragment(String getLocation) {
        if (getLocation.equals("NoLocation")){
            Fragment businessLocationData = new BusinessInteractionType();
            Bundle bundle = new Bundle();
            completeAddress="";
            businessHaveGeoLocation="false";
            businessLangitutde="";
            businessLatitude="";
            bundle.putString("buttonState","false");
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
            final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            businessLocationData.setArguments(bundle);
            transaction.replace(R.id.frameLayout, businessLocationData, "BusinessInteractionType");
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (getLocation.equals("WithLocation")) {
            Fragment businessLocationData = new BusinessInteractionType();
            Bundle bundle = new Bundle();
            bundle.putString("buttonState","false");
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
            final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            businessLocationData.setArguments(bundle);
            transaction.replace(R.id.frameLayout, businessLocationData, "BusinessInteractionType");
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    private void moveValuesToPreviousFragment() {
        Fragment businessDataCreation = new BusinessDataCreation();
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
        if (!businessTitleString.equals("")||!businessDescriptionString.equals("")){
            bundle.putString("buttonState","true");
        }else{
            bundle.putString("buttonState","false");
        }
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        businessDataCreation.setArguments(bundle);
        transaction.replace(R.id.frameLayout, businessDataCreation, "BusinessDataCreation");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setValuesInUI() {
        businessName.setText(businessTitleString);
        location_1.setText(completeAddress);
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
        if (!completeAddress.equals(""))
            Utils.setButtonState(nextFragment, true);
        else
            Utils.setButtonState(nextFragment, false);
    }


    private void initViews(View view) {
        businessName = view.findViewById(R.id.businessName);
        closeBtn = view.findViewById(R.id.closeBtn);
        location_1 = view.findViewById(R.id.location_1);
        nextFragment = view.findViewById(R.id.nextFragment);
        skipBtn = view.findViewById(R.id.skipBtn);
        backBtn = view.findViewById(R.id.buttonBack);
        progressBar = view.findViewById(R.id.progressBar);
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

