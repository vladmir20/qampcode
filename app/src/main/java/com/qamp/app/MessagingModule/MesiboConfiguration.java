/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:35 AM
 *
 */

package com.qamp.app.MessagingModule;

import com.qamp.app.R;

import java.util.List;

public class MesiboConfiguration {


    public static final String CREATE_GROUP_NOMEMEBER_TITLE_STRING = "No Members";
    public static final String CREATE_GROUP_NOMEMEBER_MESSAGE_STRING = "Add two or more members to create a group.";

    public static final int KEYBOARD_ICON = R.drawable.normal_keyboard;
    public static final int EMOJI_ICON = R.drawable.emoji_keyborad;

    //public static final String CREATE_GROUP_GROUPNAME_ERROR_MESSAGE_STRING = "Group name should be at least 2 characters";

    public static final String CREATE_GROUP_ERROR_TITLE_STRING = "Group Update Failed !";
    public static final String CREATE_GROUP_ERROR_MESSAGE_STRING = "Please check internet connection and try again later";

    public static final int MAX_GROUP_SUBJECT_LENGTH = 50;
    public static final int MIN_GROUP_SUBJECT_LENGTH = 2;

    //  public static final String CREATE_NEW_GROUP_MESSAGE_STRING = .getString(R.string.add_member_text);

    public static final String PROGRESS_DIALOG_MESSAGE_STRING = "Please wait. . .";

    public static final int DEFAULT_GROUP_MODE = 0;

    //public static final String ALL_USERS_STRING = "All Users";
    public static final String FREQUENT_USERS_STRING = "Recent Users";
    //public static final String GROUP_MEMBERS_STRING = "Group Members";

    public static final int STATUS_TIMER = R.drawable.ic_av_timer_black_18dp;
    public static final int STATUS_SEND = R.drawable.ic_done_black_18dp;
    public static final int STATUS_NOTIFIED = R.drawable.ic_check_double_black_18dp;
    public static final int STATUS_READ = R.drawable.ic_check_double_black_18dp;
    public static final int STATUS_ERROR = R.drawable.ic_error_black_18dp;
    public static final int DELETED_DRAWABLE = R.drawable.ic_action_cancel_black_18dp;

    public static final int MISSED_VOICECALL_DRAWABLE = R.drawable.baseline_call_missed_black_18;
    public static final int MISSED_VIDEOCALL_DRAWABLE = R.drawable.baseline_missed_video_call_black_18;
    public static final int MISSED_CALL__TINT_COLOR = 0xCC0000;

    public static final int NORMAL_TINT_COLOR = 0xAAAAAA;
    public static final int READ_TINT_COLOR = 0x23b1ef;
    public static final int ERROR_TINT_COLOR = 0xCC0000;
    public static final int DELETED_TINT_COLOR = 0xBBBBBB;

    public static final int DEFAULT_PROFILE_PICTURE = R.drawable.default_user_image;
    public static final int DEFAULT_GROUP_PICTURE = R.drawable.default_group_image;

    public static final String FAVORITED_INCOMING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String FAVORITED_OUTGOING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String NORMAL_INCOMING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
    public static final String NORMAL_OUTGOING_MESSAGE_DATE_SPACE = "\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";

    public static final int HUDDLE_TOPIC_TEXT_COLOR_WITH_PICTURE = 0x000000;
    public static final int HUDDLE_TOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0x000000;

    public static final int TOPIC_TEXT_COLOR_WITH_PICTURE = 0xffa6abad;
    public static final int TOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0xff000000;
    public static final int DELETEDTOPIC_TEXT_COLOR_WITHOUT_PICTURE = 0x77000000;

    public static final String STATUS_COLOR_WITHOUT_PICTURE = "#a6abad";
    public static final String STATUS_COLOR_OVER_PICTURE = "#ffffffff";

    public static final String TOPIC_COLOR = "#354052";

    public static final String HUDDLE_STATUS_COLOR_WITHOUT_PICTURE = "#ffffff";
    public static final String HUDDLE_OUTGOING_CHAT_COLOR = "#272A3D";
    public static final String HUDDLE_STATUS_COLOR_OVER_PICTURE = "#354052";

    public static final int PROGRESSVIEW_DOWNLOAD_SYMBOL = R.drawable.ic_file_download_white_24dp;
    public static final int PROGRESSVIEW_UPLOAD_SYMBOL = R.drawable.ic_file_upload_white_24dp;


    public static final String MESSAGE_STRING_USERLIST_SEARCH = "Messages";
    public static final String USERS_STRING_USERLIST_SEARCH = "Users";
    //public static final String CREATE_NEW_GROUP_STRING = "Create New Group";

