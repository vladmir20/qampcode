package com.qamp.app.AddChannel;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.qamp.app.AddChannel.Fragments.CreateCommunityFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

public class AddChannelActivity extends AppCompatActivity {


    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_channel);
        AppUtils.setStatusBarColor(AddChannelActivity.this, R.color.colorAccent);
        frameLayout = findViewById(R.id.frameLayout);

        CreateCommunityFragment createCommunity = new CreateCommunityFragment();

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, createCommunity);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}