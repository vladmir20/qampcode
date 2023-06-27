/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Fragments;


import static com.qamp.app.MesiboApiClasses.SampleAPI.startContactsSync;
import static com.qamp.app.MessagingModule.MesiboConfiguration.CONTACT_PERMISSION_MESSAGE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PERMISSION_DENIED_CONTACTS;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PERMISSION_NEEDED;
import static com.qamp.app.Utils.Utilss.getLetterTile;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Activity.CommunityDashboard;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.MessagingModule.Contact;
import com.qamp.app.MessagingModule.RoundImageDrawable;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class CreateCommunityFour extends Fragment implements Mesibo.SyncListener, Backpressedlistener {

    private static final String[] PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    public static Backpressedlistener backpressedlistener;
    Button skip;
    ImageView cancel;
    ArrayList<Contact> contactList = new ArrayList<>();
    TextView channelName, shareInvite;
    RecyclerView contactsRecycle;
    MesiboProfile profile;
    ArrayList<String> mesiboProfileArrayList = new ArrayList<>();

    Button buttonNext;

    private ArrayList<MesiboProfile> mUserProfiles = null;
    private ArrayList<MesiboProfile> mSearchResultList = null;
    //ContactAdapter mAdapter = null;
    private CommunityContactAdapter listAdapter;
    private ArrayList<ContactCommunityData> contactsList = new ArrayList<>();

    private static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() >= 10) {
            // Extract the country code and remaining digits
            String countryCode = phoneNumber.substring(0, phoneNumber.length() - 10);
            String remainingDigits = phoneNumber.substring(phoneNumber.length() - 10);

            // Format the number with parentheses and country code
            return "(+" + countryCode + ") " + remainingDigits;
        } else {
            return phoneNumber; // Return the input as-is if it doesn't meet the format requirements
        }
    }

    private static String removeCountryCode(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() > 2) {
            return phoneNumber.substring(2); // Extract the portion of the number after the country code
        } else {
            return phoneNumber; // Return the input as-is if it doesn't have a valid country code
        }
    }
    String channelTitle,channelDescription,channelId,channelType,lat,lng,email,number,domain,invitationType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_four, container, false);
        checkPermissionAndFetchContacts();

        skip = view.findViewById(R.id.skip);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);
        shareInvite = view.findViewById(R.id.shareInvite);
        contactsRecycle = view.findViewById(R.id.contactRecycler);
        buttonNext = view.findViewById(R.id.buttonNext);

        Bundle bundle = getArguments();
        if (bundle != null) {
            channelTitle = bundle.getString("ChannelName");
            channelDescription = bundle.getString("ChannelDescription");
            channelName.setText(channelTitle);
            channelType = bundle.getString("ChannelBusinessType");
            lat = bundle.getString("lat");
            lng = bundle.getString("lng");
            email = bundle.getString("email");
            number = bundle.getString("number");
            domain = bundle.getString("domain");
            invitationType = bundle.getString("invitationType");
        }

        mUserProfiles = new ArrayList<>();

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtils.isNetWorkAvailable(getActivity())) {
                    AppUtils.openProgressDialog(getActivity());
                    createChannel();
                } else {
                    Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                }
            }
        });

//        shareInvite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                for (int i=0; i<mesiboProfileArrayList.size(); i++){
//                    System.out.println("Printed "+String.valueOf(i)+" "+mesiboProfileArrayList.get(i)+"\n");
//                }
//                if (AppUtils.isNetWorkAvailable(getActivity())) {
//                    AppUtils.openProgressDialog(getActivity());
//                    shareInviteToDb(mesiboProfileArrayList);
//                } else {
//                    Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        checkPermissionAndFetchContacts();
        showUserList();

        CommunityContactAdapter adapter = new CommunityContactAdapter(getActivity(), contactsList);
        contactsRecycle.setHasFixedSize(true);
        contactsRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
        contactsRecycle.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(contactsRecycle, false);
        adapter.notifyDataSetChanged();

