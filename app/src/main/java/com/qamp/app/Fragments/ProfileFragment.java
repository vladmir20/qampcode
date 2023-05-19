/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.CAMERA_VALUE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.GALLERY_VALUE;
import static com.qamp.app.MessagingModule.MesiboConfiguration.REMOVE_VALUE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.emojiview.EmojiconEditText;
import com.mesibo.messaging.RoundImageDrawable;
import com.qamp.app.Activity.SelectLanguage;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.BottomSheetFragments.ProfileBottomSheetFragment;
import com.qamp.app.Modal.ProfileModel;
import com.qamp.app.Utils.QAMPAPIConstants;
import com.qamp.app.Utils.QampConstants;
import com.qamp.app.Utils.QampUiHelper;
import com.qamp.app.R;
import com.qamp.app.Utils.UTF8JsonObjectRequest;
import com.qamp.app.Utils.AppUtils;
import com.qamp.app.Utils.Utilss;
import com.qamp.app.Utils.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements MesiboProfile.Listener, View.OnClickListener {

    static final int CAMERA_PERMISSION_CODE = 102;
    static final int EXTENAL_STORAGE_READ_PERMISSION_CODE = 103;
    private static final int MAX_NAME_CHAR = 50;
    private static final int MAX_STATUS_CHAR = 150;
    private static Boolean mSettingsMode = false;
    private final MesiboProfile mProfile = Mesibo.getSelfProfile();
    public View mView = null;
    ImageView edit1, edit2, edit3, edit4;
    EmojiconEditText mEmojiNameEditText, mPhoneNumber, eMail;
    EmojiconEditText mEmojiStatusEditText;
    ImageView mEmojiNameBtn;
    ImageView mEmojiStatusBtn;
    Fragment mHost;
    TextView profilePhoneLabel;
    LinearLayout editPhoto;
    ImageView cameraIcon;
    CircleImageView circleImageView;
    TextView cameraText;
    private ProgressDialog mProgressDialog;
    private GridView profileGrid;
    private ArrayList<ProfileModel> extrasModel;
    private TextView current_Lang;
    private ImageView edit_lang;
    private Uri staticUri = null;
    private long mGroupId = 0;
    private boolean mLaunchMesibo = false;

    public ProfileFragment() {
        mGroupId = 0;
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;//from   w  ww  . j  a  v  a2s.  c om
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static boolean saveBitmpToFilePath(Bitmap bmp, String filePath) {
        File file = new File(filePath);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        if (null != bmp) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, fOut);

            try {
                fOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void setGroupId(long groupid) {
        mGroupId = groupid;
    }

    public void setLaunchMesibo(boolean launchMesibo) {
        mLaunchMesibo = launchMesibo;
    }

    public MesiboProfile getProfile() {
        if (mGroupId > 0) return Mesibo.getProfile(mGroupId);
        return Mesibo.getSelfProfile();
    }

    public void activateInSettingsMode() {
        mSettingsMode = true;
    }

    void setUserPicture() {
        MesiboProfile profile = getProfile();
        Bitmap image = profile.getImageOrThumbnail();
        if (null != image) {
            circleImageView.setImageDrawable(new RoundImageDrawable(image));
            editPhoto.setVisibility(View.VISIBLE);
            cameraText.setVisibility(View.GONE);
            cameraIcon.setVisibility(View.GONE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleImageView.setImageDrawable(getActivity().getDrawable(R.drawable.rounded_profile_view));
            }
            editPhoto.setVisibility(View.GONE);
            cameraText.setVisibility(View.VISIBLE);
            cameraIcon.setVisibility(View.VISIBLE);
        }
        if (true) return;
        String url = profile.getImageUrl();
        String filePath = getProfile().getImagePath();
        Bitmap b;
        if (Mesibo.fileExists(filePath)) {
            b = BitmapFactory.decodeFile(filePath);
            if (null != b) {
                circleImageView.setImageDrawable(new RoundImageDrawable(b));
                editPhoto.setVisibility(View.VISIBLE);
                cameraText.setVisibility(View.GONE);
                cameraIcon.setVisibility(View.GONE);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleImageView.setImageDrawable(getActivity().getDrawable(R.drawable.rounded_profile_view));
            }
            editPhoto.setVisibility(View.GONE);
            cameraText.setVisibility(View.VISIBLE);
            cameraIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        mView = v;
        initaliseViews(mView);
        Utilss.setLanguage(getActivity());
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings", MODE_PRIVATE);
        String language = preferences.getString("app_lang", "");
        current_Lang.setText(getResources().getString(R.string.current_language) + language);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getProfile().getImagePath() != null) {
                    QampUiHelper.launchImageViewer(getActivity(), getProfile().getImagePath());
                }
            }
        });
        edit_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectLanguage.class);
                startActivity(intent);
            }
        });
        editPhoto.setOnClickListener(this);
        cameraIcon.setOnClickListener(this);
        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editOne = "editOne";
                bottomFragment(editOne);
            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editTwo = "editTwo";
                bottomFragment(editTwo);
            }
        });
        edit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editFour = "editFour";
                bottomFragment(editFour);
            }
        });

