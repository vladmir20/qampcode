package com.qamp.app.Fragments.ChannelFragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.mesibo.api.Mesibo;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.Utils;

public class BusinessDataCreation extends Fragment implements Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    TextView businessName;
    ImageView closeBtn;
    TextInputEditText businessTitle, businessType, businessDescription;
    Button nextFragment, cancelBtn;

    String buttonState, businessTitleString, businessDescriptionString, businessTypeString,
            businessHaveGeoLocation, businessLatitude, businessLangitutde, businessEmailID, businessMobileNumber,
            businessDomain, businessTypeStringBusiness, businessInvitationType, completeAddress, location;

    public BusinessDataCreation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_data_creation, container, false);
        initViews(view);
        initalizeVariables();
        Bundle bundle = getArguments();
        if (bundle != null)
            updateVariablesIfHaveValues(bundle);
        setValuesInUI();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        nextFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!businessTitle.getText().toString().isEmpty()) &&
                        (!businessDescription.getText().toString().isEmpty())) {
                    moveValuesToNextFragment();
                }else{
                    Toast.makeText(getActivity(), "Business Name and Business Description cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    private void moveValuesToNextFragment() {
        Fragment businessLocationData = new BusinessLocationData();
        Bundle bundle = new Bundle();
        bundle.putString("buttonState","false");
        bundle.putString("businessTitleString",businessTitle.getText().toString());
        bundle.putString("businessDescriptionString",businessDescription.getText().toString());
        bundle.putString("businessTypeString",businessType.getText().toString());
        bundle.putString("businessHaveGeoLocation",businessHaveGeoLocation);
        bundle.putString("businessLangitutde",businessLangitutde);
        bundle.putString("businessLatitude",businessLatitude);
        bundle.putString("businessEmailID",businessEmailID);
        bundle.putString("businessMobileNumber",businessMobileNumber);
        bundle.putString("businessDomain",businessDomain);
        bundle.putString("businessTypeStringBusiness",businessTypeStringBusiness);
        bundle.putString("businessInvitationType",businessInvitationType);
        bundle.putString("completeAddress",completeAddress);
        bundle.putString("location",location);
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        businessLocationData.setArguments(bundle);
        transaction.replace(R.id.frameLayout, businessLocationData, "BusinessLocationData");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setValuesInUI() {
        businessTitle.setText(businessTitleString);
        businessDescription.setText(businessDescriptionString);
        businessType.setText(businessTypeString);
        setButtonBehaviour();
    }

    private void setButtonBehaviour() {
        businessTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (businessTitle.getText().toString().isEmpty() || businessDescription.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment, false);
                else
                    Utils.setButtonState(nextFragment, true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (businessTitle.getText().toString().isEmpty() || businessDescription.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment, false);
                else
                    Utils.setButtonState(nextFragment, true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (businessTitle.getText().toString().isEmpty() || businessDescription.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment, false);
                else
                    Utils.setButtonState(nextFragment, true);
            }
        });
        businessDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (businessTitle.getText().toString().isEmpty() || businessDescription.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment, false);
                else
                    Utils.setButtonState(nextFragment, true);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (businessTitle.getText().toString().isEmpty() || businessDescription.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment, false);
                else
                    Utils.setButtonState(nextFragment, true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (businessTitle.getText().toString().isEmpty() || businessDescription.getText().toString().isEmpty())
                    Utils.setButtonState(nextFragment, false);
                else
                    Utils.setButtonState(nextFragment, true);
            }
        });
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
        if (buttonState.equals("true"))
            Utils.setButtonState(nextFragment, true);
        else
            Utils.setButtonState(nextFragment, false);
    }

        private void initalizeVariables () {
            buttonState = "false";
            businessTitleString = businessTitle.getText().toString();
            businessDescriptionString = businessDescription.getText().toString();
            businessTypeString = businessType.getText().toString();
            businessHaveGeoLocation = "false";
            businessLatitude = "0.0";
            businessLangitutde = "0.0";
            businessEmailID = "test@email.com";
            businessMobileNumber = Mesibo.getSelfProfile().address.substring(2, 12);
            businessDomain = "www.qamp.in";
            businessTypeStringBusiness = "businessType_test";
            businessInvitationType = "OPEN_TO_ALL";
            completeAddress = "";
            location = "";
        }

        private void initViews (View view){
            businessName = view.findViewById(R.id.businessName);
            closeBtn = view.findViewById(R.id.closeBtn);
            businessTitle = view.findViewById(R.id.businessTitle);
            businessType = view.findViewById(R.id.businessType);
            businessDescription = view.findViewById(R.id.businessDescription);
            nextFragment = view.findViewById(R.id.nextFragment);
            cancelBtn = view.findViewById(R.id.cancelBtn);
            Utils.setButtonState(nextFragment, false);
        }

        @Override
        public void onBackPressed () {
            getActivity().finish();
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
    }
