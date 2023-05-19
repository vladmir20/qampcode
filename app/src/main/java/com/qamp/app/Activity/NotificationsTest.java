/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.R;

public class NotificationsTest extends AppCompatActivity {
    public static String tokenFinal = "";
    public static String getFirebaseDeviceToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                        }
                        String token = null;
                        if (task.getResult()!=null)
                             token = task.getResult();
                        tokenFinal = token;
                        AppConfig.getConfig().deviceToken = task.getResult();
                        System.out.println("My Device Token New rea- " + token);
                    }
                });
        return tokenFinal;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_notifications);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                        }
                        String token = task.getResult();
                        System.out.println("My Device Token - " + token);

                    }
                });
    }
}
