package com.qamp.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.mesibo.api.Mesibo;
import com.qamp.app.Adapter.ViewPagerAdapter;
import com.qamp.app.CustomClasses.DepthPageTransformer;
import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

import java.util.Timer;
import java.util.TimerTask;

public class OnBoardingScreens extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int currentPage = 0;
    private int NUM_PAGES = 3; // Number of pages in your ViewPager
    private int[] imageResources = {R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3};
    private String[] imageText = {"Seamless Communication",
            "Ads Free Feeds & Updates",
            "Discover & Interact "};
    private String[] imageTextDescription = {"Experience seamless communication with nearby communities & your loved ones. Do more than just chat",
            "See only what you follow without any distraction. Engage directly with people & communities of your interest",
            "Discover local businesses & communities or create your presence to connect with people around you"};
    // Replace with your image resources
    private ImageView[] dots;

    private ImageView backBtn;
    private TextView skipIntroBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarColor(OnBoardingScreens.this, R.color.colorAccent);
        setContentView(R.layout.activity_on_boarding);
        //MesiboInit.initialiseMesibo(OnBoardingScreens.this,false);
        getIntent().getStringExtra("myProfile");
        Toast.makeText(this, ""+ Mesibo.getSelfProfile().getAddress(), Toast.LENGTH_SHORT).show();
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        backBtn = findViewById(R.id.backBtn);
        skipIntroBtn = findViewById(R.id.skipIntroBtn);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageResources, imageText, imageTextDescription);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(true, new DepthPageTransformer());

        // Initialize dots
        dots = new ImageView[NUM_PAGES];
        for (int i = 0; i < NUM_PAGES; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot)); // Non-active dot drawable
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayout.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.active_dot)); // Set the first dot as active

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Update dots when page changes
                for (int i = 0; i < NUM_PAGES; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_active_dot));
                }
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.active_dot));
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Auto-scroll ViewPager with a timer
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new AutoScrollTask(), 5000, 3000); // Change the interval as needed

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });
        skipIntroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OnBoardingScreens.this, OnBoardingProfile.class);
                startActivity(intent);
                AppUtils.saveStringValue(OnBoardingScreens.this,"isOnBoardingScreensSaved","true");
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
    }

    // Task to auto-scroll ViewPager
    private class AutoScrollTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentPage == NUM_PAGES - 1) {
                        currentPage = 0;
                    } else {
                        currentPage++;
                    }
                    viewPager.setCurrentItem(currentPage);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(OnBoardingScreens.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
