// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.qamp.app.uihelper.MesiboUiHelperConfig;

public class Alert {
    public static final String TAG = "Alert";
    public static final int DIALOG_POSITIVE = 1;
    public static final int DIALOG_NEGATIVE = 2;
    public static final int DIALOG_CANCELED = 3;
    public static final int PROMPT_NONETWORK = 1;
    public static final int PROMPT_NOCREDITS = 2;
    public static final int PROMPT_SLOWCONNECTION = 3;
    public static final int PROMPT_NOTCONNECTED = 4;
    public static final int PROMPT_LOGOUT = 5;
    public static final int PROMPT_BUYCREDITS = 6;
    public static final int PROMPT_EARNFREECREDITS = 7;
    public static final int PROMPT_REDEEMFREECREDITS = 8;
    public static final int PROMPT_CHANGEPHONENUMBER = 9;
    public static final int PROMPT_CALLCHARGE = 10;
    static int mDialogid;
    static DialogListener mListener;
    static Alert _instance;
    private static DialogInterface.OnClickListener mOnClickListener;
    private static DialogInterface.OnCancelListener mOnCancelListner;

    static {
        Alert.mDialogid = -1;
        Alert.mListener = null;
        Alert.mOnClickListener = (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, final int i) {
                if (null == Alert.mListener) {
                    return;
                }
                dialogInterface.dismiss();
                if (i == -1) {
                    Alert.mListener.onDialog(Alert.mDialogid, 1);
                } else {
                    Alert.mListener.onDialog(Alert.mDialogid, 2);
                }
                Alert.mDialogid = -1;
                Alert.mListener = null;
            }
        };
        Alert.mOnCancelListner = (DialogInterface.OnCancelListener) new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialogInterface) {
                if (null == Alert.mListener) {
                    return;
                }
                Alert.mListener.onDialog(Alert.mDialogid, 3);
                Alert.mDialogid = -1;
                Alert.mListener = null;
            }
        };
    }

    public static Dialog showAlertMessage(final Context activityContext, final String title, final String message) {
        return showAlertMessage(activityContext, title, message, true);
    }

    public static void showAlertDialog(final Context context, final String title, final String message) {
        showAlertDialog(context, title, message, null, null);
    }

    public static void showAlertDialog(final Context context, final String title, final String message, final String positivetitle) {
        showAlertDialog(context, title, message, positivetitle, null);
    }

    public static void showAlertDialog(final Context context, final String title, final String message, final String positivetitle, final String negativetitile) {
        showAlertDialog(context, title, message, positivetitle, negativetitile, true);
    }

    public static void showAlertDialog(final Context context, final String title, final String message, final String positivetitle, final String negativetitile, final boolean cancelable) {
        showAlertDialog(context, title, message, positivetitle, negativetitile, 0, null, cancelable);
    }

    public static void showAlertDialog(final Context context, final String title, final String message, final String positivetitle, final String negativetitile, final int id, final DialogListener listener, final boolean cancelable) {
        if (null == context) {
            TMLog.d("Alert", "Null context in showAlertDialog");
            return;
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle((CharSequence) title);
        dialog.setMessage((CharSequence) message);
        dialog.setCancelable(cancelable);
        if (TextUtils.isEmpty((CharSequence) positivetitle)) {
            dialog.setPositiveButton(17039370, (listener != null) ? Alert.mOnClickListener : null);
        } else {
            dialog.setPositiveButton((CharSequence) positivetitle, (listener != null) ? Alert.mOnClickListener : null);
        }
        if (TextUtils.isEmpty((CharSequence) negativetitile)) {
            dialog.setNegativeButton(17039360, (listener != null) ? Alert.mOnClickListener : null);
        } else {
            dialog.setNegativeButton((CharSequence) negativetitile, (listener != null) ? Alert.mOnClickListener : null);
        }
        if (null != listener) {
            Alert.mDialogid = id;
            Alert.mListener = listener;
            dialog.setOnCancelListener(Alert.mOnCancelListner);
        }
        try {
            dialog.show();
        } catch (Exception e) {
            TMLog.d("Alert", "Exception showing alert: " + e);
        }
    }

    public static void prompt(final Context context, final int id, final DialogListener listener, final boolean cancelable) {
        String title = null;
        String message = null;
        String positivetitle = null;
        String negativetitle = null;
        if (3 == id) {
            title = "Slow Connection";
            message = "You are presently connected over a slow data connection (2G) and hence the call quality MAY NOT be good. You might want to use callback instead.";
            positivetitle = "call";
            negativetitle = "Use callback";
        } else if (4 == id) {
            title = "No Internet Connection";
            message = "Your phone is not connected to the internet. Please check your internet connection and try again later.";
            positivetitle = "ok";
            negativetitle = null;
        } else if (1 == id) {
            title = "Netwrok error";
            message = "We could not place the call. Check internet connection on your phone and try again later. You may also use callback if you have a slow conection.";
            positivetitle = "ok";
            negativetitle = "Use callback";
        } else if (2 != id) {
            if (5 == id) {
                title = "Logout?";
                message = "You will not be able to make calls till you login again. Continue?";
                positivetitle = "Logout";
            } else if (6 != id) {
                if (7 == id) {
                    title = "Earn Bonus Credits";
                    message = "Earn bonus credits by inviting your friends and family to use TringMe. We add bonus credits to your account when they make a purchase";
                    positivetitle = "Invite & Earn";
                } else if (8 == id) {
                    title = "Redeem Bonus Credits";
                    message = "Awesome, bonus credits will be redeemed on your next purchase. Redeem now?";
                    positivetitle = "Redeem";
                } else {
                    if (9 != id) {
                        return;
                    }
                    title = "Change Phone Number?";
                    message = "We will send you an SMS with the verification code to change the phone number. Continue?";
                }
            }
        }
        showAlertDialog(context, title, message, positivetitle, negativetitle, id, listener, cancelable);
    }

    public static Dialog showAlertMessage(final Context context, final String title, final String message, final boolean cancelable) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(context);
        if (null == adb) {
            throw new NullPointerException("Application Context not available");
        }
        adb.setCancelable(cancelable);
        if (!cancelable) {
            final DialogInterface.OnClickListener dcl = (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.cancel();
                }
            };
            adb.setPositiveButton((CharSequence) "OK", dcl);
        }
        if (MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setTitle((CharSequence) title);
        adb.setMessage((CharSequence) message);
        return (Dialog) adb.create();
    }

    public static Dialog showSelectionDialog(final Context activityContext, final String title, final String message, final String nText, final DialogInterface.OnClickListener nClickHandler, final String pText, final DialogInterface.OnClickListener pClickHandler) {
        return showSelectionDialog(activityContext, title, message, true, nText, nClickHandler, pText, pClickHandler);
    }

    public static AlertDialog showInfoDialog(final String text, final Context c) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(c);
        if (MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setMessage((CharSequence) text);
        final AlertDialog ad = adb.create();
        ad.show();
        new Handler().postDelayed((Runnable) new Runnable() {
            @Override
            public void run() {
                if (ad.isShowing()) {
                    ad.cancel();
                }
            }
        }, 5000L);
        return ad;
    }

    public static Dialog showSelectionDialog(final Context activityContext, final String title, final String message, final boolean cancelable, String nText, final DialogInterface.OnClickListener nClickHandler, String pText, final DialogInterface.OnClickListener pClickHandler) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(activityContext);
        if (null == adb) {
            throw new NullPointerException("Application Context not available");
        }
        adb.setCancelable(cancelable);
        if (MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setTitle((CharSequence) title);
        adb.setMessage((CharSequence) message);
        if (null != nClickHandler) {
            nText = ((null == nText) ? "Cancel" : nText);
            adb.setNegativeButton((CharSequence) nText, nClickHandler);
        }
        if (null != pClickHandler) {
            pText = ((null == pText) ? "OK" : pText);
            adb.setPositiveButton((CharSequence) pText, pClickHandler);
        }
        return (Dialog) adb.create();
    }

    public static Dialog createCustomViewDialog(final Context activityContext, final String title, final View contentView, final boolean cancelable, final String nText, final DialogInterface.OnClickListener nClickHandler, final String pText, final DialogInterface.OnClickListener pClickHandler) {
        return createCustomViewDialog(activityContext, title, contentView, cancelable, nText, nClickHandler, pText, pClickHandler, null, null);
    }

    public static Dialog createCustomViewDialog(final Context activityContext, final String title, final View contentView, final boolean cancelable, String nText, final DialogInterface.OnClickListener nClickHandler, String pText, final DialogInterface.OnClickListener pClickHandler, String mText, final DialogInterface.OnClickListener mClickHandler) {
        final AlertDialog.Builder adb = new AlertDialog.Builder(activityContext);
        if (null == adb) {
            throw new NullPointerException("Application Context not available");
        }
        adb.setCancelable(cancelable);
        if (MesiboUiHelperConfig.mAppIconResourceId > 0) {
            adb.setIcon(MesiboUiHelperConfig.mAppIconResourceId);
        }
        adb.setTitle((CharSequence) title);
        adb.setView(contentView);
        if (null != nClickHandler) {
            nText = ((null == nText) ? "Cancel" : nText);
            adb.setNegativeButton((CharSequence) nText, nClickHandler);
        }
        if (null != pClickHandler) {
            pText = ((null == pText) ? "OK" : pText);
            adb.setPositiveButton((CharSequence) pText, pClickHandler);
        }
        if (null != mClickHandler) {
            mText = ((null == mText) ? "None" : mText);
            adb.setNeutralButton((CharSequence) mText, mClickHandler);
        }
        return (Dialog) adb.create();
    }

    public static ProgressDialog getProgressDialog(final Context c, final String message) {
        final ProgressDialog progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage((CharSequence) message);
        return progressDialog;
    }

    public static void showCountryDialog(final Context context) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle((CharSequence) "Select Country");
        dialog.setNegativeButton((CharSequence) "cancel", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void showNetworkError(final Context c) {
        showAlertDialog(c, "No Internet Connection", "Your phone is not connected to the internet. Please check your internet connection and try again later.");
    }

    public static void showConnectionError(final Context c) {
        showAlertDialog(c, "Connection Failed", "Sorry, we could not connect. Please check your internet connection and try again later");
    }

    public static void showCallbackInProgress(final Context c) {
        showAlertDialog(c, "Previous Callback in Progress", "Please wait for your previous callback request to finish.");
    }

    public static void showUnknownError(final Context c) {
        showAlertDialog(c, "Error", "Sorry, something went wrong. Please try again later");
    }

    public static void showPassworReset(final Context c) {
        showAlertDialog(c, "Reset password", "Instructions for resetting your password have been emailed to you. Please check your spam folder if you don't see it in your Inbox");
    }

    public static void showInvalidPhoneNumber(final Context c) {
        showAlertDialog(c, "Invalid Phone Number", "The phone number you entered is not a valid number. Please check the number and try again. ");
    }

    public static void showChoicesDialog(final Activity context, final String title, final String[] items) {
        final AlertDialog.Builder builder = new AlertDialog.Builder((Context) context);
        if (!TextUtils.isEmpty((CharSequence) title)) {
            builder.setTitle((CharSequence) title);
        }
        builder.setItems((CharSequence[]) items, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int which) {
            }
        });
        builder.show();
    }

    public interface DialogListener {
        void onDialog(final int p0, final int p1);
    }
}
