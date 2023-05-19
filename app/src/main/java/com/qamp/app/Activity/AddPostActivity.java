/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.qamp.app.R;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;

public class AddPostActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(AddPostActivity.this);
        setContentView(R.layout.activity_add_post);
        AppUtils.setStatusBarColor(AddPostActivity.this, R.color.grey_5);

    }

}