    public static final String ATTACHMENT_STRING = "Attachment";
    public static final String LOCATION_STRING = "Location";
    public static final String VIDEO_STRING = "Video";
    public static final String AUDIO_STRING = "Audio";
    public static final String IMAGE_STRING = "Image";
    public static final String MISSED_VIDEO_CALL = "Missed video call";
    public static final String MISSED_VOICE_CALL = "Missed voice call";
    public static final String MESSAGE_DELETED_STRING = "This message was deleted";

    //http://romannurik.github.io/AndroidAssetStudio/icons-generic.html
    //#7f7f7f, 1dp
    public static final int ATTACHMENT_ICON = R.drawable.ic_attachment_grey_18dp;
    public static final int VIDEO_ICON = R.drawable.ic_video_on_grey_18dp;
    public static final int IMAGE_ICON = R.drawable.ic_insert_photo_grey_500_18dp;
    public static final int LOCATION_ICON = R.drawable.ic_location_on_grey_18dp;

    public static final String EMPTY_MESSAGE_LIST = "";

    //public static final String EMPTY_USER_LIST = "You do not have any contacts";
    //    public static final String EMPTY_SEARCH_LIST ="Your search returned no results";
    //public static final String EMPTY_SEARCH_LIST = "No Result Found";
    //    public static final String EMPTY_MESSAGE_LIST ="You do not have any messages";
    public static final String GROUP_ADDRESS = "Create a New Group";


    public static final int LETTER_TITLAR_SIZE = 60;

    public static final int MESIBO_INTITIAL_READ_USERLIST = 100;
    public static final int MESIBO_SUBSEQUENT_READ_USERLIST = 100;
    public static final int MESIBO_SEARCH_READ_USERLIST = 100;


    public static final int MESIBO_INTITIAL_READ_MESSAGEVIEW = 50;
    public static final int MESIBO_SUBSEQUENT_READ_MESSAGEVIEW = 50;

    public static final int EXPIRY_PARAMS_MESSAGEVIEW = 24 * 30 * 3600;

    //public static final int KEYBOARD_ICON = R.mipmap.keyboard;
    //public static final int EMOJI_ICON = R.mipmap.emoji;
    public static final int DEFAULT_LOCATION_IMAGE = R.drawable.bmap;
    public static final int DEFAULT_FILE_IMAGE = R.drawable.file_file;

    public static final String GROUP_TEMP_IMAGE_FILE = "grouptemp.jpg";

    public static final String YOU_STRING_IN_REPLYVIEW = "You";

    public static final String VIDEO_RECORDER_STRING = "Video Recorder";
    public static final String FROM_GALLERY_STRING = "From Gallery";
    public static final String VIDEO_TITLE_STRING = "Select your video from?";

    public static final String COPY_STRING = "Copy";
    public static final String GOOGLE_PLAYSERVICE_STRING = "Please Download Google play service from Google Play store.";

    public static final String TITLE_PERMISON_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_FAIL = "One or more required permission was denied by you! Change the permission from settings and try again";


    public static final String TITLE_PERMISON_LOCATION_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_LOCATION_FAIL = "Location permission was denied by you! Change the permission from settings menu location service";

    public static final String TITLE_PERMISON_CAMERA_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_CAMERA_FAIL = "Camera permission was denied by you! Change the permission from settings menu";

    public static final String TITLE_PERMISON_EXTERNAL_FAIL = "Permission Denied";
    public static final String MSG_PERMISON_EXTERNAL_FAIL = "External Storage permission was denied by you! Change the permission from settings menu";


    public static final String TITLE_INVALID_GROUP = "Invalid group";
    public static final String MSG_INVALID_GROUP = "You are not a member of this group or not allowed to send message to this group";

    public static final int TOOLBAR_COLOR = MesiboUI.getConfig().mToolbarColor;
    public static final int TOOLBAR_TEXT_COLOR = MesiboUI.getConfig().mToolbarTextColor;
    public static final int TOOLBAR_STATUSBAR_COLOR = MesiboUI.getConfig().mStatusbarColor;

    public static final String FILE_NOT_AVAILABLE_TITLE = "File not available";
    public static final String FILE_NOT_AVAILABLE_MSG = "Sorry, this File is no longer available on the server.";

    //public static final String USER_STATUS_TYPING = "typing...";
    //public static final String USER_STATUS_CHATTING = "chatting...";

