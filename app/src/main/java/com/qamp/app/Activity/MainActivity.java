package com.qamp.app.Activity;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.qamp.app.Fragment.ChatFragment;
import com.qamp.app.Fragment.DiscoverFragment;
import com.qamp.app.Fragment.FeedFragment;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

public class MainActivity extends AppCompatActivity {
    // Define constants for fragment IDs
    private static final int FRAGMENT_CHAT = 1;
    private static final int FRAGMENT_FEED = 2;
    private static final int FRAGMENT_DISCOVER = 3;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private int activeFragmentId = FRAGMENT_CHAT; // Set the initial active fragment ID

    private LinearLayout chatNav, feedNav, discoverNav;
    private TextView chatText, feedText, discoverText;


    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppUtils.setStatusBarColor(MainActivity.this, R.color.colorAccent);
        chatNav = findViewById(R.id.chats_nav_card);
        feedNav = findViewById(R.id.feed_nav_card);
        discoverNav = findViewById(R.id.discover_nav_card);

        chatText = findViewById(R.id.chat_text);
        feedText = findViewById(R.id.feed_text);
        discoverText = findViewById(R.id.discover_text);
        initComponent();
        initToolbar();
        initNavigationMenu();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void initComponent() {

        // Initialize your fragments
        final ChatFragment chatFragment = new ChatFragment();
        final FeedFragment feedFragment = new FeedFragment();
        final DiscoverFragment discoverFragment = new DiscoverFragment();

        // Set the initial fragment
        switchFragment(chatFragment);
        updateBottomNavigationState(chatNav, chatText);

        // Set click listeners for each custom bottom navigation item
        chatNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(chatFragment);
                updateBottomNavigationState(chatNav, chatText);
            }
        });

        feedNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(feedFragment);
                updateBottomNavigationState(feedNav, feedText);
            }
        });

        discoverNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(discoverFragment);
                updateBottomNavigationState(discoverNav, discoverText);
            }
        });
    }


    private void updateBottomNavigationState(LinearLayout selectedCardView, TextView selectedTextView) {
        // Reset the UI state for all CardViews and TextViews
        resetBottomNavigationState();

        // Update the selected CardView's background color
        selectedCardView.setBackground(getResources().getDrawable(R.drawable.corner_radius_button));

        // Update the selected TextView's text color
        selectedTextView.setTextColor(ContextCompat.getColor(this, R.color.buttonActiveText));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            selectedTextView.setTypeface(getResources().getFont(R.font.inter_semibold));
        }

    }

    private void resetBottomNavigationState() {
        //Reset the UI state for all CardViews
        chatNav.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_color));
        feedNav.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_color));
        discoverNav.setBackgroundColor(ContextCompat.getColor(this, R.color.transparent_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chatText.setTypeface(getResources().getFont(R.font.inter_regular));
            feedText.setTypeface(getResources().getFont(R.font.inter_regular));
            discoverText.setTypeface(getResources().getFont(R.font.inter_regular));
        }

     }


    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(android.R.color.black), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the drawer
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    private void initNavigationMenu() {
        NavigationView nav_view = findViewById(R.id.nav_view);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // open drawer at start
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        AppUtils.changeMenuIconColor(menu, getResources().getColor(android.R.color.black));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
