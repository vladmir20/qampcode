package com.qamp.app.messaging;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mesibo.api.Mesibo;

import org.mesibo.messenger.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Utils {
    public static void setActivityStyle(AppCompatActivity context, Toolbar toolbar) {
        if (!(toolbar == null || MesiboUI.getConfig().mToolbarColor == 0)) {
            toolbar.setBackgroundColor(MesiboUI.getConfig().mToolbarColor);
        }
        if (Build.VERSION.SDK_INT >= 21 && MesiboUI.getConfig().mStatusbarColor != 0) {
            Window window = context.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(MesiboUI.getConfig().mStatusbarColor);
        }
    }

    public static void createRoundDrawable(Context context, View view, int color, float radiusInDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        float radiusInPx = radiusInDp * 8.0f;
        if (context != null) {
            radiusInPx = TypedValue.applyDimension(1, radiusInDp, context.getResources().getDisplayMetrics());
        }
        drawable.setCornerRadius(radiusInPx);
        view.setBackground(drawable);
    }

    public static void setTitleAndColor(ActionBar actionBar, String title) {
        SpannableString s = new SpannableString(title);
        if (MesiboUI.getConfig().mToolbarTextColor != 0) {
            s.setSpan(new ForegroundColorSpan(MesiboUI.getConfig().mToolbarTextColor), 0, title.length(), 33);
        }
        if (title != null) {
            actionBar.setTitle(s);
        }
    }

    public static void setTextViewColor(TextView textView, int color) {
        if (color != 0) {
            textView.setTextColor(color);
        }
    }

    public static void showAlert(Context context, String title, String message, DialogInterface.OnClickListener onclick) {
        if (context != null) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setCancelable(true);
            dialog.setPositiveButton(17039379, onclick);
            dialog.setNegativeButton(17039360, onclick);
            try {
                dialog.show();
            } catch (Exception e) {
            }
        }
    }

    public static void showAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public static void showServicesSuspendedAlert(Context context) {
        if (context != null && Mesibo.isAccountSuspended()) {
            showAlert(context, "Mesibo Service Suspended", "Mesibo Services for this App are suspended. Upgrade to continue");
        }
    }

    public static String getFileSizeString(long fileSize) {
        long fileSize2;
        if (fileSize <= 0) {
            return "";
        }
        String unit = "KB";
        if (fileSize > 1048576) {
            unit = "MB";
            fileSize2 = fileSize / 1048576;
        } else {
            fileSize2 = fileSize / 1024;
        }
        if (fileSize2 < 1) {
            fileSize2 = 1;
        }
        return String.valueOf(fileSize2) + unit;
    }

    public static boolean saveBitmpToFilePath(Bitmap bmp, String filePath) {
        try {
            FileOutputStream fOut = new FileOutputStream(new File(filePath));
            if (bmp != null) {
                bmp.compress(Bitmap.CompressFormat.PNG, 70, fOut);
                try {
                    fOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fOut.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            FileOutputStream fileOutputStream = fOut;
            return true;
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
            return false;
        }
    }

    public static boolean aquireUserPermission(Context context, String permission, int REQUEST_CODE) {
        if (ContextCompat.checkSelfPermission(context, permission) == 0) {
            return true;
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale((AppCompatActivity) context, permission)) {
            ActivityCompat.requestPermissions((AppCompatActivity) context, new String[]{permission}, REQUEST_CODE);
        }
        return false;
    }

    public static boolean aquireUserPermissions(Context context, List<String> permissions, int REQUEST_CODE) {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != 0) {
                permissionsNeeded.add(permission);
            }
        }
        if (permissionsNeeded.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions((AppCompatActivity) context, (String[]) permissionsNeeded.toArray(new String[permissionsNeeded.size()]), REQUEST_CODE);
        return false;
    }

    public static boolean checkPermissionGranted(Context context, String permission) {
        return Build.VERSION.SDK_INT < 23 || context.checkSelfPermission(permission) == 0;
    }

    public static boolean checkPlayServices(Activity activity, int requestCode) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode == 0) {
            return true;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            GooglePlayServicesUtil.getErrorDialog(resultCode, activity, requestCode).show();
        } else {
            Toast.makeText(activity.getApplicationContext(), MesiboConfiguration.GOOGLE_PLAYSERVICE_STRING, 1).show();
            activity.finish();
        }
        return false;
    }
}
