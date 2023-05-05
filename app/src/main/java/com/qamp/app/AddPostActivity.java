/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 05/05/23, 1:33 PM
 *
 */

package com.qamp.app;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.qamp.app.Utils.AppUtils;

public class AddPostActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilss.setLanguage(AddPostActivity.this);
        setContentView(R.layout.activity_add_post);
        AppUtils.setStatusBarColor(AddPostActivity.this, R.color.grey_5);

    }

}
