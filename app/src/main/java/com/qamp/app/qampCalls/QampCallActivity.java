package com.qamp.app.qampCalls;

//import com.qamp.app.util.Utils;


//import com.qamp.app.Util.Utils;
import com.qamp.app.Utilss;

public class QampCallActivity extends MesiboCallActivityInternal {
    public QampCallActivity() {
    }

    public int checkPermissions(boolean var1) {
        String[] var2 = new String[]{"android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE"};
        if (var1) {
            var2 = new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE"};
        }

        return Utilss.checkPermissions(0, this, var2, false);
    }

    public void setPublisher(MesiboCall.MesiboParticipant var1) {
    }
}

