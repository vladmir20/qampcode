/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

package com.qamp.app.Services;

import com.google.firebase.messaging.FirebaseMessagingService;

public class MesiboInstanceIDListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
       ///  Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        MesiboRegistrationIntentService.startRegistration(this, null, null);
    }


}
