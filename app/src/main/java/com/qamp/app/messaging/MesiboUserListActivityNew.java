package com.qamp.app.messaging;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboMessage;
import com.mesibo.api.MesiboProfile;
import com.mesibo.messaging.RoundImageDrawable;
import com.qamp.app.Fragments.DiscoverFragment;
import com.qamp.app.Fragments.FeedFragment;
import com.qamp.app.R;

public class MesiboUserListActivityNew extends AppCompatActivity implements MesiboProfile.Listener, MesiboUserListFragment.FragmentListener {
    public static final String TAG = "MesiboMainActivity";
    public ImageView hamburgerButton;
    BottomNavigationView navView;
    LinearLayout name_tite_layout, search_view;
    TextView contactsSubTitle = null;
    TextView contactsTitle = null;
    Bundle mEditGroupBundle = null;
    long mForwardId = 0;
    long[] mForwardIds;
    boolean mHideHomeBtn = false;
    boolean mKeepRunning = false;
    MesiboUI.Config mMesiboUIOptions = null;
    int mMode = 0;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private Toolbar toolbar;
    private LinearLayout edit_Profile;
    private LinearLayout logout;
    private ImageButton btn_close_filter;
    private TextView name_text, viewProfile_text;
    private LinearLayout shareViaWhatsapp;
    private ImageView circleProfileView;
    private ImageView search_image;
    private long pressedTime;
    private long mGroupId = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.navigation_dashboard:
                    if (search_image.getVisibility() == View.GONE)
                        search_image.setVisibility(View.VISIBLE);
                    MesiboUserListFragment fragment = new MesiboUserListFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content, fragment, "");
                    fragmentTransaction.commit();
                    return true;

                case R.id.navigation_home:
                    search_image.setVisibility(View.GONE);
                    if (search_view.getVisibility() == View.VISIBLE) {
                        search_view.setVisibility(View.GONE);
                        name_tite_layout.setVisibility(View.VISIBLE);
                    }
                    FeedFragment fragment1 = new FeedFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content, fragment1);
                    fragmentTransaction1.commit();
                    return true;

                case R.id.navigation_notifications:
                    search_image.setVisibility(View.GONE);
                    if (search_view.getVisibility() == View.VISIBLE) {
                        search_view.setVisibility(View.GONE);
                        name_tite_layout.setVisibility(View.VISIBLE);
                    }
                    DiscoverFragment fragment2 = new DiscoverFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "");
                    fragmentTransaction2.commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        MesiboUserListActivityNew.super.onCreate(savedInstanceState);
        if (!Mesibo.isReady()) {
            finish();
            return;
        }
        MesiboImages.init(this);
        this.mMode = getIntent().getIntExtra(MesiboUserListFragment.MESSAGE_LIST_MODE, MesiboUserListFragment.MODE_MESSAGELIST);
        this.mForwardId = getIntent().getLongExtra("mid", 0);
        this.mForwardIds = getIntent().getLongArrayExtra(MesiboUI.MESSAGE_IDS);
        String forwardMessage = getIntent().getStringExtra("message");
        boolean forwardAndClose = getIntent().getBooleanExtra(MesiboUI.FORWARD_AND_CLOSE, false);
        this.mKeepRunning = getIntent().getBooleanExtra(MesiboUI.KEEP_RUNNING, false);
        if (getIntent().getBooleanExtra(MesiboUI.START_IN_BACKGROUND, false)) {
            moveTaskToBack(true);
        }
        if (this.mMode == MesiboUserListFragment.MODE_EDITGROUP) {
            this.mEditGroupBundle = getIntent().getBundleExtra(MesiboUI.BUNDLE);
        }
        MesiboUIManager.enableSecureScreen(this);
        //setContentView(R.layout.activity_messages);
        this.mMesiboUIOptions = MesiboUI.getConfig();
