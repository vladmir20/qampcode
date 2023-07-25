package com.qamp.app.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qamp.app.Adapter.QampContactScreenAdapter;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.ContactSyncClass;

public class QampContactScreenNew extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 101;
    private RecyclerView recyclerView;
    private QampContactScreenAdapter adapter;

    private TextView textView66;
    private EditText editTextTextPersonName5;

    private ImageView imageView53;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qamp_contact_screen);
        AppUtils.setStatusBarColor(QampContactScreenNew.this, R.color.colorAccent);
        recyclerView = findViewById(R.id.contacts);
        textView66 = findViewById(R.id.textView66);
        editTextTextPersonName5 = findViewById(R.id.editTextTextPersonName5);
        imageView53 = findViewById(R.id.imageView53);
        textView66.setText(ContactSyncClass.contactsList.size()+" Contacts");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            // Permission has already been granted
            // ContactSyncClass.getContactData();
        }
        adapter = new QampContactScreenAdapter(this, ContactSyncClass.contacts);
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
        imageView53.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}