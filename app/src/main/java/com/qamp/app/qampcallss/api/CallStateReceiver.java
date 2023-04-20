/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:32 PM
 *
 */

package com.qamp.app.qampcallss.api;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class CallStateReceiver extends PhoneStateListener {
    private static final String TAG = "CallStateReceiver";
    private static CallStateReceiver _instance = null;
    /* access modifiers changed from: private */
    public static Context context = null;
    private static boolean mIncoming = false;
    private static int mLastState = -1;
    private static Set<CallStateListener> mListeners = Collections.newSetFromMap(new WeakHashMap());
    private static boolean mPickedup = false;

    /* renamed from: tm */
    private static TelephonyManager f1tm = null;
    private boolean isPhoneCalling = false;

    public interface CallStateListener {
        void onCallStateChanged(boolean z, boolean z2, String str);
    }

    private CallStateReceiver(Context context2) {
        _instance = this;
        context = context2;
        f1tm = (TelephonyManager) context2.getSystemService("phone");
    }

    public static void addListener(CallStateListener callStateListener) {
        init();
        if (mListeners.size() == 0) {
            try {
                f1tm.listen(_instance, 32);
            } catch (Exception e) {
            }
        }
        mListeners.add(callStateListener);
    }

    public static synchronized CallStateReceiver init() {
        CallStateReceiver callStateReceiver;
        synchronized (CallStateReceiver.class) {
            callStateReceiver = _instance != null ? _instance : new CallStateReceiver(CallManager.getAppContext());
        }
        return callStateReceiver;
    }

    public static void removeListener(CallStateListener callStateListener) {
        init();
        mListeners.remove(callStateListener);
        if (mListeners.size() == 0) {
            f1tm.listen(_instance, 0);
        }
    }

    public void extractFromLogs() {
        if (this.isPhoneCalling) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        Cursor query = CallStateReceiver.context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{"number"}, (String) null, (String[]) null, "date desc");
                        query.moveToFirst();
                        query.getString(0);
                    } catch (Exception e) {
                    }
                }
            }, 500);
            this.isPhoneCalling = false;
        }
    }

    public void onCallStateChanged(int i, String str) {
        boolean z = true;
        if (mListeners.size() != 0) {
            boolean z2 = i != 0;
            if (z2 && mLastState == 0) {
                mIncoming = 1 == i;
            }
            if (2 != i) {
                z = false;
            }
            mPickedup = z;
            mLastState = i;
            for (CallStateListener onCallStateChanged : mListeners) {
                onCallStateChanged.onCallStateChanged(z2, mIncoming, str);
            }
            super.onCallStateChanged(i, str);
        }
    }
}
