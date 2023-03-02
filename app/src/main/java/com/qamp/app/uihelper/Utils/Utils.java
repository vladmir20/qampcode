// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
