package com.qamp.app.Activity.ChannelActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.qamp.app.Fragments.ChannelFragments.BusinessDataCreation;
import com.qamp.app.Fragments.ChannelFragments.BusinessInteractionType;
import com.qamp.app.Fragments.ChannelFragments.BusinessInvitaionAndCreation;
import com.qamp.app.Fragments.ChannelFragments.BusinessLocationData;
import com.qamp.app.MessagingModule.MesiboUserListActivityNew;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class AddChannelActivities extends AppCompatActivity {
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(AddChannelActivities.this);
        setContentView(R.layout.acitivities_add_chanel);
        AppUtils.setStatusBarColor(AddChannelActivities.this, R.color.colorAccent);
        frameLayout = findViewById(R.id.frameLayout);
        BusinessDataCreation createCommunity = new BusinessDataCreation();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, createCommunity,"BusinessDataCreation");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().findFragmentByTag("BusinessDataCreation") != null &&
                getSupportFragmentManager().findFragmentByTag("BusinessDataCreation").isVisible()) {
            BusinessDataCreation.backpressedlistener.onBackPressed();
        }
        else if (getSupportFragmentManager().findFragmentByTag("BusinessLocationData") != null &&
                getSupportFragmentManager().findFragmentByTag("BusinessLocationData").isVisible()) {
            BusinessLocationData.backpressedlistener.onBackPressed();
        }
        else if (getSupportFragmentManager().findFragmentByTag("BusinessInteractionType") != null &&
                getSupportFragmentManager().findFragmentByTag("BusinessInteractionType").isVisible()) {
            BusinessInteractionType.backpressedlistener.onBackPressed();
        }
        else if (getSupportFragmentManager().findFragmentByTag("BusinessInvitaionAndCreation") != null &&
                getSupportFragmentManager().findFragmentByTag("BusinessInvitaionAndCreation").isVisible()) {
            BusinessInvitaionAndCreation.backpressedlistener.onBackPressed();
        }
        else{
            super.onBackPressed();
            Intent mainActivity = new Intent(AddChannelActivities.this, MesiboUserListActivityNew.class);
            startActivity(mainActivity);
        }
    }
}