//        mHost = this;
//        mProgressDialog = AppUtils.getProgressDialog(getActivity(), "Please wait...");
//        mPhoneNumber.setEnabled(false);
//        if (0 == getProfile().getGroupId()) {
//            mPhoneNumber.setText(AppUtils.getFormatedNumber(getProfile().address));
//        } else {
//            mPhoneNumber.setVisibility(View.GONE);
//            profilePhoneLabel.setVisibility(View.GONE);
//        }
//       // profileGrid.setOnItemClickListener(this);
//
        extrasModel = new ArrayList<ProfileModel>();

//
//
//      //  editPhoto.setOnClickListener(this);
//        eMail.setEnabled(false);
//        mEmojiNameEditText.setEnabled(false);
//        System.out.println(mProfile.getName() + "Aditya");
//        if (!TextUtils.isEmpty(mProfile.getName())) mEmojiNameEditText.setText(mProfile.getName());
//        mEmojiNameEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NAME_CHAR)});
//        //mEmojiNameEditText.addTextChangedListener(emojiNameEditTextWatcher);
//        mEmojiStatusEditText.setEnabled(false);
//        if (!TextUtils.isEmpty(mProfile.getStatus()))
//            mEmojiStatusEditText.setText(mProfile.getStatus());
//        mEmojiStatusEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_STATUS_CHAR)});
//      //  mEmojiStatusEditText.addTextChangedListener(emojiStatusEditTextWatcher);
//
//        MesiboProfile profile = getProfile();
//
//        if (!TextUtils.isEmpty(profile.getName()) && profile.isGroup()) {
//            mEmojiNameEditText.setText(profile.getName());
//            mEmojiStatusEditText.setText(profile.getStatus());
//        }
//
////        mEmojiNameBtn = (ImageView) v.findViewById(R.id.name_emoji_btn);
////        mEmojiStatusBtn = (ImageView) v.findViewById(R.id.status_emoji_btn);
//
//
//        editPhoto.setVisibility(View.GONE);
//        cameraText.setVisibility(View.VISIBLE);
//        cameraIcon.setVisibility(View.VISIBLE);
//
//        FrameLayout rootView = v.findViewById(R.id.register_new_profile_rootlayout);
//        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
//        final EmojiconsPopup popup = new EmojiconsPopup(rootView, getActivity());
//
//        //Will automatically set size according to the soft keyboard size
//        popup.setSizeForSoftKeyboard();
//
//        View.OnClickListener emojilistener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                EmojiconEditText mEmojiEditText = mEmojiNameEditText;
//                ImageView mEmojiButton = mEmojiNameBtn;
//
////                if (v.getId() == R.id.status_emoji_btn) {
////                    mEmojiEditText = mEmojiStatusEditText;
////                    mEmojiButton = mEmojiStatusBtn;
////                }
//
//                //If popup is not showing => emoji keyboard is not visible, we need to show it
//                if (!popup.isShowing()) {
//
//                    //If keyboard is visible, simply show the emoji popup
//                    if (popup.isKeyBoardOpen()) {
//                        popup.showAtBottom();
//                        changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_keyboard);
//                    }
//                    //else, open the text keyboard first and immediately after that show the emoji popup
//                    else {
//                        mEmojiEditText.setFocusableInTouchMode(true);
//                        mEmojiEditText.requestFocus();
//                        popup.showAtBottomPending();
//                        final InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                        inputMethodManager.showSoftInput(mEmojiEditText, InputMethodManager.SHOW_IMPLICIT);
//                        changeEmojiKeyboardIcon(mEmojiButton, R.drawable.ic_keyboard);
//                    }
//                }
//                //If popup is showing, simply dismiss it to show the undelying text keyboard
//                else {
//                    popup.dismiss();
//                }
//            }
//        };
//
////        mEmojiNameBtn.setOnClickListener(emojilistener);
////        mEmojiStatusBtn.setOnClickListener(emojilistener);
//
//        //If the emoji popup is dismissed, change emojiButton to smiley icon
//        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                changeEmojiKeyboardIcon(mEmojiNameBtn, R.drawable.ic_sentiment_satisfied_black_24dp);
//                changeEmojiKeyboardIcon(mEmojiStatusBtn, R.drawable.ic_sentiment_satisfied_black_24dp);
//            }
//        });
//
//        //If the text keyboard closes, also dismiss the emoji popup
//        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
//            @Override
//            public void onKeyboardOpen(int keyBoardHeight) {
//            }
//
//            @Override
//            public void onKeyboardClose() {
//                if (popup.isShowing()) popup.dismiss();
//            }
//        });
//
//        //On emoji clicked, add it to edittext
//        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
//            @Override
//            public void onEmojiconClicked(Emojicon emojicon) {
//                EmojiconEditText mEmojiEditText = mEmojiNameEditText;
//                if (mEmojiStatusEditText.hasFocus()) {
//                    mEmojiEditText = mEmojiStatusEditText;
//                }
//
//                if (mEmojiEditText == null || emojicon == null) {
//                    return;
//                }
//
//                int start = mEmojiEditText.getSelectionStart();
//                int end = mEmojiEditText.getSelectionEnd();
//                if (start < 0) {
//                    mEmojiEditText.append(emojicon.getEmoji());
//                } else {
//                    mEmojiEditText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
//                }
//            }
//        });
//
//        //On backspace clicked, emulate the KEYCODE_DEL key event
//        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {
//            @Override
//            public void onEmojiconBackspaceClicked(View v) {
//                EmojiconEditText mEmojiEditText = mEmojiNameEditText;
//                if (mEmojiStatusEditText.hasFocus()) {
//                    mEmojiEditText = mEmojiStatusEditText;
//                }
//                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
//                mEmojiEditText.dispatchKeyEvent(event);
//            }
//        });
//
//        if (AppConfig.getConfig().name.equals("null") || AppConfig.getConfig().name.isEmpty()) {
//            mEmojiNameEditText.setText("");
//        } else {
//            mEmojiNameEditText.setText(AppConfig.getConfig().name);
//        }
//        if (AppConfig.getConfig().status.equals("null") || AppConfig.getConfig().status.isEmpty()) {
//            mEmojiStatusEditText.setText("");
//        } else {
//            mEmojiStatusEditText.setText(AppConfig.getConfig().status);
//        }
        MesiboProfile profile = getProfile();
        profile.addListener(this);
        profile = getProfile(); // in case profile was updated in between
        setUserPicture();
        updateUI(profile);
        setUserPicture();
        getThumbnailList();
        getUserDataFromUrl();
        return v;
    }

    private void getUserDataFromUrl() {

    }

    @Override
    public void onStart() {
        super.onStart();
        Utilss.setLanguage(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilss.setLanguage(getActivity());
    }

    private void initaliseViews(View v) {
        edit1 = v.findViewById(R.id.edit1);
        edit2 = v.findViewById(R.id.edit2);
        edit3 = v.findViewById(R.id.edit3);
        edit4 = v.findViewById(R.id.edit4);
        current_Lang = v.findViewById(R.id.current_Lang);
        edit_lang = v.findViewById(R.id.edit_lang);
        mPhoneNumber = v.findViewById(R.id.profile_self_phone);
        profilePhoneLabel = v.findViewById(R.id.profile_phone_label);
        profileGrid = v.findViewById(R.id.profile_pic_grid);
        circleImageView = v.findViewById(R.id.circleImageView);
        cameraIcon = v.findViewById(R.id.cameraIcon);
        editPhoto = v.findViewById(R.id.editPhoto);
        eMail = v.findViewById(R.id.email);
        mEmojiNameEditText = v.findViewById(R.id.name_emoji_edittext);
        mEmojiStatusEditText = v.findViewById(R.id.status_emoji_edittext);
        cameraText = v.findViewById(R.id.cameraText);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.self_user_image:
                //if (!Utils.hasImage(mProfileImage)) {
                BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                dialog.setContentView(R.layout.fragment_profile_bottom_sheet);
                LinearLayout cameraButton, galleryButton;
                ImageView removeButton;
                cameraButton = dialog.findViewById(R.id.camera_container);
                galleryButton = dialog.findViewById(R.id.gallery_container);
                removeButton = dialog.findViewById(R.id.remove_button);

                Intent i = new Intent();

                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i.putExtra("buttonTapped", CAMERA_VALUE);
                        dialog.dismiss();
                        openCamera();
                    }
                });

                galleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i.putExtra("buttonTapped", GALLERY_VALUE);
                        dialog.dismiss();
                        openGallery();
                    }
                });

                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i.putExtra("buttonTapped", REMOVE_VALUE);
                        dialog.dismiss();
                        removeProfilePic();
                    }
                });
                dialog.show();

                break;

            case R.id.editPhoto:
                ProfileBottomSheetFragment profileBottomSheetFragment = new ProfileBottomSheetFragment(true);
                profileBottomSheetFragment.show(getParentFragmentManager(), profileBottomSheetFragment.getTag());
                break;
            case R.id.cameraIcon:
                ProfileBottomSheetFragment profileBottomSheetFragment2 = new ProfileBottomSheetFragment(false);
                profileBottomSheetFragment2.show(getParentFragmentManager(), profileBottomSheetFragment2.getTag());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 5001) {
//            if (resultCode == RESULT_OK) {
//                if (data.getStringExtra("buttonTapped").equals(CAMERA_VALUE)) {
//                    if (AppUtils.aquireUserPermission(getActivity(), Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)) {
//                        //   MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_CAMERAIMAGE);
//                        ImagePicker.Companion.with(this)
//                                .saveDir(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM))
//                                .cameraOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
//                        return;
//                    }
//                } else if (data.getStringExtra("buttonTapped").equals(GALLERY_VALUE)) {
//                    if (AppUtils.aquireUserPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, EXTENAL_STORAGE_READ_PERMISSION_CODE)) {
//                        //   MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILEIMAGE);
//                        ImagePicker.with(this)
//                                .saveDir(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM))
//                                .galleryOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
//                        return;
//                    }
//                    return;
//                } else if (data.getStringExtra("buttonTapped").equals(REMOVE_VALUE)) {
//                    setImageProfile(null);
//
//                    editPhoto.setVisibility(View.GONE);
//                    cameraText.setVisibility(View.VISIBLE);
//                    cameraIcon.setVisibility(View.VISIBLE);
//
//                    return;
//                }
//            }
//        } else if (requestCode == 10001) {
//            if (RESULT_OK != resultCode) return;
//
//            String filePath = MediaPicker.processOnActivityResult(getActivity(), requestCode, resultCode, data);
//
//            if (null == filePath) return;
//
//            QampUiHelper.launchImageEditor(getActivity(), MediaPicker.TYPE_FILEIMAGE, -1, null, filePath, false, false, true, true, 1200, this);
//        } else if (requestCode == 10000) {
//            if (RESULT_OK != resultCode) return;
//
//            String filePath = MediaPicker.processOnActivityResult(getActivity(), requestCode, resultCode, data);
//
//            if (null == filePath) return;
//
//            QampUiHelper.launchImageEditor(getActivity(), MediaPicker.TYPE_FILEIMAGE, -1, null, filePath, false, false, true, true, 1200, this);
//        }
        if (data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    editPhoto.setVisibility(View.VISIBLE);
                    cameraText.setVisibility(View.GONE);
                    cameraIcon.setVisibility(View.GONE);
                } else {
                    editPhoto.setVisibility(View.GONE);
                    cameraText.setVisibility(View.VISIBLE);
                    cameraIcon.setVisibility(View.VISIBLE);
                }
                setImageProfile(uri);
                saveProfilePhoto(uri);
                MesiboProfile profile = getProfile();
                profile.setImage(decodeUriAsBitmap(getActivity(), uri));
                profile.save();
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }

    }

    public void openCamera() {
        ImagePicker.Companion.with(this)
                .saveDir(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM))
                .cameraOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
    }

    public void openGallery() {
        ImagePicker.with(this)
                .saveDir(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM))
                .galleryOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                 ImagePicker.Companion.with(this)
