/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.qamp.app.Adapter.InvitationAdapter;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.Modal.InviteModal;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChannelNotificationCenter extends Fragment implements Backpressedlistener {
    public static Backpressedlistener backpressedlistener;

    private RecyclerView inviteRecyclerView;
    private InvitationAdapter inviteListAdapter;
    private ArrayList<InviteModal> inviteList;
    private boolean isLoading = false;
    public ChannelNotificationCenter() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.channel_notification_center_layout, container, false);
        inviteRecyclerView = view.findViewById(R.id.inviteRecyclerView);
        inviteList = new ArrayList<>();
        inviteListAdapter = new InvitationAdapter(inviteList, getContext());
        inviteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inviteRecyclerView.setAdapter(inviteListAdapter);
        // Load initial invite list
        loadInviteList();
        // Pagination listener for loading more invites when scrolling
        inviteRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!isLoading && !recyclerView.canScrollVertically(1)) {
                    loadMoreInviteList();
                }
            }
        });
        return view;
    }

    private void loadInviteList() {
        isLoading = true;

        String url = "https://dcore.qampservices.in/v1/channel-service/invite";
        int pageNumber = 1;
        int pageSize = 10;
        String userSessionToken = AppConfig.getConfig().token;

        // Create the request parameters
        HashMap<String, String> headers = new HashMap<>();
        headers.put("page-number", String.valueOf(pageNumber));
        headers.put("page-size", String.valueOf(pageSize));
        headers.put("user-session-token", userSessionToken);

        // Make the API request to get the invite list
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("SUCCESS")) {
                            JSONArray dataArr = response.getJSONArray("data");
                            ArrayList<InviteModal> inviteList = new ArrayList<>();

                            // Parse the invite list data
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject inviteObj = dataArr.getJSONObject(i);
                                InviteModal invite = new InviteModal();
                                invite.setUid(inviteObj.getString("uid"));
                                invite.setChannelId(inviteObj.getString("channelId"));
                                invite.setCountryCode(inviteObj.getString("countryCode"));
                                invite.setInvitedMobileNumber(inviteObj.getString("invitedMobileNumber"));
                                invite.setState(inviteObj.getString("state"));
                                invite.setReaded(inviteObj.getBoolean("readed"));
                                invite.setChannelImageUrl(inviteObj.getString("channelImageUrl"));
                                invite.setInviterUserName(inviteObj.getString("invityUserName"));
                                invite.setChannelTitle(inviteObj.getString("channelTitle"));
                                invite.setCreationDate(inviteObj.getLong("creationDate"));
                                invite.setUpdatedDate(inviteObj.getLong("updatedDate"));
                                invite.setInvitedRegion(inviteObj.optString("invitedRegion"));

                                inviteList.add(invite);
                            }

                            // Update the invite list and notify the adapter
                            inviteListAdapter.setInviteList(inviteList);
                            inviteListAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Set isLoading to false after the request is completed
                    isLoading = false;
                },
                error -> {
                    // Handle the error
                    Toast.makeText(getContext(), "Error loading invite list", Toast.LENGTH_SHORT).show();

                    // Set isLoading to false after the request is completed
                    isLoading = false;
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        // Add the request to the request queue
        queue.add(request);
    }

    private void loadMoreInviteList() {
        isLoading = true;

        String url = "https://dcore.qampservices.in/v1/channel-service/invite";
        int pageNumber = 2; // Assuming we need to load the second page
        int pageSize = 10;
        String userSessionToken = AppConfig.getConfig().token;

        // Create the request parameters
        HashMap<String, String> headers = new HashMap<>();
        headers.put("page-number", String.valueOf(pageNumber));
        headers.put("page-size", String.valueOf(pageSize));
        headers.put("user-session-token", userSessionToken);

        // Make the API request to get the invite list
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("SUCCESS")) {
                            JSONArray dataArr = response.getJSONArray("data");
                            ArrayList<InviteModal> inviteList = new ArrayList<>();

                            // Parse the invite list data
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject inviteObj = dataArr.getJSONObject(i);
                                InviteModal invite = new InviteModal();
                                invite.setUid(inviteObj.getString("uid"));
                                invite.setChannelId(inviteObj.getString("channelId"));
                                invite.setCountryCode(inviteObj.getString("countryCode"));
                                invite.setInvitedMobileNumber(inviteObj.getString("invitedMobileNumber"));
                                invite.setState(inviteObj.getString("state"));
                                invite.setReaded(inviteObj.getBoolean("readed"));
                                invite.setChannelImageUrl(inviteObj.getString("channelImageUrl"));
                                invite.setInviterUserName(inviteObj.getString("invityUserName"));
                                invite.setChannelTitle(inviteObj.getString("channelTitle"));
                                invite.setCreationDate(inviteObj.getLong("creationDate"));
                                invite.setUpdatedDate(inviteObj.getLong("updatedDate"));
                                invite.setInvitedRegion(inviteObj.optString("invitedRegion"));
                                inviteList.add(invite);
                            }

                            // Append the new invite list to the existing list and notify the adapter
                            inviteListAdapter.appendInviteList(inviteList);
                            inviteListAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Set isLoading to false after the request is completed
                    isLoading = false;
                },
                error -> {
                    // Handle the error
                    Toast.makeText(getContext(), "Error loading more invite list", Toast.LENGTH_SHORT).show();

                    // Set isLoading to false after the request is completed
                    isLoading = false;
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        // Add the request to the request queue
        queue.add(request);
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