//        Toolbar toolbar = findViewById(R.id.message_toolbar);
//        this.contactsSubTitle = (TextView) findViewById(R.id.contacts_subtitle);
//        this.contactsTitle = (TextView) findViewById(R.id.contacts_title);
        Utils.setTextViewColor(this.contactsTitle, MesiboUI.getConfig().mToolbarTextColor);
        Utils.setTextViewColor(this.contactsSubTitle, MesiboUI.getConfig().mToolbarTextColor);
        Utils.setActivityStyle(this, toolbar);
        setSupportActionBar(toolbar);
        // Utils.setActivityStyle(this, toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = this.mMesiboUIOptions.messageListTitle;
        if (TextUtils.isEmpty(title)) {
            title = Mesibo.getAppName();
        }
//        this.contactsTitle.setText(title);
        if (this.mMode == MesiboUserListFragment.MODE_MESSAGELIST) {
//            this.contactsSubTitle.setText(this.mMesiboUIOptions.offlineIndicationTitle);
            //        getSupportActionBar().setDisplayHomeAsUpEnabled(this.mMesiboUIOptions.enableBackButton);
        }
        if (savedInstanceState == null) {
            UserListFragment userListFragment = new UserListFragment();
            userListFragment.setListener(this);
            Bundle bl = new Bundle();
            bl.putInt(MesiboUserListFragment.MESSAGE_LIST_MODE, this.mMode);
            bl.putLong("mid", this.mForwardId);
            if (!TextUtils.isEmpty(forwardMessage)) {
                bl.putString("message", forwardMessage);
            }
            bl.putLongArray(MesiboUI.MESSAGE_IDS, this.mForwardIds);
            if (this.mMode == MesiboUserListFragment.MODE_EDITGROUP) {
                bl.putBundle(MesiboUI.BUNDLE, this.mEditGroupBundle);
            }
            bl.putBoolean(MesiboUI.FORWARD_AND_CLOSE, forwardAndClose);
            userListFragment.setArguments(bl);
        }
        //Utils.setLanguage(MainActivity.this);
        setContentView(R.layout.activity_messages_new);
        initViews();
        initFragment();
        navView.setOnNavigationItemSelectedListener(selectedListener);
        hamburgerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNavigationMenu();
            }
        });
        edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // QampUiHelper.launchEditProfile(MainActivity.this, 0, 0, false);
            }
        });
        btn_close_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppConfig.reset();
//                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent1);
//                finish();
            }
        });

        MesiboProfile profile = getProfile();
        if (!TextUtils.isEmpty(profile.getName())) name_text.setText(profile.getName());

        shareViaWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = "https://play.google.com/store/apps/details?id=com.qamp.app";

                Uri uri = Uri.parse(link);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, link.toString());
                intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.shareAppText));

                startActivity(Intent.createChooser(intent, "Share Link"));
            }
        });
        viewProfile_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // QampUiHelper.launchEditProfile(MainActivity.this, 0, 0, false);
                //finish();
            }
        });


    }

    private void initFragment() {
        UserListFragment fragment = new UserListFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment, "null");
        fragmentTransaction.addToBackStack("userListFragment");
        fragmentTransaction.commit();
        setUserPicture();
    }

    @SuppressLint("RestrictedApi")
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  AppUtils.setActivityStyle(this, toolbar);
        //setSupportActionBar(toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.container);
        nvDrawer = (NavigationView) findViewById(R.id.nav_view_hamburger);
//        MaterialShapeDrawable navViewBackground = (MaterialShapeDrawable) nvDrawer.getBackground();
//        navViewBackground.setShapeAppearanceModel(
//                navViewBackground.getShapeAppearanceModel()
//                        .toBuilder()
//                        .setTopRightCorner(CornerFamily.ROUNDED, 32)
//                        .setBottomRightCorner(CornerFamily.ROUNDED, 32)
//                        .build());
        //initNavigationMenu();
        hamburgerButton = findViewById(R.id.hambergur_button_menu);
        viewProfile_text = findViewById(R.id.viewProfile_text);
        circleProfileView = findViewById(R.id.image);
        navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);
        edit_Profile = (LinearLayout) findViewById(R.id.edit_Profile);
        logout = (LinearLayout) findViewById(R.id.logout);
        btn_close_filter = (ImageButton) findViewById(R.id.btn_close_filter);
        name_text = findViewById(R.id.name_text);
        shareViaWhatsapp = findViewById(R.id.shareViaWhatsapp);
        name_tite_layout = findViewById(R.id.name_tite_layout);
        search_view = findViewById(R.id.search_view);
        search_image = findViewById(R.id.search_image);

