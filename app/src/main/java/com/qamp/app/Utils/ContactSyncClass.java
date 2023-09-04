package com.qamp.app.Utils;

import static android.content.Context.TELEPHONY_SERVICE;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextPaint;
import android.text.TextUtils;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Modal.DeviceContactModal;
import com.qamp.app.Modal.QampContactScreenModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactSyncClass {
    public static final int REQUEST_READ_CONTACTS = 101;
    public static final char[] mFirstChar = new char[1];
    public static final TextPaint mPaint = new TextPaint();
    public static final int mTileLetterFontSize = 25;
    public static final Rect mBounds = new Rect();
    public static ArrayList<DeviceContactModal> contactsList = new ArrayList<>();
    public static int[] mColors = {-957596, -686759, -416706, -1784274, -9977996, -10902850, -14642227, -5414233};

    public static Context mContext;

    public static ArrayList<QampContactScreenModel> contacts = new ArrayList<>();
    public static ArrayList<QampContactScreenModel> groupContacts = new ArrayList<>();
    public static int totalcontacts = 0;

    public static void readContacts() {
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = formatPhoneNumber(phoneNumber, mContext);
                // Add the contact to the ArrayList
                contactsList.add(new DeviceContactModal(name, phoneNumber));

            }
            cursor.close();
        }
    }

    public static ArrayList<QampContactScreenModel> getContactData(Context Mcontext) {
        mContext = Mcontext;
        contactsList.clear();
        readContacts();
        contacts.clear();
        groupContacts.clear();
        ArrayList<MesiboProfile> mesiboProfileArrayList = Mesibo.getSortedUserProfiles();
        Set<String> addedPhoneNumbers = new HashSet<>();
        for (int i = 0; i < contactsList.size(); i++) {
            String phoneNumber = contactsList.get(i).getPhoneNumber();
            for (int j = 0; j < mesiboProfileArrayList.size(); j++) {
                if (contactsList.get(i).getPhoneNumber().equals(mesiboProfileArrayList.get(j).getAddress())) {
                    // Check if the phone number has already been added before adding it again
                    if (!addedPhoneNumbers.contains(phoneNumber)) {
                        Bitmap b = null;
                        MesiboProfile mesiboProfile = Mesibo.getProfile(phoneNumber);
                        b = mesiboProfile.getImage();
                        //b=null;
                        if (b == null) {
                            int width = 35;
                            int height = 35;
                            if (true) {
                                b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            }
                            char firstChar = '*';
                            Canvas c = new Canvas();
                            c.setBitmap(b);
                            if (!TextUtils.isEmpty(contactsList.get(i).getName())) {
                                firstChar = contactsList.get(i).getName().charAt(0);
                            }
                            c.drawColor(pickColor(contactsList.get(i).getName()));
                            if (isEnglishLetterOrDigit(firstChar)) {
                                mFirstChar[0] = Character.toUpperCase(firstChar);
                            } else {
                                mFirstChar[0] = firstChar;
                            }
                            mPaint.setTextSize((float) mTileLetterFontSize);
                            mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);

                            // Calculate the center coordinates for x and y
                            int centerX = width / 2;
                            int centerY = height / 2;

                            // Calculate the text size and position
                            mPaint.setTextSize((float) mTileLetterFontSize);
                            mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);
                            float textX = centerX - mBounds.exactCenterX();
                            float textY = centerY - mBounds.exactCenterY();
                            c.drawText(mFirstChar, 0, 1, textX, textY, mPaint); }
                        contacts.add(new QampContactScreenModel( contactsList.get(i).getName(),
                                phoneNumber, true, b));
                        groupContacts.add(new QampContactScreenModel( contactsList.get(i).getName(),
                                phoneNumber, true, b));
                        addedPhoneNumbers.add(phoneNumber); // Add the phone number to the set
                    }
                }
            }
        }

        for (int i = 0; i < contactsList.size(); i++) {
            String phoneNumber = contactsList.get(i).getPhoneNumber();
            if (!addedPhoneNumbers.contains(phoneNumber)) {
                Bitmap b = null;
                int width = 35;
                int height = 35;
                Bitmap bmp;
                if (true) {
                    bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                }
                char firstChar = '*';
                Canvas c = new Canvas();
                c.setBitmap(bmp);
                if (!TextUtils.isEmpty(contactsList.get(i).getName())) {
                    firstChar = contactsList.get(i).getName().charAt(0);
                }
                c.drawColor(pickColor(contactsList.get(i).getName()));
                if (isEnglishLetterOrDigit(firstChar)) {
                    mFirstChar[0] = Character.toUpperCase(firstChar);
                } else {
                    mFirstChar[0] = firstChar;
                }
                mPaint.setTextSize((float) mTileLetterFontSize);
                mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);

                // Calculate the center coordinates for x and y
                int centerX = width / 2;
                int centerY = height / 2;

                // Calculate the text size and position
                mPaint.setTextSize((float) mTileLetterFontSize);
                mPaint.getTextBounds(mFirstChar, 0, 1, mBounds);
                float textX = centerX - mBounds.exactCenterX();
                float textY = centerY - mBounds.exactCenterY();
                c.drawText(mFirstChar, 0, 1, textX, textY, mPaint);

                 //c.drawText(mFirstChar, 0, 1, (float) ((width / 2) + 0), (float) ((height / 2) + 0 + ((mBounds.bottom - mBounds.top) / 2)), mPaint);

                contacts.add(new QampContactScreenModel( contactsList.get(i).getName(),
                        phoneNumber, false,  bmp));
                addedPhoneNumbers.add(phoneNumber); // Add the phone number to the set
            }
        }
        return contacts;
    }

    private static boolean isEnglishLetterOrDigit(char c) {
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9');
    }

    public static int pickColor(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }
        return mColors[Math.abs(key.hashCode()) % mColors.length];
    }

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
}
