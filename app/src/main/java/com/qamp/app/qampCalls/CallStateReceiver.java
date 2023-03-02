package com.qamp.app.qampCalls;


import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class CallStateReceiver extends PhoneStateListener {
    private static final String TAG = "CallStateReceiver";
    private static TelephonyManager tm = null;
    private static Context context = null;
    private static Set<CallStateListener> mListeners = Collections.newSetFromMap(new WeakHashMap());
    private static CallStateReceiver _instance = null;
    private boolean isPhoneCalling = false;

    private CallStateReceiver(Context var1) {
        _instance = this;
        context = var1;
        tm = (TelephonyManager)var1.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static synchronized CallStateReceiver init() {
        return _instance != null ? _instance : new CallStateReceiver(CallManager.getAppContext());
    }

    public void onCallStateChanged(int var1, String var2) {
        if (0 != mListeners.size()) {
            Iterator var3 = mListeners.iterator();

            while(var3.hasNext()) {
                ((CallStateListener)var3.next()).onCallStateChanged(var1, var2);
            }

            super.onCallStateChanged(var1, var2);
        }
    }

    public static void addListener(CallStateListener var0) {
        init();
        if (mListeners.size() == 0) {
            tm.listen(_instance, 32);
        }

        mListeners.add(var0);
    }

    public static void removeListener(CallStateListener var0) {
        init();
        mListeners.remove(var0);
        if (mListeners.size() == 0) {
            tm.listen(_instance, 0);
        }

    }

    public void extractFromLogs() {
        if (this.isPhoneCalling) {
            (new Handler()).postDelayed(new Runnable() {
                public void run() {
                    try {
                        String[] var1 = new String[]{"number"};
                        Cursor var3;
                        (var3 = CallStateReceiver.context.getContentResolver().query(Calls.CONTENT_URI, var1, (String)null, (String[])null, "date desc")).moveToFirst();
                        var3.getString(0);
                    } catch (Exception var2) {
                    }
                }
            }, 500L);
            this.isPhoneCalling = false;
        }

    }

    public interface CallStateListener {
        void onCallStateChanged(int var1, String var2);
    }
}

