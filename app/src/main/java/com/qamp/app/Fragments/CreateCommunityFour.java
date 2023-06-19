/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Fragments;


import static com.qamp.app.MesiboApiClasses.SampleAPI.startContactsSync;
import static com.qamp.app.Utils.Utilss.getLetterTile;
import static com.qamp.app.MessagingModule.MesiboConfiguration.CONTACT_PERMISSION_MESSAGE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PERMISSION_DENIED_CONTACTS;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PERMISSION_NEEDED;

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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Activity.CommunityDashboard;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.MessagingModule.Contact;
import com.qamp.app.MessagingModule.RoundImageDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */



import static com.qamp.app.Utils.Utilss.getLetterTile;
import static com.qamp.app.MessagingModule.MesiboConfiguration.CONTACT_PERMISSION_MESSAGE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PERMISSION_DENIED_CONTACTS;
import static com.qamp.app.MessagingModule.MesiboConfiguration.PERMISSION_NEEDED;

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
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Activity.CommunityDashboard;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Listener.Backpressedlistener;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.MessagingModule.Contact;
import com.qamp.app.MessagingModule.RoundImageDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class CreateCommunityFour extends Fragment implements Mesibo.SyncListener, Backpressedlistener {

    private static final String[] PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
    public static Backpressedlistener backpressedlistener;
    Button skip, shareInvite;
    ImageView cancel;
    ArrayList<Contact> contactList = new ArrayList<>();
    String lat, lng, channelTitle, channelDescription;
    TextView channelName;
    String latitude, longitude;
    RecyclerView contactsRecycle;
    MesiboProfile profile;
    ArrayList<String> mesiboProfileArrayList = new ArrayList<>();
    LinearLayout button3;
    boolean isSelectedAll = false;
    private ArrayList<MesiboProfile> mUserProfiles = null;
    private ArrayList<MesiboProfile> mSearchResultList = null;
    //ContactAdapter mAdapter = null;
    private CommunityContactAdapter listAdapter;
    private ArrayList<ContactCommunityData> contactsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.add_channel_four, container, false);
        checkPermissionAndFetchContacts();

        skip = view.findViewById(R.id.skip);
        cancel = view.findViewById(R.id.cancel_1);
        channelName = view.findViewById(R.id.channelName);
        shareInvite = view.findViewById(R.id.button);
        contactsRecycle = view.findViewById(R.id.contactRecycler);
        button3 = view.findViewById(R.id.button3);
        //OpenContactRecycler();

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

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedAll) {
                    adapter.selectAll();
                    isSelectedAll = true;
                    mesiboProfileArrayList.clear();
                } else {
                    isSelectedAll = false;
                    adapter.selectAll();
                    mesiboProfileArrayList.clear();
                }
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

            if (!isSelectedAll) {
                holder.checkBox.setChecked(false);
                isSelectedAll = false;
            } else {
                holder.checkBox.setChecked(true);
                isSelectedAll = true;
            }

            if (holder.checkBox.isChecked())
                mesiboProfileArrayList.add(contact.getNumber());
            else
                mesiboProfileArrayList.remove(contact.getNumber());

            setShareNumber();

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked){
                        mesiboProfileArrayList.remove(contact.getNumber());
                    }else if ((!isSelectedAll)&&isChecked){
                        mesiboProfileArrayList.add(contact.getNumber());
                    }
                    setShareNumber();
                }
            });

        }

        private void setShareNumber() {
            shareInvite.setText("" + getActivity().getResources().getString(R.string.share_invite_text) + " (" + mesiboProfileArrayList.size() + ")");
        }

        @Override
        public int getItemCount() {
            return contactCommunityData.size();
        }

        public void selectAll() {
            Log.e("onClickSelectAll", "yes");
            isSelectedAll = true;
            notifyDataSetChanged();
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
                txtNumber.setText(number);
            }
        }

    }
}
//public class CreateCommunityFour extends Fragment implements Mesibo.SyncListener, Backpressedlistener {
//    public static Backpressedlistener backpressedlistener;
//    Button next;
//    ImageView cancel;
//    TextView channelName;
//    String channelTitle, channelDescription, channelBusinessType, channel_ID;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.add_channel_four, container, false);
//
//        cancel = view.findViewById(R.id.cancel_1);
//        channelName = view.findViewById(R.id.channelName);
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            channelTitle = bundle.getString("Channel_Title");
//            channelDescription = bundle.getString("Channel_Description");
//            channelBusinessType = bundle.getString("Channel_Type");
//            channel_ID = bundle.getString("Channel_ID");
//            channelName.setText(channelTitle);
//        }
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getActivity().finish();
//            }
//        });
//
//        return view;
//    }
//
//    @Override
//    public void Mesibo_onSync(int i) {
//
//    }
//
//    @Override
//    public void onBackPressed() {
//
//    }
//
//    @Override
//    public void onPause() {
//        backpressedlistener = null;
//        super.onPause();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        backpressedlistener = this;
//    }
//}