

package com.qamp.app.LoginModule.MesiboApiClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.JobIntentService;

/**
 * Require WAKE_LOCK persmission for API level earlier than Android O
 */

public class MesiboJobIntentService extends JobIntentService {
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        try {
        enqueueWork(context, MesiboJobIntentService.class, JOB_ID, work);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onHandleWork(Intent intent) {
        try {
        MesiboRegistrationIntentService.sendMessageToListener( true);
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    final Handler mHandler = new Handler();
    // Helper for showing tests
    void toast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override public void run() {
                Toast.makeText(MesiboJobIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
