/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.qamp.app.R;

import java.util.ArrayList;
import java.util.List;

public class Utils
{
    public static void showAlert(final Context context, final String title, final String message, final DialogInterface.OnClickListener pl, final DialogInterface.OnClickListener nl) {
        if (null == context) {
            return;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle((CharSequence)title);
        dialog.setMessage((CharSequence)message);
        dialog.setCancelable(true);
        dialog.setPositiveButton(17039370, pl);
        dialog.setNegativeButton(17039360, nl);
        try {
            dialog.show();
        }
        catch (Exception ex) {}
    }


    public  static  void setButtonState(Button buttonState, boolean state){
        if (!state){
            buttonState.setEnabled(state);
            buttonState.setBackgroundColor(buttonState.getContext().getResources().getColor(R.color.buttonDisable));
            buttonState.setTextColor(buttonState.getContext().getResources().getColor(R.color.buttonDisableText));
        }else{
            buttonState.setEnabled(state);
            buttonState.setBackgroundColor(buttonState.getContext().getResources().getColor(R.color.buttonActive));
            buttonState.setTextColor(buttonState.getContext().getResources().getColor(R.color.buttonActiveText));
        }
    }

    public static boolean aquireUserPermissions(final Context context, final List<String> permissions, final int REQUEST_CODE) {
        final List<String> permissionsNeeded = new ArrayList<String>();
        for (final String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != 0) {
                permissionsNeeded.add(permission);
            }
        }
        if (permissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions((Activity)context, (String[])permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE);
        return false;
    }
}
