package com.qamp.app.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.qamp.app.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class AppUtils {
    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(color));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static void changeMenuIconColor(Menu menu, @ColorInt int color) {
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable == null) continue;
            drawable.mutate();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }
    }


    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() != 10) {
            return phoneNumber; // Invalid phone number, return as is
        }
        return "+91-" + phoneNumber.substring(0, 4) + "-" + phoneNumber.substring(4);
    }

    public static Boolean validatePhoneNumber(String phoneEditText, String countryCode) {
        String swissNumberStr = phoneEditText;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, countryCode);
            boolean isValid = phoneUtil.isValidNumber(swissNumberProto); // returns true
            if (isValid && countryCode.equals("91")) {
                return true;
            }
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e);
        }

        return false;
    }

    public static void showCustomToast(Context context, String message) {
        // Inflate the custom toast layout
        View toastView = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);

        // Customize the ImageView and TextView as needed
        ImageView imageView = toastView.findViewById(R.id.imageView);
        TextView textView = toastView.findViewById(R.id.textView);
        // You can set a specific logo or message here if needed
        // imageView.setImageResource(R.drawable.your_custom_logo);
        textView.setText(message);

        // Create and show the custom toast
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT); // Adjust the duration as needed
        toast.setView(toastView);
        toast.show();
    }

    public static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    // Save a string value to SharedPreferences
    public static void saveStringValue(Activity context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Retrieve a string value from SharedPreferences
    public static String getStringValue(Activity context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }


}
