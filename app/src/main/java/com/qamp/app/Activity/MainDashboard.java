package com.qamp.app.Activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;

public class MainDashboard extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        AppUtils.setStatusBarColor(MainDashboard.this,R.color.colorAccent);
    }
}
