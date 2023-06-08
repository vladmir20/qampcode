/*
 * *
 *  * Created by Shivam Tiwari on 01/06/23, 11:54 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 01/06/23, 11:54 PM
 *
 */

package com.qamp.app.Utils;

import static com.qamp.app.MessagingModule.ContactsBottomSheetFragment.PROJECTION;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.MessagingModule.Contact;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public class ContantContantUtil {

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static ArrayList<MesiboProfile> mesiboProfileArrayList = Mesibo.getSortedUserProfiles();
    public static ArrayList<MesiboProfile> mUserProfiles = new ArrayList<MesiboProfile>();
    public static ArrayList<Contact> contactList = new ArrayList<>();

    public static String removePrefix(String s, String prefix) {
        if (s != null && prefix != null && s.startsWith(prefix)) {
            return s.substring(prefix.length());
        }
        return s;
    }

    public static void showUserList(int readCount, Context context, Activity activity) {
        //Android version is lesser than 6.0 or the permission is already granted.
            //setEmptyViewText();
            getContactList(context);
            if (readCount == 0) {
            } else {
                try {
                   // mUserProfiles.clear();
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
                        // userProfile.unread = 0;
                        userProfile.groupid = 0;
                        //userProfile.lastActiveTime = 0;
                        userProfile.lookedup = false;
                        userProfile.other = null;
                        otherContactsList.add(userProfile);


//                    ArrayList<MesiboProfile> mesiboProfileArrayList1 = Mesibo.getSortedUserProfiles();
//                    for (int j = 0; j < mesiboProfileArrayList1.size(); j++) {
//                        for (int i = 0; i < otherContactsList.size(); i++) {
//                            if (null != otherContactsList.get(i).address && otherContactsList.get(i).
//                                    address.equals(mesiboProfileArrayList1.get(j).address)) {
//                                mesiboProfileArrayList1.get(j).setName(otherContactsList.get(i).getName());
//                                otherContactsList.remove(i);
//                                break;
//                            }
//                        }
//                    }
//
//
//
//                    ArrayList<MesiboProfile> groupMainContactsList = new ArrayList<>();
//                    for (int i = 0; i < mesiboProfileArrayList1.size(); i++) {
//                        if (mesiboProfileArrayList1.get(i).isGroup())
//                            groupMainContactsList.add(mesiboProfileArrayList1.get(i));
//
//                    }
//                    mUserProfiles.addAll(mesiboProfileArrayList1);
                     mUserProfiles.addAll(otherContactsList);
//                    mUserProfiles.removeAll(groupMainContactsList);
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Some Error Occured" + e, Toast.LENGTH_SHORT).show();
                }
            }

    }

    public static void getContactList(Context context) {
        ContentResolver cr = context.getContentResolver();
        contactList.removeAll(contactList);
        ArrayList<MesiboProfile> mesiboProfileArrayList1 = Mesibo.getSortedUserProfiles();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor != null) {
            HashSet<String> mobileNoSet = new HashSet<String>();
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                int i=0;
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
                        Log.d("hvy", "onCreateView  Phone Number: name = " + name
                                + " No = " + number);
                    }
                    final String noToCheck = removePrefix(number, "+");
                    Optional<MesiboProfile> mesiboProfile = mesiboProfileArrayList1.stream().
                            parallel().filter(m -> m.getAddress().equals(noToCheck)).findFirst();
                    if (mesiboProfile.isPresent()) {
                        mUserProfiles.add(mesiboProfile.get());
                    }
                }
            } finally {
                cursor.close();
                System.out.println("check " + mUserProfiles.size());
            }
        }
    }

}
