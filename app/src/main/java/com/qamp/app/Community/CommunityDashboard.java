package com.qamp.app.Community;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.qamp.app.CommunityFragments.ChannelMetaData.Fragments.AboutFragment;
import com.qamp.app.CommunityFragments.Chats.Fragments.ChatsFragment;
import com.qamp.app.CommunityFragments.Feed.Fragments.FeedFragment;
import com.qamp.app.CommunityFragments.Insights.Fragments.InsightsFragment;
import com.qamp.app.CommunityFragments.PinBoard.Fragments.PinBoardFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

public class CommunityDashboard extends AppCompatActivity {

    String name;
    TextView channelTitle;
    Button createPost;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_dashboard);
        AppUtils.setStatusBarColor(CommunityDashboard.this,R.color.colorAccent);
        tabLayout = findViewById(R.id.navigation);
        viewPager = findViewById(R.id.body);
        channelTitle = findViewById(R.id.name);


        AlertDialog.Builder builder
                = new AlertDialog.Builder(this);
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.welcome_dialog,
                        null);
        builder.setView(customLayout);
        AlertDialog dialog
                = builder.create();
//        createPost = dialog.findViewById(R.id.button6);
//        createPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addPost();
//            }
//        });
        dialog.show();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        if (bundle != null) {

            name = (String) bundle.get("Name");
            channelTitle.setText(name);
        }


        CommunityTabAdapter communityTabAdapter = new CommunityTabAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        communityTabAdapter.addFragment(new ChatsFragment(), "Chats");
        communityTabAdapter.addFragment(new FeedFragment(), "Feed");
        communityTabAdapter.addFragment(new PinBoardFragment(), "Pin Board");
        communityTabAdapter.addFragment(new InsightsFragment(), "Insights");
        communityTabAdapter.addFragment(new AboutFragment(), "About");
        viewPager.setAdapter(communityTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void addPost() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(R.layout.add_post);
        dialog.show();
    }
}