//        if (AppConfig.getConfig().profileId != "") {
//            setUserPicture();
//        }

        circleProfileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getProfile().getImagePath() != null) {
                    // QampUiHelper.launchImageViewer(MainActivity.this, getProfile().getImagePath());
                }
            }
        });

    }

    public MesiboProfile getProfile() {
        if (mGroupId > 0) return Mesibo.getProfile(mGroupId);
        return Mesibo.getSelfProfile();
    }

    void setUserPicture() {
        MesiboProfile profile = getProfile();
        Bitmap image = profile.getImageOrThumbnail();
        if (null != image) {
            circleProfileView.setImageDrawable(new com.mesibo.messaging.RoundImageDrawable(image));
        } else {
            circleProfileView.setImageDrawable(getDrawable(R.drawable.default_user_image));

        }
        if (true) return;
        String url = profile.getImageUrl();
        String filePath = getProfile().getImagePath();
        Bitmap b;
        if (Mesibo.fileExists(filePath)) {
            b = BitmapFactory.decodeFile(filePath);
            if (null != b) {
                circleProfileView.setImageDrawable(new RoundImageDrawable(b));
            }
        } else {
            circleProfileView.setImageDrawable(getDrawable(R.drawable.default_user_image));
        }
    }

    private void initNavigationMenu() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        // open drawer at start
        mDrawer.openDrawer(GravityCompat.START);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Utils.setLanguage(MainActivity.this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //  Utils.setLanguage(MainActivity.this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 226: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    // MesiboAPI.startMesibo(true);
                }

                return;
            }
            case 102: {
                int id = 2131427745;
                for (Fragment fragment : getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().getFragments()) {
                    if (fragment.getId() == id && fragment.isVisible())
                        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        int id = 2131427745;
        for (Fragment fragment : getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().getFragments()) {
            if (fragment.getId() == id && fragment.isVisible())
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void MesiboProfile_onUpdate(MesiboProfile mesiboProfile) {

    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    public void Mesibo_onUpdateTitle(String title) {
        this.contactsTitle.setText(title);
    }

    public void Mesibo_onUpdateSubTitle(String title) {
        if (title == null) {
            this.contactsSubTitle.setVisibility(8);
            return;
        }
        this.contactsSubTitle.setVisibility(0);
        this.contactsSubTitle.setText(title);
    }

    public boolean Mesibo_onClickUser(String address, long groupid, long forwardid) {
        return false;
    }

    public boolean Mesibo_onUserListFilter(MesiboMessage params) {
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                onBackPressed();
                return true;
            default:
                return MesiboUserListActivityNew.super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (this.mKeepRunning) {
            moveTaskToBack(true);
        } else {
            if (search_view.getVisibility() == View.VISIBLE) {
                search_view.setVisibility(View.GONE);
                name_tite_layout.setVisibility(View.VISIBLE);
            } else if (mDrawer.isDrawerOpen(GravityCompat.START)) {
                mDrawer.closeDrawer(GravityCompat.START);
            } else {
                if (pressedTime + 1000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), R.string.exitWarning, Toast.LENGTH_SHORT).show();
                }
                pressedTime = System.currentTimeMillis();
            }
        }

    }

    /* JADX WARNING: type inference failed for: r2v0, types: [android.content.Context, com.qamp.app.messaging.MesiboUserListActivity, androidx.appcompat.app.AppCompatActivity] */
    public void onResume() {
        MesiboUserListActivityNew.super.onResume();
        Mesibo.setForegroundContext(this, 0, true);
        if (ActivityCompat.shouldShowRequestPermissionRationale(MesiboUserListActivityNew.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("PERMISSION_NEEDED")
                    .setMessage("EXTERNAL_STORAGE_PERMISSION")
                    .setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MesiboUserListActivityNew.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 226);
                        }
                    })
                    .setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            if (PermissionChecker.checkSelfPermission(MesiboUserListActivityNew.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MesiboUserListActivityNew.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 226);
            }
        }

    }

    /* access modifiers changed from: protected */
    public void onPause() {
        MesiboUserListActivityNew.super.onPause();
    }
}
