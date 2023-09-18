package com.qamp.app.MesiboImpModules;

import static android.content.Context.TELEPHONY_SERVICE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Modal.ContactModel;
import com.qamp.app.Modal.QampContactScreenModel;
import com.qamp.app.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactSyncClass {
    /**
     * fetching
     * device contact first
     */
    public static ArrayList<ContactModel> deviceContactList = new ArrayList<>();
    public static ArrayList<QampContactScreenModel> contacts = new ArrayList<>();
    public static ArrayList<QampContactScreenModel> groupContacts = new ArrayList<>();


    public static void getDeviceContacts(Context context) {
        deviceContactList.clear();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        Set<String> contactSet = new HashSet<>(); // Use a Set to store unique contacts

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = formatPhoneNumber(phoneNumber, context);

                // Check if the contact is not a duplicate (based on phone number)
                if (!contactSet.contains(phoneNumber)) {
                    // Add the contact to the ArrayList and the Set
                    deviceContactList.add(new ContactModel(name, phoneNumber));
                    contactSet.add(phoneNumber);
                }
            }
            cursor.close();
        }
    }

    /**
     * comparing and getting mesibo profiles and seperating them
     */
    public static void getMyQampContacts(Context context) {
        getDeviceContacts(context);
        contacts.clear();
        groupContacts.clear();
        ArrayList<MesiboProfile> mesiboProfileArrayList = Mesibo.getSortedUserProfiles();
        mesiboProfileArrayList.add(Mesibo.getSelfProfile());
        Set<String> addedPhoneNumbers = new HashSet<>();
        for (int i = 0; i < deviceContactList.size(); i++) {
            String phoneNumber = deviceContactList.get(i).getNumber();
            for (int j = 0; j < mesiboProfileArrayList.size(); j++) {
                if (deviceContactList.get(i).getNumber().equals(mesiboProfileArrayList.get(j).getAddress())) {
                    // Check if the phone number has already been added before adding it again
                    Bitmap b = null;
                    MesiboProfile mesiboProfile = Mesibo.getProfile(phoneNumber);
                    b = mesiboProfile.getImage();
                    //b=null;
                    if (!addedPhoneNumbers.contains(phoneNumber)) {
                        if (b == null) {
                            Drawable drawable = context.getResources().getDrawable(R.drawable.person);
                            b = drawableToBitmap(drawable);
                        }
                        if (Mesibo.getSelfProfile().getAddress().equals(phoneNumber)) {
                            contacts.add(new QampContactScreenModel(deviceContactList.get(i).getName() + "(You)",
                                    phoneNumber, true, b));
                        } else {
                            contacts.add(new QampContactScreenModel(deviceContactList.get(i).getName(),
                                    phoneNumber, true, b));
                        }
                        groupContacts.add(new QampContactScreenModel(deviceContactList.get(i).getName(),
                                phoneNumber, true, b));
                        addedPhoneNumbers.add(phoneNumber); // Add the phone number to the set
                    }
                }
            }
        }

        for (int i = 0; i < deviceContactList.size(); i++) {
            String phoneNumber = deviceContactList.get(i).getNumber();
            if (!addedPhoneNumbers.contains(phoneNumber)) {
                Bitmap bmp = null;
                Drawable drawable = context.getResources().getDrawable(R.drawable.person);
                bmp = drawableToBitmap(drawable);
                contacts.add(new QampContactScreenModel(deviceContactList.get(i).getName(),
                        phoneNumber, false, bmp));
                addedPhoneNumbers.add(phoneNumber); // Add the phone number to the set
            }
        }
    }

    /**
     * Normalising my Phone number
     */
    public static String formatPhoneNumber(String phoneNumber, Context context) {
        // Remove all non-numeric characters from the phone number
        String formattedNumber = PhoneNumberUtils.stripSeparators(phoneNumber);

        // Get the country code from the TelephonyManager
        TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso().toUpperCase();

        // Remove any plus sign from the prefix
        formattedNumber = formattedNumber.replaceFirst("^\\+", "");
        return formattedNumber;
    }
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
