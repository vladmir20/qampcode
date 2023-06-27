/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mesibo.api.Mesibo;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CreateCommunityThree extends Fragment implements Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    Button next;
    LinearLayout buttonBack;
    ImageView cancel;
    TextView channelName;

    String channelTitle, lat, lng, channelDescription, channelBusinessType, invitationType , location;
    CardView openToAllLayout, byInviteLayout, byRequestLayout;
    TextView openToAllDes, byInviteDes, byRequestDes;
    ImageView openToAllLayoutCheck, byInviteCheck, byRequestCheck;
    String URL = QAMPAPIConstants.channel_base_url + String.format(QAMPAPIConstants.addChannel);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_three, container, false);

        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);
        buttonBack = view.findViewById(R.id.buttonBack);

        openToAllLayout = view.findViewById(R.id.openToAllLayout);
        byInviteLayout = view.findViewById(R.id.byInviteLayout);
        byRequestLayout = view.findViewById(R.id.byRequestLayout);

        openToAllDes = view.findViewById(R.id.openToAllDes);
        byInviteDes = view.findViewById(R.id.byInviteDes);
        byRequestDes = view.findViewById(R.id.byRequestDes);

        openToAllLayoutCheck = view.findViewById(R.id.openToAllLayoutCheck);
        byInviteCheck = view.findViewById(R.id.byInviteCheck);
        byRequestCheck = view.findViewById(R.id.byRequestCheck);

        setCommunityPermissionLayout("openToAllLayout", view);

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


        Bundle bundle = getArguments();
        if (bundle != null) {
            channelTitle = bundle.getString("ChannlName");
            lat = bundle.getString("Latitude");
            lng = bundle.getString("Longitude");
            channelDescription = bundle.getString("ChannelDescription");
            channelBusinessType = bundle.getString("ChannelBusinessType");
            channelName.setText(channelTitle);
            location = bundle.getString("Location");
        }

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment createCommunity = new CommunityLocationFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ChannelName", channelName.getText().toString());
                bundle.putString("ChannelDescription", channelDescription);
                bundle.putString("ChannelBusinessType", channelBusinessType);
                bundle.putString("ButtonState", "true");
                bundle.putString("CompleteAddress",location);
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                createCommunity.setArguments(bundle);
                transaction.replace(R.id.frameLayout, createCommunity);
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


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (AppUtils.isNetWorkAvailable(getActivity())) {
//                    AppUtils.openProgressDialog(getActivity());
  //                   createCommunities();
//                } else {
//                    Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
//                }
                Fragment createCommunity = new CreateCommunityFour();
                Bundle bundle = new Bundle();
                bundle.putString("ChannelName", channelName.getText().toString());
                bundle.putString("ChannelDescription", channelDescription);
                bundle.putString("ChannelBusinessType", channelBusinessType);
                bundle.putString("CompleteAddress",location);
                bundle.putString("lat",lat);
                bundle.putString("lng",lng);
                bundle.putString("email","");
                bundle.putString("number",Mesibo.getSelfProfile().address.substring(2, 12));
                bundle.putString("domain","www.test.com");
                bundle.putString("invitationType","BY_INVITE_ONLY");
                final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                createCommunity.setArguments(bundle);
                transaction.replace(R.id.frameLayout, createCommunity);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void createCommunities() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("title", channelTitle);
            jsonBody.put("description", channelDescription);
            jsonBody.put("type", "BUSINESS");
            if (lat.equals("0.0") && lng.equals("0.0")) {
                jsonBody.put("haveGeoLocation", "false");
            } else {
                jsonBody.put("haveGeoLocation", "true");
            }
            jsonBody.put("latitude", lat);
            jsonBody.put("longitude", lng);
            jsonBody.put("emailid", "shivam@qamp.in");
            String number = Mesibo.getSelfProfile().address.substring(2, 12);
            jsonBody.put("mobileNumber", number);
            jsonBody.put("domain", "www.test.com");
            jsonBody.put("businessType", "BUSINESS");
            jsonBody.put("invitationType", "BY_INVITE_ONLY");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AppUtils.closeProgresDialog();
                    try {
                        String status = response.getString("status");
                        Log.e("VOLLEY Status======", status);
                        if (status.contains(QampConstants.success)) {
                            JSONObject data = response.getJSONObject("data");
                            Bundle bundle = new Bundle();
                            bundle.putString("Channel_ID", data.getString("uid"));
                            bundle.putString("Channel_Type", data.getString("type"));
                            bundle.putString("Channel_Title", data.getString("title"));
                            bundle.putString("Channel_Description", data.getString("description"));
                            inviteFragment(bundle);
                        } else {
                            JSONArray errors = response.getJSONArray("error");
                            JSONObject error = (JSONObject) errors.get(0);
                            String errMsg = error.getString("errMsg");
                            String errorCode = error.getString("errCode");
                            Toast.makeText(getActivity(), "" + errMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        AppUtils.closeProgresDialog();
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.general_error), Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("user-session-token", AppConfig.getConfig().token);
                    params.put("content-type", "application/json");
                    return params;
                }
            };
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private void inviteFragment(Bundle bundle) {
        Fragment createCommunityFour = new CreateCommunityFour();
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        createCommunityFour.setArguments(bundle);
        transaction.replace(R.id.frameLayout, createCommunityFour);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void setCommunityPermissionLayout(String string, View view) {
        if (string.equals("openToAllLayout")) {
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_active));
            byInviteCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byRequestCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteDes.setText("Closed community where people with \ninvitation can join");
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            invitationType = "OPEN_TO_ALL";
        } else if (string.equals("byInviteLayout")) {
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_active));
            byRequestCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));            byInviteDes.setText("Closed community where people with \ninvitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            invitationType = "BY_INVITE";
        } else if (string.equals("byRequestLayout")) {
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byInviteCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_normal));
            byRequestCheck.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.radio_active));
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            byInviteDes.setText("Closed community where people with \ninvitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            invitationType = "BY_REQUEST";
        }
    }

    @Override
    public void onBackPressed() {
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
}