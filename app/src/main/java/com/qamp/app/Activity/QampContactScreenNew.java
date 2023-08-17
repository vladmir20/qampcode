package com.qamp.app.Activity;

import static com.qamp.app.Adapter.QampContactScreenAdapter.slectedgtoup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mesibo.api.MesiboProfile;
import com.qamp.app.Adapter.QampContactScreenAdapter;
import com.qamp.app.MessagingModule.CreateNewGroupActivity;
import com.qamp.app.MessagingModule.UserListFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.ContactSyncClass;

import java.util.Iterator;

public class QampContactScreenNew extends AppCompatActivity {
    private static final int REQUEST_READ_CONTACTS = 101;
    private RecyclerView recyclerView;
    private QampContactScreenAdapter adapter;

    private TextView textView66,create_group;
    private EditText editTextTextPersonName5;

    private ImageView imageView53;

    private LinearLayout next_group;


    private boolean isGroupMakingProcedureActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qamp_contact_screen);
        AppUtils.setStatusBarColor(QampContactScreenNew.this, R.color.colorAccent);
        recyclerView = findViewById(R.id.contacts);
        textView66 = findViewById(R.id.textView66);
        create_group = findViewById(R.id.create_group);
        editTextTextPersonName5 = findViewById(R.id.editTextTextPersonName5);
        imageView53 = findViewById(R.id.imageView53);
        next_group = findViewById(R.id.next_group);
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

        if (!isGroupMakingProcedureActive){
            showList(false, next_group);
            isGroupMakingProcedureActive = false;
        }

        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGroupMakingProcedureActive){
                    next_group.setVisibility(View.VISIBLE);
                    showList(true,next_group);
                    isGroupMakingProcedureActive = true;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        create_group.setBackgroundColor(QampContactScreenNew.this.getColor(R.color.colorPrimary));
                    }
                }else{
                    next_group.setVisibility(View.GONE);
                    showList(false,next_group);
                    isGroupMakingProcedureActive = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        create_group.setBackgroundColor(QampContactScreenNew.this.getColor(R.color.white));
                    }
                }
            }
        });
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

        next_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserListFragment.mMemberProfiles.clear();
                if (slectedgtoup.size() >= 2) {
                    Iterator<MesiboProfile> it = slectedgtoup.iterator();
                    while (it.hasNext()) {
                        MesiboProfile d = it.next();
                        UserListFragment.mMemberProfiles.add(d);
                    }
                    //  MesiboUIManager.launchGroupActivity(ContactsBottomSheetFragment.this.getActivity(), (Bundle) null);
                    Intent intent = new Intent(QampContactScreenNew.this, CreateNewGroupActivity.class);
                    startActivity(intent);

                } else if (slectedgtoup.size() == 0 || slectedgtoup.size() == 1) {
                    Toast.makeText(QampContactScreenNew.this, "Please Select atleast two members to create group", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showList(boolean isGroupMaking, LinearLayout next_group) {
        adapter = new QampContactScreenAdapter(QampContactScreenNew.this,
                ContactSyncClass.contacts, isGroupMaking , next_group);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(QampContactScreenNew.this));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}