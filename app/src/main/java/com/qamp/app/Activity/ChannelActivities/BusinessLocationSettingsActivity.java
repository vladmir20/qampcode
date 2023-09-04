package com.qamp.app.Activity.ChannelActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputEditText;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class BusinessLocationSettingsActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Utilss.setLanguage(BusinessLocationSettingsActivity.this);
        setContentView(R.layout.business_location_edit_layout);
        AppUtils.setStatusBarColor(BusinessLocationSettingsActivity.this, R.color.colorAccent);
        initViews();
    }

    private void initViews() {
        frameLayout = findViewById(R.id.frameLayout);
        ImageView crossButton = findViewById(R.id.crossButton);
        TextView saveButton = findViewById(R.id.saveButton);


        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mainActivity = new Intent(BusinessLocationSettingsActivity.this, ChannelAdminSettingsActivity.class);
                startActivity(mainActivity);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BusinessLocationSettingsActivity.this, "Save button clicked", Toast.LENGTH_SHORT).show();
            }
        });
//        TextInputEditText locationEditText = findViewById(R.id.created);
//
//        locationEditText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                locationEditText.setText(""); // Make the text within the EditText vanish
//            }
//        });
    }
}