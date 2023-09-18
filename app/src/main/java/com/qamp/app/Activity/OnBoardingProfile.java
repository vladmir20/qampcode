package com.qamp.app.Activity;

import android.Manifest;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.messaging.RoundImageDrawable;
import com.qamp.app.Fragment.OnboardingBottomSheetFragment;
import com.qamp.app.MesiboImpModules.ContactSyncClass;
import com.qamp.app.Modal.QampContactScreenModel;
import com.qamp.app.R;
import com.qamp.app.Utils.AppConfig;
import com.qamp.app.Utils.AppUtils;

import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnBoardingProfile extends AppCompatActivity implements MesiboProfile.Listener, Mesibo.ConnectionListener {
    static final int CAMERA_PERMISSION_CODE = 102;
    static final int EXTENAL_STORAGE_READ_PERMISSION_CODE = 103;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static Boolean mSettingsMode = false;
    private final ImageView editUserImage = null;
    CardView saveButton;
    TextView saveButtonText;
    private ImageView cameraIcon = null;
    private TextView cameraText, onboardingProfileSubtitle, onboardingProfileTitle;
    private EditText nameEditText;
    private LinearLayout editPhoto;
    private CircleImageView circleImageView;
    private long mGroupId = 0;
    private boolean mLaunchMesibo = false;

    public OnBoardingProfile() {
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

    public MesiboProfile getProfile() {
        if (mGroupId > 0) return Mesibo.getProfile(mGroupId);
        return Mesibo.getSelfProfile();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImagePicker.Companion.with(this).saveDir(getExternalFilesDir(Environment.DIRECTORY_DCIM)).cameraOnly().cropSquare().compress(1024).maxResultSize(1080, 1080).start();
            } else {
                //TBD, show alert that you can't continue
                //AppUtils.showAlert(OnBoardingUserProfile.this, TITLE_PERMISON_CAMERA_FAIL, MSG_PERMISON_CAMERA_FAIL);
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, start contact synchronization
                ContactSyncClass.getMyQampContacts(OnBoardingProfile.this);
             } else {
                // Permissions denied, show a message or handle accordingly
                Toast.makeText(this, "Contacts permission denied.", Toast.LENGTH_SHORT).show();
            }
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
        AppUtils.setStatusBarColor(OnBoardingProfile.this, R.color.colorAccent);
        setContentView(R.layout.activity_on_boarding_profile);
        initViews();
        ContactSyncClass.getMyQampContacts(OnBoardingProfile.this);
        Log.e("mytesting1",""+ContactSyncClass.contacts.size());
        Log.e("mytesting2",""+ContactSyncClass.groupContacts.size());
        for (QampContactScreenModel contact : ContactSyncClass.contacts) {
            System.out.println("MyShivamNameTest: " + contact.getMes_rv_name());
            System.out.println("MyShivamPhoneTest: " + contact.getMes_rv_phone());
            // Print other fields as needed
        }
        // Check and request contacts permission if not granted
        if (checkPermissions()) {
            // Permissions are granted, start contact synchronization
//            List<ContactMesiboSync.DeviceContacts> contacts = ContactMesiboSync.fetchContacts(OnBoardingProfile.this);
//            List<String> phoneNumbers = ContactMesiboSync.getPhoneNumbers(contacts);
//            String[] phoneNumbersArray = phoneNumbers.toArray(new String[0]);
//            Mesibo.syncContacts(phoneNumbersArray,true,true,0,true);
        } else {
            // Request permissions
            requestPermissions();
        }
        //MesiboInit.initialiseMesibo(OnBoardingProfile.this,false);
         Log.e("androidToken", AppConfig.getConfig().deviceToken);
        for (MesiboProfile phoneNumber : Mesibo.getSortedUserProfiles()) {
            Log.d("MyTag", phoneNumber.address); // Replace "MyTag" with your desired tag
            Log.d("MyTag," + phoneNumber.address, phoneNumber.getName()); // Replace "MyTag" with your desired tag
        }
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
                    //QampUiHelper.launchImageViewer(OnBoardingUserProfile.this, getProfile().getImagePath());
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
                    //saveProfile();
                    Intent intent = new Intent(OnBoardingProfile.this, WelcomeOnBoarding.class);
                    startActivity(intent);
                    AppUtils.saveStringValue(OnBoardingProfile.this, "isOnBoardingProfileSaved", "true");
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
                if (mLaunchMesibo) {
                    //QampUiHelper.launchMesibo(OnBoardingUserProfile.this, 0, false, true);
                }
                // finish();
            }
        });
        setUserPicture();
        updateUI(profile);
    }


    private void requestPermissions() {
        // Request contacts permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_REQUEST_CODE);
    }

    private void updateUI(MesiboProfile profile) {
        if (!profile.isGroup()) {
            nameEditText.setText(profile.getName());
        }
        if (!TextUtils.isEmpty(profile.getName())) nameEditText.setText(profile.getName());
        nameEditText.setText(profile.getName());
    }

    private boolean checkPermissions() {
        // Check if all required permissions are granted
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void textFieldListeners() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                if (!nameEditText.getText().toString().equals("")) {
                    final int sdk = Build.VERSION.SDK_INT;
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        saveButton.setBackgroundDrawable(ContextCompat.getDrawable(OnBoardingProfile.this, R.drawable.corner_radius_button));
                        saveButtonText.setTextColor(ContextCompat.getColor(OnBoardingProfile.this, R.color.text_color_black));
                    } else {
                        saveButton.setBackground(ContextCompat.getDrawable(OnBoardingProfile.this, R.drawable.corner_radius_button));
                        saveButtonText.setTextColor(ContextCompat.getColor(OnBoardingProfile.this, R.color.text_color_black));
                    }
                } else {
                    final int sdk = Build.VERSION.SDK_INT;
                    //saveButton.setCompoundDrawables(null, null, null, null);
                    if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                        saveButton.setBackgroundDrawable(ContextCompat.getDrawable(OnBoardingProfile.this, R.drawable.corner_radius_gray_button));
                        saveButtonText.setTextColor(ContextCompat.getColor(OnBoardingProfile.this, R.color.text_color_button));
                    } else {
                        saveButton.setBackground(ContextCompat.getDrawable(OnBoardingProfile.this, R.drawable.corner_radius_gray_button));
                        saveButtonText.setTextColor(ContextCompat.getColor(OnBoardingProfile.this, R.color.text_color_button));
                    }
                }
            }
        });
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
        //saveProfilePhoto(uri);
        MesiboProfile profile = getProfile();
        if (resultCode != RESULT_CANCELED) {
            if (uri != null) {
                profile.setImage(decodeUriAsBitmap(OnBoardingProfile.this, uri));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circleImageView.setImageDrawable(getDrawable(R.drawable.rounded_profile_view));
        }
        MesiboProfile profile = getProfile();
        profile.setImage(null);
        profile.save();
    }


    private void initViews() {
        saveButton = findViewById(R.id.saveButton);

        cameraText = findViewById(R.id.cameraText);
        onboardingProfileSubtitle = findViewById(R.id.onboardingProfileSubtitle);
        onboardingProfileTitle = findViewById(R.id.onboardingProfileTitle);
        nameEditText = findViewById(R.id.nameEditText);
        circleImageView = findViewById(R.id.circleImageView);
        editPhoto = findViewById(R.id.editPhoto);
        cameraIcon = findViewById(R.id.cameraIcon);
        cameraText = findViewById(R.id.cameraText);
        textFieldListeners();
        editPhoto.setVisibility(View.GONE);
        cameraText.setVisibility(View.VISIBLE);
        cameraIcon.setVisibility(View.VISIBLE);
        saveButtonText = findViewById(R.id.saveButtonText);
    }

    @Override
    public void onPause() {
        super.onPause();
        MesiboProfile profile = getProfile();
        profile.removeListener(this);
    }

    @Override
    public void Mesibo_onConnectionStatus(int i) {

    }

    @Override
    public void MesiboProfile_onUpdate(MesiboProfile mesiboProfile) {

    }

    @Override
    public void MesiboProfile_onEndToEndEncryption(MesiboProfile mesiboProfile, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
