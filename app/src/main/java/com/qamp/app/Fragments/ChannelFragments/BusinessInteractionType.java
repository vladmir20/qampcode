package com.qamp.app.Fragments.ChannelFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;

public class BusinessInteractionType extends Fragment implements Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    TextView businessName;
    ImageView closeBtn;
    Button nextFragment, skipBtn;
    LinearLayout backBtn;

    String buttonState, businessTitleString, businessDescriptionString, businessTypeString,
            businessHaveGeoLocation, businessLatitude, businessLangitutde, businessEmailID, businessMobileNumber,
            businessDomain, businessTypeStringBusiness, businessInvitationType, completeAddress, location;

    CardView openToAllLayout, byInviteLayout, byRequestLayout;
    TextView openToAllDes, byInviteDes, byRequestDes;
    ImageView openToAllLayoutCheck, byInviteCheck, byRequestCheck;

    public BusinessInteractionType() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_interaction_type, container, false);
        initViews(view);
        Bundle bundle = getArguments();
        if (bundle != null)
            updateVariablesIfHaveValues(bundle);
        setValuesInUI();
        setCommunityPermissionLayout("openToAllLayout", view);
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            System.out.println("Key: " + key + ", Value: " + value);
        }
        openToAllLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommunityPermissionLayout("openToAllLayout", view);
            }
        });
        byInviteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommunityPermissionLayout("byInviteLayout", view);
            }
        });
        byRequestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCommunityPermissionLayout("byRequestLayout", view);
            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveValuesToNextFragment();
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
                moveValuesToNextFragment();
            }
        });
        return view;
    }

    private void setCommunityPermissionLayout(String string, View view) {
        if (string.equals("openToAllLayout")) {
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_active));
            openToAllDes.setText("Open community where anyone can join.");
            byInviteCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byRequestCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteDes.setText("Closed community where people with \ninvitation can join");
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            businessInvitationType = "OPEN_TO_ALL";
        } else if (string.equals("byInviteLayout")) {
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_active));
            byRequestCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteDes.setText("Closed community where people with \ninvitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            businessInvitationType = "BY_INVITE";
        } else if (string.equals("byRequestLayout")) {
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byRequestCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_active));
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            byInviteDes.setText("Closed community where people with \ninvitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            businessInvitationType = "BY_REQUEST";
        }
    }

    private void moveValuesToNextFragment() {
        Fragment businessInvitaionAndCreation = new BusinessInvitaionAndCreation();
        Bundle bundle = new Bundle();
        bundle.putString("buttonState", "false");
        bundle.putString("businessTitleString", businessTitleString);
        bundle.putString("businessDescriptionString", businessDescriptionString);
        bundle.putString("businessTypeString", businessTypeString);
        bundle.putString("businessHaveGeoLocation", businessHaveGeoLocation);
        bundle.putString("businessLangitutde", businessLangitutde);
        bundle.putString("businessLatitude", businessLatitude);
        bundle.putString("businessEmailID", businessEmailID);
        bundle.putString("businessMobileNumber", businessMobileNumber);
        bundle.putString("businessDomain", businessDomain);
        bundle.putString("businessTypeStringBusiness", businessTypeString);
        bundle.putString("businessInvitationType", businessInvitationType);
        bundle.putString("completeAddress", completeAddress);
        bundle.putString("location", location);
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        businessInvitaionAndCreation.setArguments(bundle);
        transaction.replace(R.id.frameLayout, businessInvitaionAndCreation, "BusinessInvitaionAndCreation");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void moveValuesToPreviousFragment() {
        Fragment businessLocationData = new BusinessLocationData();
        Bundle bundle = new Bundle();
        bundle.putString("businessTitleString", businessTitleString);
        bundle.putString("businessDescriptionString", businessDescriptionString);
        bundle.putString("businessTypeString", businessTypeString);
        bundle.putString("businessHaveGeoLocation", businessHaveGeoLocation);
        bundle.putString("businessLangitutde", businessLangitutde);
        bundle.putString("businessLatitude", businessLatitude);
        bundle.putString("businessEmailID", businessEmailID);
        bundle.putString("businessMobileNumber", businessMobileNumber);
        bundle.putString("businessDomain", businessDomain);
        bundle.putString("businessTypeStringBusiness", businessTypeString);
        bundle.putString("businessInvitationType", businessInvitationType);
        bundle.putString("completeAddress", completeAddress);
        bundle.putString("location", location);
        if (!completeAddress.equals("")) {
            bundle.putString("buttonState", "true");
        } else {
            bundle.putString("buttonState", "false");
        }
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        businessLocationData.setArguments(bundle);
        transaction.replace(R.id.frameLayout, businessLocationData, "BusinessLocationData");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setValuesInUI() {
        businessName.setText(businessTitleString);
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
    }


    private void initViews(View view) {
        businessName = view.findViewById(R.id.businessName);
        closeBtn = view.findViewById(R.id.closeBtn);
        nextFragment = view.findViewById(R.id.nextFragment);
        skipBtn = view.findViewById(R.id.cancelBtn);
        backBtn = view.findViewById(R.id.buttonBack);

        openToAllLayout = view.findViewById(R.id.openToAllLayout);
        byInviteLayout = view.findViewById(R.id.byInviteLayout);
        byRequestLayout = view.findViewById(R.id.byRequestLayout);

        openToAllDes = view.findViewById(R.id.openToAllDes);
        byInviteDes = view.findViewById(R.id.byInviteDes);
        byRequestDes = view.findViewById(R.id.byRequestDes);

        openToAllLayoutCheck = view.findViewById(R.id.openToAllLayoutCheck);
        byInviteCheck = view.findViewById(R.id.byInviteCheck);
        byRequestCheck = view.findViewById(R.id.byRequestCheck);
    }


    @Override
    public void onPause() {
        backpressedlistener = null;
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        backpressedlistener = this;
    }

    @Override
    public void onBackPressed() {
        moveValuesToPreviousFragment();
    }

}