    public static final String WHATSAPP_NOT_INSTALLED = "Whatsapp app not installed in your phone";
    public static final String PERMISSION_NEEDED = "Permission Needed";
    public static final String MEDIA_PERMISSION_MESSAGE = "This permission is needed to store the media content";
    public static final String EXTERNAL_STORAGE_PERMISSION = "This permission is needed for external storage. If denied you can change permission from Settings.";
    public static final String CONTACT_PERMISSION_MESSAGE = "This permission is needed to sync existing Huddle Contacts and to show all contact list. If denied you can change permission from Settings.";
    public static final String GOOGLE_PLAY_SERVICE_NOT_AVAILABLE = "Google Play Services is not available.";
    public static final String GOOGLE_PLAY_EXCEPTION = "Google Play Services exception.";
    public static final String ASK_TO_DOWNLOAD = "Ask your family and friends to download Huddle";
    public static final String ENTER_PHONE_NUMBER = "Please enter phone number";
    public static final String INCORRECT_OTP = "Incorrect OTP";
    public static final String PERMISSION_DENIED_CONTACTS = "Permission denied to read your Contacts";
    public static final String JOIN_HUDDLE_MESSAGE = "Please join QAMP";
    public static final String MISSED_CALL = "Huddle Missed Call";
    public static final String NO_INTERNET = "No Internet Connection";
    public static final String PHONE_NOT_CONNECTED = "Your phone is not connected to the internet. Please check your internet connection and try again later.";
    public static final String NAME_NOT_LESS_THREE_CHAR = "Name can not be less than 3 characters";
    public static final String STATUS_NOT_LESS_THREE_CHAR = "Status can not be less than 3 characters";


    // Bundle Constants
    public static final String BUNDLE_CONTACT_DETAILS = "contactDetails";
    public static final String BUNDLE_FORWARD_DETAILS = "forwardDetails";
    public static final String BUNDLE_GROUP_DETAILS = "groupDetails";
    public static final String BUNDLE_SEARCH_MESSAGE_LIST = "searchMessageList";
    public static final String BUNDLE_LOGIN_DETAILS = "loginDetails";
    public static final String BUNDLE_UPLOAD = "upload";
    public static final String BUNDLE_SETGROUP = "setgroup";
    public static final String BUNDLE_DELGROUP = "delgroup";
    public static final String BUNDLE_GETGROUP = "getgroup";
    public static final String BUNDLE_EDITMEMBER = "editmembers";
    public static final String BUNDLE_SETADMIN = "setadmin";
    public static final String BUNDLE_PROFILE = "profile";


    // Date
    public static final String DATE_1 = "HH:mm";
    public static final String DATE_2 = "E, dd MMM";
    public static final String DATE_3 = "dd-MM-yyyy";
    public static final String YESTERDAY = "Yesterday";
    public static final String DDMMYY = "dd/MM/yy";
    public static final String TODAY = "Today";

    // Font
    public static final String SAN_SERIF_LIGHT = "sans-serif-light";

    // File names
    public static final String FILE_AUDIO = "file_audio";
    public static final String FILE_DOC = "file_doc";
    public static final String FILE_FILE = "file_file";
    public static final String FILE_PDF = "file_pdf";
    public static final String FILE_TXT = "file_txt";
    public static final String FILE_XLS = "file_xls";
    public static final String FILE_XML = "file_xml";

    public static final String CAMERA_VALUE = "camera";
    public static final String GALLERY_VALUE = "gallery";
    public static final String REMOVE_VALUE = "remove";

    public static List<String> mPermissions = null;
    public static String mPermissionsRequestMessage = "Please grant permissions to continue";
    public static String mPermissionsDeniedMessage = "App will close now since the required permissions were not granted";
    public static String FAVORITED_OUTGOING_MESSAGE_DATE_View ="";
    public static String NORMAL_OUTGOING_MESSAGE_DATE_View="";


    public MesiboConfiguration() {

    }


    public static final int messageBackgroundColorForMe = MesiboUI.getConfig().messageBackgroundColorForMe;
    public static final int messageBackgroundColorForPeer = MesiboUI.getConfig().messageBackgroundColorForPeer;
    public static final int headerCellBackgroundColor = 0xffe1d6a6;
    //public static final int otherCellsBackgroundColor = 0xffc4dff6;
    public static final int otherCellsBackgroundColor = 0xffcce7e8;
    public static final int messagingBackgroundColor = MesiboUI.getConfig().messagingBackgroundColor;


    //http://romannurik.github.io/AndroidAssetStudio/icons-generic.html
    //#7f7f7f, 1dp
}