//                        .saveDir(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM))
//                        .cameraOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
//            } else {
//                Toast.makeText(getActivity(), MSG_PERMISON_CAMERA_FAIL, Toast.LENGTH_LONG).show();
//            }
//            return;
//        } else if (requestCode == EXTENAL_STORAGE_READ_PERMISSION_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                 ImagePicker.with(this)
//                        .saveDir(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM))
//                        .galleryOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
//            } else {
//                Toast.makeText(getActivity(), MSG_PERMISON_CAMERA_FAIL, Toast.LENGTH_LONG).show();
//            }
//            return;
//        }
//    }

    public void removeProfilePic() {
        setImageProfile(null);
        editPhoto.setVisibility(View.GONE);
        cameraText.setVisibility(View.VISIBLE);
        cameraIcon.setVisibility(View.VISIBLE);

        MesiboProfile profile = getProfile();
        profile.setImage(null);
        profile.save();

    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    public void setImageProfile(Uri uri) {
        if (null == uri) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleImageView.setImageDrawable(getActivity().getDrawable(R.drawable.rounded_profile_view));
            }
            circleImageView.setImageURI(null);
        } else {
            circleImageView.setImageURI(uri);
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        if (path != null){
//            return Uri.parse(path);
//        }else{
//            return Uri.parse("");
//        }
        File imagesFolder = new File(inContext.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdir();
            File file = new File(imagesFolder, "pic.jpg");
            FileOutputStream stream = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(inContext.getApplicationContext(), "com.qamp.app" + ".provider", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

    public void openDialogue(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onImageEdit(int i, String s, String filePath, Bitmap bitmap, int status) {
        if (0 != status) {
            return;
        }

        if (null != staticUri) {
            saveProfilePhoto(staticUri);
            setImageProfile(getImageUri(getContext(), bitmap));
            editPhoto.setVisibility(View.VISIBLE);
            cameraText.setVisibility(View.GONE);
            cameraIcon.setVisibility(View.GONE);
            AppConfig.getConfig().photo = AppUtils.saveToInternalStorage(getActivity().getApplicationContext(), bitmap);
        }
    }


//    private void getProfilePhoto() {
//        if (AppUtils.isNetWorkAvailable(getContext())) {
//            AppUtils.openProgressDialog(getActivity());
//            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.getProfilePhotoById) + String.format(AppConfig.getConfig().profileId);
//            getProfilePhoto(url);
//        } else {
//            Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
//        }
//    }

    private void getThumbnailList() {
        if (AppUtils.isNetWorkAvailable(getContext())) {
            AppUtils.openProgressDialog(getActivity());
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.getThumbnailList);
            getThumbnailList(url);
        } else {
            Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

    public void saveProfile() {
        if (AppUtils.isNetWorkAvailable(getContext())) {
            AppUtils.openProgressDialog(getActivity());
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.updateProfile);
            updateProfile(url, false, "");
        } else {
            Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

//    public void updateUID(String uid) {
//        if (AppUtils.isNetWorkAvailable(getContext())) {
//            AppUtils.openProgressDialog(getActivity());
//            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.updateProfile);
//            updateProfile(url, true, uid);
//        } else {
//            Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
//        }
//    }

    private void saveProfilePhoto(Uri uri) {
        if (AppUtils.isNetWorkAvailable(getContext())) {
            AppUtils.openProgressDialog(getActivity());
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.updateProfilePhoto);
            updateProfilePhoto(url, uri);
        } else {
            Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

//    private void getProfilePhoto(String url) {
//        String URL = url;
//        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
//            @Override
//            public void onResponse(NetworkResponse response) {
//                try {
//                    AppUtils.closeProgresDialog();
//                    Log.i("VOLLEY======", new String(response.data));
//                    JSONObject jsonObject = new JSONObject(new String(response.data));
//                    String status = jsonObject.getString("status");
//                    Log.e("VOLLEY Status======", status);
//                    if (status.contains(QampConstants.success)) {
//                        JSONArray jsonArray2 = jsonObject.getJSONArray("data");
//                        JSONObject jsonObject4 = jsonArray2.getJSONObject(0);
//                        JSONObject buffer = jsonObject4.getJSONObject("buffer");
//                        JSONArray jsonArray = buffer.getJSONArray("data");
//
//                        byte[] bytes = new byte[jsonArray.length()];
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            bytes[i] = (byte) (((int) jsonArray.get(i)) & 0xFF);
//                        }
//                        Base64.encodeToString(bytes, Base64.DEFAULT);
//                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        setImageProfile(getImageUri(getContext(), bmp));
//                        editPhoto.setVisibility(View.VISIBLE);
//                        cameraText.setVisibility(View.GONE);
//                        cameraIcon.setVisibility(View.GONE);
//                        AppConfig.getConfig().photo = AppUtils.saveToInternalStorage(getActivity().getApplicationContext(), bmp);
//                    } else {
//                        JSONArray errors = jsonObject.getJSONArray("error");
//                        JSONObject error = (JSONObject) errors.get(0);
//                        String errMsg = error.getString("errMsg");
//                        String errorCode = error.getString("errCode");
//                        Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                editPhoto.setVisibility(View.GONE);
//                cameraText.setVisibility(View.VISIBLE);
//                cameraIcon.setVisibility(View.VISIBLE);
//                //    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
//
//                Log.e("GotError", "" + error.getMessage());
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("user-session-token", AppConfig.getConfig().token);
////                headers.put("user-session-version", String.valueOf(AppConfig.getConfig().version));
//                headers.put("content-type", "multipart/form-data");
//                return headers;
//            }
//        };
//
//        //adding the request to volley
//        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
//    }

    private void getThumbnailList(String url) {
        String URL = url;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.GET, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    AppUtils.closeProgresDialog();
                    Log.i("VOLLEY======", new String(response.data));
                    JSONObject jsonObject = new JSONObject(new String(response.data));
                    String status = jsonObject.getString("status");
                    Log.e("VOLLEY Status======", status);
                    if (status.contains(QampConstants.success)) {
                        JSONArray jsonArray2 = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray2.length(); i++) {
                            JSONObject jsonObject4 = jsonArray2.getJSONObject(i);
                            JSONObject buffer = jsonObject4.getJSONObject("buffer");
                            String uid = jsonObject4.getString("uid");
                            JSONArray jsonArray = buffer.getJSONArray("data");
                            extrasModel.add(new ProfileModel("ProfileImage", i, "ic_close", jsonArray, uid));
                        }

                        profileGrid.setAdapter(new ProfileAdapter(getContext(), extrasModel));
                    } else {
                        JSONArray errors = jsonObject.getJSONArray("error");
                        JSONObject error = (JSONObject) errors.get(0);
                        String errMsg = error.getString("errMsg");
                        String errorCode = error.getString("errCode");
                        Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                editPhoto.setVisibility(View.GONE);
                cameraText.setVisibility(View.VISIBLE);
                cameraIcon.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();

                Log.e("GotError", "" + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("user-session-token", AppConfig.getConfig().token);
                headers.put("page-number", String.valueOf(1));
                headers.put("page-size", String.valueOf(5));
//                headers.put("user-session-version", String.valueOf(AppConfig.getConfig().version));
                headers.put("content-type", "multipart/form-data");
                return headers;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }

    private void updateProfile(String url, Boolean shouldUpdateUID, String uid) {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String URL = url;
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("version", AppConfig.getConfig().version);
            jsonBody.put("token", AppConfig.getConfig().token);
            jsonBody.put("status", mEmojiStatusEditText.getText().toString());
            jsonBody.put("fullName", mEmojiNameEditText.getText().toString());
            jsonBody.put("firstName", "");
            jsonBody.put("lastName", "");
            jsonBody.put("profilePicId", AppConfig.getConfig().profileId);
            if (shouldUpdateUID) {
                jsonBody.put("uid", uid);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.PUT, URL, jsonBody, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    AppUtils.closeProgresDialog();
                    String status = response.getString("status");
                    Log.e("VOLLEY Status======", status);
                    if (status.contains(QampConstants.success)) {
                        JSONObject data = response.getJSONObject("data");
                        String fullName = data.getString("fullName");
                        String profileStatus = data.getString("status");
                        int responseVersion = data.getInt("version");
                        AppConfig.getConfig().name = fullName; //TBD, save into preference
                        AppConfig.getConfig().status = profileStatus;
                        AppConfig.getConfig().version = responseVersion;
//                        if (shouldUpdateUID) {
//                            getProfilePhoto();
//                        } else {
//                            Toast.makeText(getContext(), R.string.success_profile, Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        JSONArray errors = response.getJSONArray("error");
                        JSONObject error = (JSONObject) errors.get(0);
                        String errMsg = error.getString("errMsg");
                        String errorCode = error.getString("errCode");
                        // Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    AppUtils.closeProgresDialog();
                    e.printStackTrace();
                    Toast.makeText(getContext(), getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtils.closeProgresDialog();
                Log.e("VOLLEY", error.toString());
                Toast.makeText(getContext(), getString(R.string.general_error), Toast.LENGTH_LONG).show();
            }
        }) {
        };

        requestQueue.add(request);
    }

    private void updateProfilePhoto(String url, Uri uri) {
        String URL = url;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    AppUtils.closeProgresDialog();
                    Log.i("VOLLEY======", new String(response.data));
                    JSONObject jsonObject = new JSONObject(new String(response.data));
                    String status = jsonObject.getString("status");
                    Log.e("VOLLEY Status======", status);
                    if (status.contains(QampConstants.success)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String uid = data.getString("uid");
                        String profileId = data.getString("profilePicId");
                        int responseVersion = data.getInt("version");
                        AppConfig.getConfig().uid = uid; //TBD, save into preference
                        AppConfig.getConfig().profileId = profileId;
                        AppConfig.getConfig().version = responseVersion;
                    } else {
                        JSONArray errors = jsonObject.getJSONArray("error");
                        JSONObject error = (JSONObject) errors.get(0);
                        String errMsg = error.getString("errMsg");
                        String errorCode = error.getString("errCode");
                        // Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    AppUtils.closeProgresDialog();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("GotError", "" + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("user-session-token", AppConfig.getConfig().token);
                headers.put("user-session-version", String.valueOf(AppConfig.getConfig().version));
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("profilePic", new DataPart(imagename + ".png", getFileDataFromDrawable(uri), "image/jpeg"));

                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Uri uri) {
        InputStream iStream = null;
        try {
            iStream = getActivity().getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] inputData = new byte[0];
        try {
            inputData = getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


//    @Override
//    public void OnSearchClick() {
//        saveProfile();
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        // on profile photo tap
//        ProfileModel extraData = extrasModel.get(i);
//        updateUID(extraData.getUid());
//    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile profile) {
        updateUI(profile);
        setUserPicture();
    }


    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    private void updateUI(MesiboProfile profile) {
        if (!profile.isGroup()) {
            mPhoneNumber.setText(AppUtils.getFormatedNumber(profile.address));
        } else {
            mPhoneNumber.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(profile.getStatus()))
            mEmojiStatusEditText.setText(profile.getStatus());
        if (!TextUtils.isEmpty(profile.getName()))
            mEmojiNameEditText.setText(profile.getName());
    }

    private int getCellHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels / 8;

        return width;
    }

    private void bottomFragment(String string) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        switch (string) {
            case "editOne":
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_name);
                TextView cancel = bottomSheetDialog.findViewById(R.id.cancel);
                TextView submit = bottomSheetDialog.findViewById(R.id.save);
                EditText name = bottomSheetDialog.findViewById(R.id.name);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEmojiNameEditText.setText(name.getText());
                        String name = mEmojiNameEditText.getText().toString();
                        MesiboProfile profile = getProfile();
                        if (!TextUtils.isEmpty(name)) {
                            profile.setName(name);
                            profile.save();
                            AppUtils.closeProgresDialog();
                            UpdateNameStatusEmail(name, "Name", AppConfig.getConfig().version);
                        }
                        if (mLaunchMesibo) {
                            QampUiHelper.launchMesibo(getActivity(), 0, false, true);
                        }
                        Toast toast3 = Toast.makeText(getContext(), getResources().getString(R.string.updated_successfully), Toast.LENGTH_LONG);
                        toast3.show();
                        bottomSheetDialog.dismiss();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                break;
            case "editTwo":
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_about);
                TextView cancel1 = bottomSheetDialog.findViewById(R.id.cancel1);
                TextView submit1 = bottomSheetDialog.findViewById(R.id.submit1);
                EditText about = bottomSheetDialog.findViewById(R.id.about);

                submit1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mEmojiStatusEditText.setText(about.getText());
                        String status = mEmojiStatusEditText.getText().toString();

                        MesiboProfile profile = getProfile();
                        if (!TextUtils.isEmpty(status)) {
                            profile.setStatus(status);
                            profile.save();
                            AppUtils.closeProgresDialog();
                            UpdateNameStatusEmail(status, "Status", AppConfig.getConfig().version);
                        }
                        if (mLaunchMesibo) {
                            QampUiHelper.launchMesibo(getActivity(), 0, false, true);
                        }
                        Toast toast2 = Toast.makeText(getContext(), R.string.updated_successfully, Toast.LENGTH_LONG);
                        toast2.show();
                        bottomSheetDialog.dismiss();
                    }
                });

                cancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                break;
            case "editFour":
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_email);

                TextView cancel2 = bottomSheetDialog.findViewById(R.id.cancel3);
                TextView submit2 = bottomSheetDialog.findViewById(R.id.submit);
                EditText contactNo = bottomSheetDialog.findViewById(R.id.editTextTextPersonName3);

                submit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = eMail.getText().toString();


                        if (!TextUtils.isEmpty(email)) {
                            AppUtils.closeProgresDialog();
                            UpdateNameStatusEmail(email, "Email", AppConfig.getConfig().version);
                        }
                        if (mLaunchMesibo) {
                            QampUiHelper.launchMesibo(getActivity(), 0, false, true);
                        }
                        Toast toast1 = Toast.makeText(getContext(), getResources().getString(R.string.updated_successfully), Toast.LENGTH_LONG);
                        toast1.show();
                        bottomSheetDialog.dismiss();
                    }
                });

                cancel2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                break;

        }
        bottomSheetDialog.show();
    }

    private void UpdateNameStatusEmail(String data, String key, int version) {
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        String URL = QAMPAPIConstants.base_url + QAMPAPIConstants.updateProfile;
        JSONObject jsonBody = new JSONObject();
        try {
            if (key.equals("Name")) {
                jsonBody.put("version", version);
                jsonBody.put("token", AppConfig.getConfig().token);
                jsonBody.put("status", mEmojiStatusEditText.getText().toString());
                jsonBody.put("fullName", data);
                jsonBody.put("firstName", "TEST");
                jsonBody.put("lastName", "TEST");
                jsonBody.put("profilePicId", AppConfig.getConfig().profileId);
                jsonBody.put("email", eMail.getText().toString());
            } else if (key.equals("Email")) {
                jsonBody.put("version", version);
                jsonBody.put("token", AppConfig.getConfig().token);
                jsonBody.put("status", mEmojiStatusEditText.getText().toString());
                jsonBody.put("fullName", mEmojiNameEditText.getText().toString());
                jsonBody.put("firstName", "TEST");
                jsonBody.put("lastName", "TEST");
                jsonBody.put("profilePicId", AppConfig.getConfig().profileId);
                jsonBody.put("email", data);
            } else if (key.equals("Status")) {
                jsonBody.put("version", version);
                jsonBody.put("token", AppConfig.getConfig().token);
                jsonBody.put("status", data);
                jsonBody.put("fullName", mEmojiNameEditText.getText().toString());
                jsonBody.put("firstName", "TEST");
                jsonBody.put("lastName", "TEST");
                jsonBody.put("profilePicId", AppConfig.getConfig().profileId);
                jsonBody.put("email", eMail.getText().toString());
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        UTF8JsonObjectRequest request = new UTF8JsonObjectRequest(Request.Method.PUT, URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            AppUtils.closeProgresDialog();
                            String status = response.getString("status");
                            Log.e("VOLLEY Status======", status);
                            if (status.contains(QampConstants.success)) {
                                JSONObject data = response.getJSONObject("data");
                                String fullName = data.getString("fullName");
                                String profileStatus = data.getString("status");
                                String email = "";
                                if (data.has("email")) {
                                    email = data.getString("email");
                                }
                                int responseVersion = data.getInt("version");
                                AppConfig.getConfig().name = fullName; //TBD, save into preference
                                AppConfig.getConfig().status = profileStatus;
                                AppConfig.getConfig().version = responseVersion;
                             //   AppConfig.getConfig().email = email;
                            } else {
                                JSONArray errors = response.getJSONArray("error");
                                JSONObject error = (JSONObject) errors.get(0);
                                String errMsg = error.getString("errMsg");
                                String errorCode = error.getString("errCode");
                                //    Toast.makeText(getContext(), errMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            AppUtils.closeProgresDialog();
                            e.printStackTrace();
                            Toast.makeText(getContext(), getString(R.string.general_error), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtils.closeProgresDialog();
                Log.e("VOLLEY", error.toString());
                Toast.makeText(getContext(), getString(R.string.general_error), Toast.LENGTH_LONG).show();
            }
        }) {
        };

        requestQueue.add(request);
    }

    private class ProfileAdapter extends ArrayAdapter<ProfileModel> {
        // for view inflation
        private final LayoutInflater inflater;
        // days with events
        private HashMap<String, Integer> eventDays; // Date format
        private HashMap<Integer, String> colorMap;
        private int cellHeight = 0;

        public ProfileAdapter(Context context, ArrayList<ProfileModel> data) {
            super(context, R.layout.cell_profile_thumbnail, data);
            this.eventDays = eventDays;
            this.colorMap = colorMap;
            inflater = LayoutInflater.from(context);
            cellHeight = getCellHeight();
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {

            ProfileModel extraData = getItem(position);

//            if (view == null) {
            view = inflater.inflate(R.layout.cell_profile_thumbnail, parent, false);
            view.setMinimumHeight(cellHeight);
//            }

            LinearLayout rlParent = view.findViewById(R.id.rl_parent);
            ImageView extraImage = view.findViewById(R.id.extra_pic);

            byte[] bytes = new byte[extraData.getImageDate().length()];
            for (int i = 0; i < extraData.getImageDate().length(); i++) {
                try {
                    bytes[i] = (byte) (((int) extraData.getImageDate().get(i)) & 0xFF);
                } catch (Exception e) {

                }
            }
            Base64.encodeToString(bytes, Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            extraImage.setImageBitmap(bmp);

            rlParent.setMinimumHeight(cellHeight);

            return view;
        }
    }
}