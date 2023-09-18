package com.qamp.app.Activity;


import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qamp.app.MesiboImpModules.ContactSyncClass;
import com.qamp.app.R;
import com.qamp.app.Utils.AnimationUtils;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Utils.AppUtils;

import java.util.List;
import java.util.Map;

public class SplashScreenActivity extends AppCompatActivity {

    Button agree_btn;
    private TextView textView2, footer_text_primary, textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarColor(SplashScreenActivity.this, R.color.colorAccent);
        setContentView(R.layout.activity_splash_screen);
        initViews();
        ContactSyncClass.getMyQampContacts(SplashScreenActivity.this);
        boolean isLoggedIn = !AppConfig.getConfig().token.isEmpty();
        if (isLoggedIn) {
            textView2.setVisibility(View.GONE);
            footer_text_primary.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            agree_btn.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(AppConfig.getConfig().token)) {
                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                }
            }, 2000);
        } else {
            textView2.setVisibility(View.VISIBLE);
            footer_text_primary.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            agree_btn.setVisibility(View.VISIBLE);
            AnimationUtils.animateViewVisibility(agree_btn, false);
            AnimationUtils.animateViewVisibility(agree_btn, true);
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    // Define the shared element and its transition name
                    View sharedElement = findViewById(R.id.parentLayout);
                    sharedElements.put("backgroundTransition", sharedElement);
                }
            });
            agree_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startLoginActivity();
                }
            });
        }
    }


    private void startLoginActivity() {
        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(SplashScreenActivity.this).toBundle();
        intent.putExtra("backgroundResource", R.mipmap.splash);
        startActivity(intent, bundle);
    }

    private void initViews() {
        agree_btn = findViewById(R.id.agree_btn);
        textView2 = findViewById(R.id.textView2);
        footer_text_primary = findViewById(R.id.footer_text_primary);
        textView3 = findViewById(R.id.textView3);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
