package com.qamp.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.qamp.app.Activity.ChannelActivities.CommunityDashboard;
import com.qamp.app.Modal.InviteModal;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InviteViewHolder> {
    private ArrayList<InviteModal> inviteList;
    private Context context;
    // private OnItemClickListener listener;

    public InvitationAdapter(ArrayList<InviteModal> inviteList, Context context) {
        this.inviteList = inviteList;
        this.context = context;
    }

    public void setInviteList(ArrayList<InviteModal> inviteList) {
        this.inviteList = inviteList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public InviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_join_request_item, parent, false);
        return new InviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InviteViewHolder holder, int position) {

        InviteModal invite = inviteList.get(position);
        Glide.with(context)
                .load(invite.getChannelImageUrl())
                .placeholder(R.drawable.qamp_logo_outlined) // Placeholder image while loading
                .error(R.drawable.demoqamp) // Error image if loading fails
                .into(holder.requestingImage);
        holder.requestingTitle.setText(invite.getChannelTitle());
        if (invite.getState().equals("ACCEPTED")) {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.viewDetails.setText("Go to Channel");
            holder.requestingDescription.setText("You have joined the channel");
        } else if (invite.getState().equals("DECLINED")) {

        } else {
            holder.requestingDescription.setText(invite.getInviterUserName() + " invited you to join the channel.");
        }
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptInvite(invite.getUid());
            }
        });
        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineInvite(invite.getUid());
            }
        });
        holder.viewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommunityDashboard.class);
                context.startActivity(intent);
            }
        });
    }

    public void appendInviteList(ArrayList<InviteModal> newInviteList) {
        inviteList.addAll(newInviteList);
    }

    @Override
    public int getItemCount() {
        return inviteList.size();
    }

    private void acceptInvite(String inviteUid) {
        // TODO: Make API request to accept the invite using the provided accept curl
        // Update the invite list accordingly based on the API response
        String url = "https://dcore.qampservices.in/v1/channel-service/invite/accept";
        String userSessionToken = AppConfig.getConfig().token;

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("uid", inviteUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("SUCCESS")) {
                                // Handle successful acceptance
                                Toast.makeText(context, "Invite accepted successfully", Toast.LENGTH_SHORT).show();
                                // Refresh the invite list
                                loadInviteList();
                            } else {
                                Toast.makeText(context, "API response not successful", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "API request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("user-session-token", userSessionToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }


    private void declineInvite(String inviteUid) {
        // TODO: Make API request to decline the invite using the provided decline curl
        // Update the invite list accordingly based on the API response
        Toast.makeText(context, "Decline" + inviteUid, Toast.LENGTH_SHORT).show();

        String url = "https://dcore.qampservices.in/v1/channel-service/invite/reject";
        String userSessionToken = "your_user_session_token";

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("uid", inviteUid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if (status.equals("SUCCESS")) {
                                // Handle successful rejection
                                Toast.makeText(context, "Invite declined successfully", Toast.LENGTH_SHORT).show();

                                // Refresh the invite list
                                loadInviteList();
                            } else {
                                Toast.makeText(context, "API response not successful", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context, "API request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("user-session-token", userSessionToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);

    }

    private void loadInviteList() {
        // Clear the existing invite list
        inviteList.clear();

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
        RequestQueue queue = Volley.newRequestQueue(context);
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
                            setInviteList(inviteList);
                            notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Set isLoading to false after the request is completed

                },
                error -> {
                    // Handle the error
                    Toast.makeText(context, "Error loading invite list", Toast.LENGTH_SHORT).show();

                    // Set isLoading to false after the request is completed

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


    public static class InviteViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout mainLayout;
        private CircleImageView requestingImage;
        private TextView requestingTitle, requestingDescription, timingText, viewDetails;
        private LinearLayout acceptButton, declineButton;
        private ImageView bellIcon;

        public InviteViewHolder(@NonNull View itemView) {
            super(itemView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            requestingImage = itemView.findViewById(R.id.requestingImage);
            requestingTitle = itemView.findViewById(R.id.requestingTitle);
            requestingDescription = itemView.findViewById(R.id.requestingDescription);
            timingText = itemView.findViewById(R.id.timingText);
            viewDetails = itemView.findViewById(R.id.viewDetails);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
            bellIcon = itemView.findViewById(R.id.bellIcon);
        }


    }
}

