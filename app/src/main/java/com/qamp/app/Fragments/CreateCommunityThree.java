/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mesibo.api.Mesibo;
import com.qamp.app.Activity.CommunityDashboard;
import com.qamp.app.Activity.OnBoardingUserProfile;
import com.qamp.app.Activity.WelcomeOnboarding;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class CreateCommunityThree extends Fragment implements Backpressedlistener {

    public static Backpressedlistener backpressedlistener;
    Button next;
    ImageView cancel;
    TextView channelName;
    String channelTitle, lat, lng, channelDescription, channelBusinessType, invitationType;
    CardView openToAllLayout, byInviteLayout, byRequestLayout;
    TextView openToAllDes, byInviteDes, byRequestDes;
    CheckBox openToAllLayoutCheck, byInviteCheck, byRequestCheck;
    String URL = QAMPAPIConstants.channel_base_url + String.format(QAMPAPIConstants.addChannel);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_three, container, false);

        next = view.findViewById(R.id.buttonNext);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);

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
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtils.isNetWorkAvailable(getActivity())) {
                    AppUtils.openProgressDialog(getActivity());
                    createCommunities();
                } else {
                    Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                }
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
            if (lat.equals("0.0")&&lng.equals("0.0")){
                jsonBody.put("haveGeoLocation", "false");
            }else{
                jsonBody.put("haveGeoLocation", "true");
            }
            jsonBody.put("latitude", lat);
            jsonBody.put("longitude", lng);
            jsonBody.put("emailid", "shivam@qamp.in");
            String number = Mesibo.getSelfProfile().address.substring(2, 12);
            jsonBody.put("mobileNumber", number);
            jsonBody.put("domain", "www.test.com");
            jsonBody.put("businessType", "businessType");
            jsonBody.put("invitationType", "BY_INVITE_ONLY");
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL
                    , jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AppUtils.closeProgresDialog();
                    try {
                        String status = response.getString("status");
                        Log.e("VOLLEY Status======", status);
                        if (status.contains(QampConstants.success)) {
                            JSONObject data = response.getJSONObject("data");
                            //String fullName = data.getString("fullName");
                            System.out.println(""+data);
                            AppUtils.community_created_successfully(getActivity());
                            Intent intent = new Intent(getActivity(), CommunityDashboard.class);
                            startActivity(intent);
                            getActivity().finish();
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


    private void inviteFragment() {
        Fragment createCommunityFour = new CreateCommunityFour();
        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, createCommunityFour);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void createCommunity() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("title", channelTitle);
            jsonBody.put("description", channelDescription);
            jsonBody.put("type", "BUSINESS");
            jsonBody.put("haveGeoLocation", "true");
            jsonBody.put("latitude", lat);
            jsonBody.put("longitude", lng);
            jsonBody.put("emailid", "shivam@qamp.in");
            String number = Mesibo.getSelfProfile().address.substring(2, 12);
            jsonBody.put("mobileNumber", number);
            jsonBody.put("domain", "www.test.com");
            jsonBody.put("businessType", "businessType");
            jsonBody.put("invitationType", "BY_INVITE_ONLY");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("VOLLEY======Channel", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.contains(QampConstants.success)) {
                        Toast.makeText(getActivity(), "SUCCESS CREATED", Toast.LENGTH_SHORT).show();
                        inviteFragment();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "UNSUCCESS CREATED" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                System.out.println(AppConfig.getConfig().token.toString());
                headers.put("Content-Type", "application/json");
                headers.put("user-session-token", AppConfig.getConfig().token);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Map<String, Object> params = new HashMap<>();
                try {
                    params.put("title", jsonBody.getString("title"));
                    params.put("description", jsonBody.getString("description"));
                    params.put("type", jsonBody.getString("type"));
                    params.put("haveGeoLocation", jsonBody.getString("haveGeoLocation"));
                    params.put("latitude", jsonBody.getString("latitude"));
                    params.put("longitude", jsonBody.getString("longitude"));
                    params.put("number", jsonBody.getString("mobileNumber"));
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        System.out.println(entry.getKey() + ":" + entry.getValue().toString());
                    }
                    System.out.println("token " + AppConfig.getConfig().token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (params != null && params.size() > 0) {
                    return AppUtils.encodeParameter(params, getParamsEncoding());
                }
                return null;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void setCommunityPermissionLayout(String string, View view) {
        if (string.equals("openToAllLayout")) {
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            openToAllLayoutCheck.setChecked(true);
            openToAllLayoutCheck.setVisibility(View.VISIBLE);
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteCheck.setVisibility(View.GONE);
            byInviteDes.setText("Closed community where people with invitation can join");
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestCheck.setVisibility(View.GONE);
            byRequestDes.setText("Join request needs to be approved by community manager");
            invitationType = "OPEN_TO_ALL";
        } else if (string.equals("byInviteLayout")) {
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            byInviteCheck.setChecked(true);
            byInviteCheck.setVisibility(View.VISIBLE);
            byInviteDes.setText("Closed community where people with \ninvitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            openToAllLayoutCheck.setVisibility(View.GONE);
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byRequestCheck.setVisibility(View.GONE);
            byRequestDes.setText("Join request needs to be approved by community manager");
            invitationType = "BY_INVITE";
        } else if (string.equals("byRequestLayout")) {
            byRequestLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.background_tintTaskBar));
            byRequestCheck.setChecked(true);
            byRequestCheck.setVisibility(View.VISIBLE);
            byRequestDes.setText("Join request needs to be approved by \ncommunity manager");
            byInviteDes.setText("Closed community where people with invitation can join");
            openToAllLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            openToAllLayoutCheck.setVisibility(View.GONE);
            byInviteLayout.setCardBackgroundColor(getActivity().getResources().getColor(R.color.white));
            byInviteCheck.setVisibility(View.GONE);
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