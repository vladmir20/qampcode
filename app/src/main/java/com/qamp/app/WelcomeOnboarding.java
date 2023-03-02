package com.qamp.app;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeOnboarding extends AppCompatActivity {

    /** Duration of wait **/
    private final int WELCOME_DISPLAY_LENGTH = 4000;
    private final int WELCOME_LOGO_DISPLAY_LENGTH = 1000;
    private ImageView big_logo = null, iv_logo = null;
    private LinearLayout pictureViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_onboarding);
       // Utils.setStatusBarTheme(WelcomeOnboarding.this);

        pictureViewContainer = findViewById(R.id.pictureViewContainer);
        pictureViewContainer.setVisibility(View.GONE);
        big_logo = findViewById(R.id.big_logo);
        big_logo.setVisibility(View.VISIBLE);
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.left_to_right_transition);
        big_logo.setAnimation(animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                big_logo.setVisibility(View.GONE);
                pictureViewContainer.setVisibility(View.VISIBLE);
            }
        }, WELCOME_LOGO_DISPLAY_LENGTH);


        navigateToNextActivity();
    }

    private void navigateToNextActivity(){
        boolean isLoggedIn = !AppConfig.getConfig().token.isEmpty();

        if (isLoggedIn) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    /* Create an Intent that will start the Main-Activity. */
                    if(!TextUtils.isEmpty(AppConfig.getConfig().token)) {
                        QampUiHelper.launchMainActivity(WelcomeOnboarding.this);
                        WelcomeOnboarding.this.finish();
                        finishAffinity();
                    }
                }
            }, WELCOME_DISPLAY_LENGTH);
        }
    }
}