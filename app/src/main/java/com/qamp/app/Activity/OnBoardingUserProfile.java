/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Activity;

import static com.qamp.app.MessagingModule.MesiboConfiguration.MSG_PERMISON_CAMERA_FAIL;
import static com.qamp.app.MessagingModule.MesiboConfiguration.TITLE_PERMISON_CAMERA_FAIL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.messaging.RoundImageDrawable;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.BottomSheetFragments.OnboardingBottomSheetFragment;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnBoardingUserProfile extends AppCompatActivity implements MesiboProfile.Listener {

    static final int CAMERA_PERMISSION_CODE = 102;
    static final int EXTENAL_STORAGE_READ_PERMISSION_CODE = 103;
    private static Boolean mSettingsMode = false;
    private final ImageView editUserImage = null;
    private ImageView cameraIcon = null;
    private TextView cameraText, onboardingProfileSubtitle, onboardingProfileTitle;
    private EditText nameEditText;
    private Button saveButton;
    private LinearLayout editPhoto;
    private CircleImageView circleImageView;
    private long mGroupId = 0;
    private boolean mLaunchMesibo = false;

    public OnBoardingUserProfile() {
        mGroupId = 0;
    }

    public static Bitmap decodeUriAsBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.Companion.with(this).saveDir(getExternalFilesDir(Environment.DIRECTORY_DCIM)).cameraOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
            } else {
                //TBD, show alert that you can't continue
                AppUtils.showAlert(OnBoardingUserProfile.this, TITLE_PERMISON_CAMERA_FAIL, MSG_PERMISON_CAMERA_FAIL);

            }
            return;

        }

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
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
                circleImageView.setImageDrawable(getDrawable(R.drawable.rounded_profile_view));
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
                circleImageView.setImageDrawable(getDrawable(R.drawable.rounded_profile_view));
            }
            editPhoto.setVisibility(View.GONE);
            cameraText.setVisibility(View.VISIBLE);
            cameraIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.setStatusBarColor(OnBoardingUserProfile.this, R.color.colorAccent);
        Utilss.setLanguage(OnBoardingUserProfile.this);
        setContentView(R.layout.activity_on_boarding_user_profile);
        initaliseViews();
        nameEditText.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (nameEditText.getRight() - nameEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        nameEditText.getText().clear();
                        return true;
                    }
                }
                return false;
            }
        });
        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnboardingBottomSheetFragment bottomSheetFragment = new OnboardingBottomSheetFragment(false);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getProfile().getImagePath() != null) {
                    QampUiHelper.launchImageViewer(OnBoardingUserProfile.this, getProfile().getImagePath());
                }
            }
        });
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnboardingBottomSheetFragment bottomSheetFragment = new OnboardingBottomSheetFragment(true);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        MesiboProfile profile = getProfile();
        profile.addListener(this);
        profile = getProfile(); // in case profile was updated in between
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                MesiboProfile profile = getProfile();
                if (!TextUtils.isEmpty(name)) {
                    profile.setName(name);
                    profile.save();
                    saveProfile();
                }
                if (mLaunchMesibo) {
                    QampUiHelper.launchMesibo(OnBoardingUserProfile.this, 0, false, true);
                }
                // finish();
            }
        });
        setUserPicture();
        updateUI(profile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utilss.setLanguage(OnBoardingUserProfile.this);
    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile profile) {
        updateUI(profile);
        setUserPicture();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
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

    private void updateUI(MesiboProfile profile) {
        if (!profile.isGroup()) {
            nameEditText.setText(profile.getName());
        }
        if (!TextUtils.isEmpty(profile.getName())) nameEditText.setText(profile.getName());
        nameEditText.setText(profile.getName());
    }

    private void saveProfile() {
        if (AppUtils.isNetWorkAvailable(this)) {
            AppUtils.openProgressDialog(this);
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.updateProfile);
            updateProfile(url);
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

    private void saveProfilePhoto(Uri uri) {
        if (AppUtils.isNetWorkAvailable(this)) {
            AppUtils.openProgressDialog(this);
            String url = QAMPAPIConstants.base_url + String.format(QAMPAPIConstants.updateProfilePhoto);
            updateProfilePhoto(url, uri);
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_LONG).show();
        }
    }

    private void updateProfile(String url) {
        AppUtils.closeProgresDialog();
        RequestQueue requestQueue = Volley.newRequestQueue(OnBoardingUserProfile.this);
        String URL = url;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("version", AppConfig.getConfig().version);
            jsonBody.put("token", AppConfig.getConfig().token);
            jsonBody.put("status", "");
            jsonBody.put("fullName", nameEditText.getText().toString());
            jsonBody.put("firstName", "");
            jsonBody.put("lastName", "");
            jsonBody.put("profilePicId", AppConfig.getConfig().profileId);
            Toast.makeText(this, ""+AppConfig.getConfig().token, Toast.LENGTH_SHORT).show();
            if (AppConfig.getConfig().deviceToken != "") {
                jsonBody.put("androidToken", AppConfig.getConfig().deviceToken);
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
                        String profileId = data.getString("profilePicId");
                        AppConfig.getConfig().name = fullName; //TBD, save into preference
                        AppConfig.getConfig().status = profileStatus;
                        AppConfig.getConfig().version = responseVersion;
                        //AppConfig.getConfig().profileId = profileId;
                        Intent intent = new
                                Intent(
                                OnBoardingUserProfile.this,
                                WelcomeOnboarding.class);
                        finish();
                        startActivity(intent);
                    } else {
                        JSONArray errors = response.getJSONArray("error");
                        JSONObject error = (JSONObject) errors.get(0);
                        String errMsg = error.getString("errMsg");
                        String errorCode = error.getString("errCode");
                        Toast.makeText(OnBoardingUserProfile.this, "" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    AppUtils.closeProgresDialog();
                    e.printStackTrace();
                    Toast.makeText(OnBoardingUserProfile.this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AppUtils.closeProgresDialog();
                Log.e("VOLLEY", error.toString());
                Toast.makeText(OnBoardingUserProfile.this, getString(R.string.general_error), Toast.LENGTH_LONG).show();
            }
        }) {
        };

        requestQueue.add(request);
        AppUtils.closeProgresDialog();
    }

    private void updateProfilePhoto(String url, Uri uri) {
        AppUtils.closeProgresDialog();
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
                        //   Toast.makeText(OnBoardingUserProfile.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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
                if (uri != null) {
                    params.put("profilePic", new DataPart(imagename + ".png", getFileDataFromDrawable(uri), "image/jpeg"));
                }
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        AppUtils.closeProgresDialog();
    }

    private void textFieldListeners() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (!nameEditText.getText().toString().equals("")) {
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        saveButton.setBackgroundDrawable(ContextCompat.getDrawable(OnBoardingUserProfile.this, R.drawable.corner_radius_button));
                        saveButton.setTextColor(ContextCompat.getColor(OnBoardingUserProfile.this, R.color.text_color_black));
                    } else {
                        saveButton.setBackground(ContextCompat.getDrawable(OnBoardingUserProfile.this, R.drawable.corner_radius_button));
                        saveButton.setTextColor(ContextCompat.getColor(OnBoardingUserProfile.this, R.color.text_color_black));
                    }
                } else {
                    final int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        saveButton.setBackgroundDrawable(ContextCompat.getDrawable(OnBoardingUserProfile.this, R.drawable.corner_radius_gray_button));
                        saveButton.setTextColor(ContextCompat.getColor(OnBoardingUserProfile.this, R.color.text_color_button));
                    } else {
                        saveButton.setBackground(ContextCompat.getDrawable(OnBoardingUserProfile.this, R.drawable.corner_radius_gray_button));
                        saveButton.setTextColor(ContextCompat.getColor(OnBoardingUserProfile.this, R.color.text_color_button));
                    }
                }
            }
        });
    }

    public byte[] getFileDataFromDrawable(Uri uri) {
        InputStream iStream = null;
        try {
            iStream = getContentResolver().openInputStream(uri);
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

    private void initaliseViews() {
        // Text Field
        cameraText = findViewById(R.id.cameraText);
        onboardingProfileSubtitle = findViewById(R.id.onboardingProfileSubtitle);
        onboardingProfileTitle = findViewById(R.id.onboardingProfileTitle);
        nameEditText = findViewById(R.id.nameEditText);
        saveButton = findViewById(R.id.saveButton);
        circleImageView = findViewById(R.id.circleImageView);
        editPhoto = findViewById(R.id.editPhoto);
        cameraIcon = findViewById(R.id.cameraIcon);
        cameraText = findViewById(R.id.cameraText);
        textFieldListeners();
        editPhoto.setVisibility(View.GONE);
        cameraText.setVisibility(View.VISIBLE);
        cameraIcon.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        if (resultCode != RESULT_CANCELED) {
            if (uri != null) {
                profile.setImage(decodeUriAsBitmap(OnBoardingUserProfile.this, uri));
            }
        }
        profile.save();
    }

    public void openCamera() {
        ImagePicker.Companion.with(this).saveDir(getExternalFilesDir(Environment.DIRECTORY_DCIM)).cameraOnly().cropSquare().maxResultSize(1080, 1080).start();
    }

    public void openGallery() {
        ImagePicker.with(this).saveDir(getExternalFilesDir(Environment.DIRECTORY_DCIM)).galleryOnly().cropSquare().maxResultSize(1080, 1080).start();
    }

    public void setImageProfile(Uri uri) {
        if (null == uri) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                circleImageView.setImageDrawable(getDrawable(R.drawable.rounded_profile_view));
            }
            circleImageView.setImageURI(null);
            setUserPicture();
        } else {
            circleImageView.setImageURI(uri);
        }

    }

    public void removeProfilePic() {
        setImageProfile(null);
        editPhoto.setVisibility(View.GONE);
        cameraText.setVisibility(View.VISIBLE);
        cameraIcon.setVisibility(View.VISIBLE);

        MesiboProfile profile = getProfile();
        profile.setImage(null);
        profile.save();
    }

    @Override
    public void onPause() {
        super.onPause();
        MesiboProfile profile = getProfile();
        profile.removeListener(this);
    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }
}