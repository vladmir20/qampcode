package com.qamp.app;



import static com.qamp.app.messaging.MesiboConfiguration.CONTACT_PERMISSION_MESSAGE;
import static com.qamp.app.messaging.MesiboConfiguration.MESIBO_INTITIAL_READ_USERLIST;
import static com.qamp.app.messaging.MesiboConfiguration.PERMISSION_DENIED_CONTACTS;
import static com.qamp.app.messaging.MesiboConfiguration.PERMISSION_NEEDED;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.messaging.Contact;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class CreateCommunityFour extends Fragment implements Mesibo.SyncListener {

    private static final String[] PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    Button skip, shareInvite;
    ImageView cancel;
    ArrayList<Contact> contactList = new ArrayList<>();
    String lat, lng, channelTitle, channelDescription;
    TextView channelName;
    String latitude, longitude;
    private ArrayList<MesiboProfile> mUserProfiles = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_four, container, false);
        checkPermissionAndFetchContacts();

        skip = view.findViewById(R.id.skip);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);
        shareInvite = view.findViewById(R.id.button);

        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getString("Latitude");
            lng = bundle.getString("Longitude");
            channelTitle = bundle.getString("ChannelName");
            channelDescription = bundle.getString("ChannelDescription");
            channelName.setText(channelTitle);
            latitude = String.valueOf(lat);
            longitude = String.valueOf(lng);
        }

        mUserProfiles = new ArrayList<>();

        shareInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CommunityDashboard.class);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        //Intent intent= new Intent(getActivity(), CommunityDashboard.class);
        //                startActivity(intent);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                String URL = QAMPAPIConstants.channel_base_url + String.format(QAMPAPIConstants.addChannel);
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("title", channelTitle);
                    jsonBody.put("description", channelDescription);
                    jsonBody.put("type", "BUSINESS");
                    jsonBody.put("haveGeoLocation", "true");
                    jsonBody.put("latitude", latitude);
                    jsonBody.put("longitude", longitude);

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
                                //Toast.makeText(getActivity(), ""+respons, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), CommunityDashboard.class);
                                intent.putExtra("Name", channelTitle.toString());
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "UNSUCCESS CREATED"+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        System.out.println(AppConfig.getConfig().token.toString());
                        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
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
                            for (Map.Entry<String, Object> entry : params.entrySet()) {
                                System.out.println(entry.getKey() + ":" + entry.getValue().toString());
                            }
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
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 225: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    MesiboAPI.startContactsSync();
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
                    showUserList(MESIBO_INTITIAL_READ_USERLIST);
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

    public void showUserList(int readCount) {
        Log.d("showUserList", "showUserList");

        //setEmptyViewText();


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
            userProfile.address = state == false ? "91" + contactList.get(j).phoneNumber : contactList.get(j).phoneNumber.substring(1);
            userProfile.setName(contactList.get(j).name);
            userProfile.setStatus("0");
            userProfile.draft = null;
            //userProfile.unread = 0;
            userProfile.groupid = 0;
//            userProfile.lastActiveTime = 0;
            userProfile.lookedup = false;
            userProfile.other = null;
            otherContactsList.add(userProfile);
        }

        for (int j = 0; j < Mesibo.getSortedUserProfiles().size(); j++) {
            for (int i = 0; i < otherContactsList.size(); i++) {
                if (null != otherContactsList.get(i).address && otherContactsList.get(i).address.equals(Mesibo.getSortedUserProfiles().get(j).address)) {
                    Mesibo.getSortedUserProfiles().get(j).setName(otherContactsList.get(i).getName());
                    otherContactsList.remove(i);
                    break;
                }
            }
        }

        mUserProfiles.addAll(Mesibo.getSortedUserProfiles());
        mUserProfiles.addAll(otherContactsList);


        /**if (mUserProfiles.size() == 0) {
         subtitle.setText("");
         } else if (mUserProfiles.size() == 1) {
         subtitle.setText(String.valueOf(mUserProfiles.size() + " Contact"));
         } else {
         subtitle.setText(String.valueOf(mUserProfiles.size() + " Contacts"));
         }**/

        //mAdapter.notifyChangeInData();
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


}