//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSelectedAll) {
//                    adapter.selectAll();
//                    isSelectedAll = true;
//                    mesiboProfileArrayList.clear();
//                } else {
//                    isSelectedAll = false;
//                    adapter.selectAll();
//                    mesiboProfileArrayList.clear();
//                }
//            }
//        });
        return view;
    }

    private void createChannel() {
        String URL = QAMPAPIConstants.channel_base_url + String.format(QAMPAPIConstants.addChannel);
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
            jsonBody.put("emailid", email);
            jsonBody.put("mobileNumber", number);
            jsonBody.put("domain", domain);
            jsonBody.put("businessType", "BUSINESS");
            jsonBody.put("invitationType", invitationType);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AppUtils.closeProgresDialog();
                    try {
                        String status = response.getString("status");
                        Log.e("VOLLEY Status======", status);
                        if (status.contains(QampConstants.success)) {
                            JSONObject data = response.getJSONObject("data");
                            for (int i=0; i<mesiboProfileArrayList.size(); i++){
                                System.out.println("Printed "+String.valueOf(i)+" "+mesiboProfileArrayList.get(i)+"\n");
                            }
                            if (AppUtils.isNetWorkAvailable(getActivity())) {
//                                AppUtils.openProgressDialog(getActivity());
                                channelId = data.getString("uid");
                                shareInviteToDb(mesiboProfileArrayList);
                                System.out.println("Shivam done");
                            } else {
                                Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                            }
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


    private void shareInviteToDb(ArrayList<String> mesiboProfileArrayList) {
        final String URL = QAMPAPIConstants.channel_base_url + String.format(QAMPAPIConstants.sendbulkinvites); // Replace with your server URL
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("channelId", channelId);
            JSONArray invitedMobileNumbers = new JSONArray();
            for (int i = 0; i < mesiboProfileArrayList.size(); i++) {
                invitedMobileNumbers.put(removeCountryCode(mesiboProfileArrayList.get(i)));
            }
            jsonObject.put("invitedMobileNumbers", invitedMobileNumbers);
            jsonObject.put("countryCode", "91");


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            AppUtils.closeProgresDialog();
                            // Handle the server response
                            try {
                                // Save the response to SharedPreferences
                                //saveResponseToSharedPreferences(response.toString());
                                // Process the response JSON if needed
                                String status = response.getString("status");
                                if (status.contains(QampConstants.success)) {
                                    AppUtils.closeProgresDialog();
                                    JSONObject data = response.getJSONObject("data");
                                    String channelId = data.getString("channelId");
                                    String countryCode = data.getString("countryCode");
                                    JSONArray invitedMobileNumbers = data.getJSONArray("invitedMobileNumbers");
                                    Intent intent = new Intent(getActivity(), CommunityDashboard.class);
                                    startActivity(intent);
                                } else {

                                    System.out.println("channelID" + channelId);
                                    System.out.println("invitedMobileNumbers" + invitedMobileNumbers);
                                    System.out.println("user-session-token" + AppConfig.getConfig().token);
                                    AppUtils.closeProgresDialog();

                                    JSONArray errors = response.getJSONArray("error");
                                    JSONObject error = (JSONObject) errors.get(0);
                                    String errMsg = error.getString("errMsg");
                                    String errorCode = error.getString("errCode");
                                    Toast.makeText(getActivity(), "" + errMsg, Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(getActivity(), status, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
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
                    System.out.println("user-session-token" + AppConfig.getConfig().token);
                    return params;
                }
            };
            Volley.newRequestQueue(getContext()).add(request);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 225: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startContactsSync();
                    getContactList();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), PERMISSION_DENIED_CONTACTS, Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    @Override
    public void Mesibo_onSync(int i) {
        final int c = i;
        if (i > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //showUserList(MESIBO_INTITIAL_READ_USERLIST);
                }
            });

        }
    }

    private void getContactList() {
        ContentResolver cr = getContext().getContentResolver();
        contactList.removeAll(contactList);

        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while (cursor.moveToNext()) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    number = number.replace(" ", "");
                    number = number.replace("(", "");
                    number = number.replace(")", "");
                    number = number.replace("-", "");
                    if (!mobileNoSet.contains(number)) {
                        contactList.add(new Contact(name, number));
                        mobileNoSet.add(number);
                        Log.d("hvy", "onCreateView  Phone Number: name = " + name + " No = " + number);
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }

    public void showUserList() {
        mUserProfiles.clear();
        ArrayList<MesiboProfile> otherContactsList = new ArrayList<>();
        for (int j = 0; j < contactList.size(); j++) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Boolean state = true;
            try {
                // phone must begin with '+'
                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(contactList.get(j).phoneNumber, "");
            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
                state = false;
            }

            MesiboProfile userProfile = new MesiboProfile();
            profile = userProfile;
            userProfile.address = state == false ? "91" + contactList.get(j).phoneNumber : contactList.get(j).phoneNumber.substring(1);
            userProfile.setName(contactList.get(j).name);
            userProfile.setStatus("0");
            userProfile.draft = null;
            userProfile.groupid = 0;
            userProfile.lookedup = false;
            userProfile.other = null;
            otherContactsList.add(userProfile);
        }
        ArrayList<MesiboProfile> mesiboProfiles = Mesibo.getSortedUserProfiles();

        for (int j = 0; j < mesiboProfiles.size(); j++) {
            for (int i = 0; i < otherContactsList.size(); i++) {
                if (null != otherContactsList.get(i).address && otherContactsList.get(i).address.equals(mesiboProfiles.get(j).address)) {
                    mesiboProfiles.get(j).setName(otherContactsList.get(i).getName());
                    otherContactsList.remove(i);
                    break;
                }
            }
        }


        mUserProfiles.addAll(mesiboProfiles);
        //mUserProfiles.addAll(otherContactsList);


        if (mUserProfiles.size() == 0) {
            //subtitle.setText("");
        } else if (mUserProfiles.size() == 1) {
            //subtitle.setText(String.valueOf(mUserProfiles.size() + " Contact"));
        } else {
            // subtitle.setText(String.valueOf(mUserProfiles.size() + " Contacts"));
        }
        for (int i = 0; i < mUserProfiles.size(); i++) {
            if (!mUserProfiles.get(i).getAddress().toString().isEmpty()) {
                Bitmap b = mUserProfiles.get(i).getImage();
                if (b != null)
                    contactsList.add(new ContactCommunityData(mUserProfiles.get(i).getName(), mUserProfiles.get(i).getAddress(), b));
                else
                    contactsList.add(new ContactCommunityData(mUserProfiles.get(i).getName(), mUserProfiles.get(i).getAddress()));
            }
        }
    }

    private void checkPermissionAndFetchContacts() {
        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PermissionChecker.PERMISSION_GRANTED) {
            this.getContactList();
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            new AlertDialog.Builder(getContext()).setTitle(PERMISSION_NEEDED).setMessage(CONTACT_PERMISSION_MESSAGE).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 225);
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {

            if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 225);
            }
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

    public class CommunityContactAdapter extends RecyclerView.Adapter<CommunityContactAdapter.ViewHolder> {
        private final Context context;
        private final ArrayList<ContactCommunityData> contactCommunityData;

        public CommunityContactAdapter(Context context, ArrayList<ContactCommunityData> contactCommunityData) {
            this.context = context;
            this.contactCommunityData = contactCommunityData;
        }

        @Override
        public CommunityContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_contact_item_community, parent, false);
            CommunityContactAdapter.ViewHolder viewHolder = new CommunityContactAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(CommunityContactAdapter.ViewHolder holder, final int position) {
            final ContactCommunityData contact = contactCommunityData.get(position);
            holder.setContactName(contact.getName());
            holder.setContactNumber(contact.getNumber());
            if (contact.getProfilePic() != null)
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(contact.getProfilePic()));
            else
                holder.mContactsProfile.setImageDrawable(new RoundImageDrawable(getLetterTile(contact.getName())));

            if (holder.checkBox.isChecked())
                mesiboProfileArrayList.add(contact.getNumber());
            else
                mesiboProfileArrayList.remove(contact.getNumber());

            setShareNumber();

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        mesiboProfileArrayList.remove(contact.getNumber());
                    } else if (isChecked) {
                        mesiboProfileArrayList.add(contact.getNumber());
                    }
                    setShareNumber();
                }
            });

        }

        private void setShareNumber() {
            //   shareInvite.setText("" + getActivity().getResources().getString(R.string.share_invite_text) + " (" + mesiboProfileArrayList.size() + ")");
            shareInvite.setText(mesiboProfileArrayList.size() + " " + "Contacts Selected");
        }

        @Override
        public int getItemCount() {
            return contactCommunityData.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView txtName;
            private TextView txtNumber;
            private ImageView mContactsProfile;
            private CheckBox checkBox;

            public ViewHolder(View itemView) {
                super(itemView);
                txtName = itemView.findViewById(R.id.textView14);
                txtNumber = itemView.findViewById(R.id.number);
                mContactsProfile = itemView.findViewById(R.id.mes_rv_profile);
                checkBox = itemView.findViewById(R.id.checkBox);
            }

            public void setContactName(String name) {
                txtName.setText(name);
            }

            public void setContactNumber(String number) {
                String formattedNumber = formatPhoneNumber(number);
                if (formattedNumber != null) {
                    txtNumber.setText(formattedNumber);
                } else {
                    Log.d("MainActivity", "Invalid phone number");
                }
            }
        }
    }


}