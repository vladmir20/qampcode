package com.qamp.app.Activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.Adapter.QampContactScreenAdapter;
import com.qamp.app.MessagingModule.AllUtils.LetterTileProvider;
import com.qamp.app.Modal.DeviceContactModal;
import com.qamp.app.Modal.QampContactScreenModel;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class QampContactScreenNew extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 101;
    private RecyclerView recyclerView;
    private QampContactScreenAdapter adapter;
    private ArrayList<QampContactScreenModel> contactList;
    private ArrayList<DeviceContactModal> contactsList = new ArrayList<>();

    private TextView textView66;
    private EditText editTextTextPersonName5;

    private final char[] mFirstChar = new char[1];
    private final TextPaint mPaint = new TextPaint();
    private final int mTileLetterFontSize = 25;

    private final Rect mBounds = new Rect();
    private int[] mColors = {-957596, -686759, -416706, -1784274, -9977996, -10902850, -14642227, -5414233};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qamp_contact_screen);
        AppUtils.setStatusBarColor(QampContactScreenNew.this, R.color.colorAccent);
        recyclerView = findViewById(R.id.contacts);
        textView66 = findViewById(R.id.textView66);
        editTextTextPersonName5 = findViewById(R.id.editTextTextPersonName5);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            // Permission has already been granted
            readContacts();
        }
        contactList = getContactData();
        adapter = new QampContactScreenAdapter(this, contactList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editTextTextPersonName5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void readContacts() {
        ContentResolver contentResolver = getContentResolver();
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
                phoneNumber = formatPhoneNumber(phoneNumber);
                // Add the contact to the ArrayList
                contactsList.add(new DeviceContactModal(name, phoneNumber));
            }
            cursor.close();
        }
        textView66.setText(contactsList.size() + " Contacts");
    }


    private String formatPhoneNumber(String phoneNumber) {
        // Remove all non-numeric characters from the phone number
        String formattedNumber = PhoneNumberUtils.stripSeparators(phoneNumber);

        // Get the country code from the TelephonyManager
        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso().toUpperCase();

        // Remove any plus sign from the prefix
        formattedNumber = formattedNumber.replaceFirst("^\\+", "");
        return formattedNumber;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch contacts
                readContacts();
            } else {
                // Permission denied, show a message or handle it gracefully
                Toast.makeText(this, "Read contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ArrayList<QampContactScreenModel> getContactData() {
        ArrayList<QampContactScreenModel> contacts = new ArrayList<>();
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
                        contacts.add(new QampContactScreenModel(false, contactsList.get(i).getName(),
                                phoneNumber, true, false, b));
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
                    this.mFirstChar[0] = Character.toUpperCase(firstChar);
                } else {
                    this.mFirstChar[0] = firstChar;
                }
                this.mPaint.setTextSize((float) this.mTileLetterFontSize);
                this.mPaint.getTextBounds(this.mFirstChar, 0, 1, this.mBounds);
                c.drawText(this.mFirstChar, 0, 1, (float) ((width / 2) + 0), (float) ((height / 2) + 0 + ((this.mBounds.bottom - this.mBounds.top) / 2)), this.mPaint);

                contacts.add(new QampContactScreenModel(false, contactsList.get(i).getName(),
                        phoneNumber, false, false, bmp));
                addedPhoneNumbers.add(phoneNumber); // Add the phone number to the set
            }
        }
        return contacts;
    }
    public int pickColor(String key) {
        if (TextUtils.isEmpty(key)) {
            return 0;
        }
        return this.mColors[Math.abs(key.hashCode()) % this.mColors.length];
    }
    private static boolean isEnglishLetterOrDigit(char c) {
        return ('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z') || ('0' <= c && c <= '9');
    }

}