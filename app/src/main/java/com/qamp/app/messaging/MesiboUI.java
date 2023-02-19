package com.qamp.app.messaging;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import com.mesibo.api.MesiboProfile;
import com.qamp.app.messaging.AllUtils.MyTrace;

import com.qamp.app.R;

public class MesiboUI {
    public static final String BUNDLE = "bundle";
    public static final String FORWARD_AND_CLOSE = "forwardandclose";
    public static final String GROUP_ID = "groupid";
    public static final String GROUP_MODE = "groupmode";
    public static final String GROUP_NAME = "groupname";
    public static final String KEEP_RUNNING = "keep_running";
    public static final String MEMBERS = "members";
    public static final String MESSAGE_CONTENT = "message";
    public static final String MESSAGE_ID = "mid";
    public static final String MESSAGE_IDS = "mids";
    public static final String PEER = "peer";
    public static final String PICTURE_PATH = "picturepath";
    public static final String START_IN_BACKGROUND = "start_in_background";
    public static Config mConfig = new Config();
    public static Listener mListener = null;

    public static class Config {
        public static final int e2eeIcon = R.drawable.ic_lock_black_18dp;
        public String allUsersTitle = "All Users";

        /* renamed from: at */
        public String f0at = "at";
        public String cancelTitle = "Cancel";
        public String connectingIndicationTitle = "Connecting...";
        public Bitmap contactPlaceHolder = null;
        public String createGroupTitle = "Create a New Group";
        public String deleteForEveryoneTitle = "Delete For Everyone";
        public String deleteForMeTitle = "Delete For Me";
        public String deleteMessagesTitle = "Delete Messages?";
        public String deleteTitle = "Delete";
        public String deletedMessageTitle = "This message was deleted";
        public String e2eeActive = "Messages and Calls are End-To-End Encrypted. No one including AppName can read or listen to your communication";
        public int e2eeIconColor = -3407872;
        public String e2eeIdentityChanged = "End-To-End Encryption is Active, but the Identity has changed. It is recommended to match the fingerprint";
        public String e2eeInactive = "End-To-End Encryption Not Active. TLS encryption is active.";
        public boolean e2eeIndicator = true;
        public String e2eeTitle = "End-To-End Encryption";
        public String emptyMessageListMessage = "You do not have any messages. Click on the message button above to start a conversation";
        public String emptySearchListMessage = "Your search returned no results";
        public String emptyUserListMessage = "No Messages";
        public boolean enableBackButton = false;
        public boolean enableForward = true;
        public boolean enableSearch = true;
        public boolean enableVideoCall = false;
        public boolean enableVoiceCall = false;
        public String forwardTitle = "Forward To";
        public String forwardedTitle = "Forwarded";
        public String groupMembersTitle = "Group Members";
        public int headerIcon = R.drawable.ic_info_black;
        public int headerIconColor = -16742773;
        public String headerTitle = "";
        public String joinedIndicationTitle = "joined";
        public boolean mConvertSmilyToEmoji = true;
        public boolean mEnableNotificationBadge = false;
        protected String mGoogleApiKey = null;
        public int mHorizontalImageWidth = 75;
        public int[] mLetterTitleColors = null;
        public long mMaxImageFileSize = 307200;
        public long mMaxVideoFileSize = 20971520;
        public int mMessagingFragmentLayout = 0;
        public int mStatusbarColor = 0;
        public int mToolbarColor = -16742773;
        public int mToolbarTextColor = 0;
        public int mUserListFragmentLayout = 0;
        public int mUserListStatusColor = -7960954;
        public int mUserListTypingIndicationColor = -11953852;
        public int mVerticalImageWidth = 65;
        public int messageBackgroundColorForMe = -2755092;
        public int messageBackgroundColorForPeer = -328966;
        public String messageListTitle = "";
        public Bitmap messagingBackground = null;
        public int messagingBackgroundColor = -1447447;
        public String missedVideoCallTitle = "Missed video call";
        public String missedVoiceCallTitle = "Missed voice call";
        public String modifyGroupTitle = "Modify Group details";
        public String noNetworkIndicationTitle = "No Network";
        public String offlineIndicationTitle = "Not connected";
        public String onlineIndicationTitle = null;
        public int progressbarColor = -16742773;
        public String recentUsersTitle = "Recent Users";
        public String selectContactTitle = "Select a contact";
        public String selectGroupContactsTitle = "Select group members";
        public String sendAnotherLocation = "To send other location, long press on the map and then click on the address";
        public String sendCurrentLocation = "Send Current Location";
        public boolean showRecentInForward = true;
        public String suspendIndicationTitle = "Service Suspended";
        public int titleBackgroundColorForMe = -4198174;
        public int titleBackgroundColorForPeer = -1118482;
        public String today = "Today";
        public String typingIndicationTitle = "typing...";
        public String unknownTitle = "Unknown";
        public boolean useLetterTitleImage = true;
        public String userListTitle = "Contacts";
        public String userOnlineIndicationTitle = "online";
        public String videoFromGalleryTitle = "Gallery";
        public String videoFromRecorderTitle = "Video Recorder";
        public String videoSelectTitle = "Send your video from?";
        public String yesterday = "Yesterday";
    }

    public interface Listener {
        int MesiboUI_onGetMenuResourceId(Context context, int i, MesiboProfile mesiboProfile, Menu menu);

        boolean MesiboUI_onMenuItemSelected(Context context, int i, MesiboProfile mesiboProfile, int i2);

        boolean MesiboUI_onShowLocation(Context context, MesiboProfile mesiboProfile);

        void MesiboUI_onShowProfile(Context context, MesiboProfile mesiboProfile);
    }

    public static void setListener(Listener listner) {
        mListener = listner;
    }

    public static Listener getListener() {
        return mListener;
    }

    public static Config getConfig() {
        return mConfig;
    }

    public static void launch(Context context, int flag, boolean startInBackground, boolean keepRunnig) {
        MesiboUIManager.launchContactActivity(context, 0, MesiboUserListFragment.MODE_MESSAGELIST, flag, startInBackground, keepRunnig, (Bundle) null);
    }

    public static void launchContacts(Context context, long forwardid, int selectionMode, int flag, Bundle bundle) {
        MesiboUIManager.launchContactActivity(context, forwardid, selectionMode, flag, false, false, bundle);
    }

    public static void launchContacts(Context context, long forwardid, int selectionMode, int flag, Bundle bundle, String forwardMessage) {
        MesiboUIManager.launchContactActivity(context, forwardid, selectionMode, flag, false, false, bundle, forwardMessage);
    }

    public static void launchMessageView(Context context, MesiboProfile profile) {
        MesiboUIManager.launchMessagingActivity(context, 0, profile.getAddress(), profile.getGroupId());
    }

    public static void launchForwardActivity(Context context, String forwardMessage, boolean forwardAndClose) {
        MesiboUIManager.launchForwardActivity(context, forwardMessage, forwardAndClose);
    }

    public static void showEndToEndEncryptionInfo(Context context, String address, long groupid) {
        MesiboUIManager.showEndToEndEncryptionInfo(context, address, groupid);
    }

    public static void showEndToEndEncryptionInfoForSelf(Context context) {
        MesiboUIManager.showEndToEndEncryptionInfo(context, (String) null, 0);
    }

    public static String version() {
        return BuildConfig.BUILD_VERSION;
    }

    public static void setTestMode(boolean testMode) {
        MesiboUIManager.setTestMode(testMode);
    }

    public static void setEnableProfiling(boolean enabled) {
        MyTrace.setEnable(enabled);
    }
}
