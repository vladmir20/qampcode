/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.fcm;

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
