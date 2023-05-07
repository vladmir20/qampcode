/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app.messaging;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.mesibo.api.Mesibo;
import com.mesibo.api.MesiboProfile;
import com.mesibo.mediapicker.MediaPicker;
import com.qamp.app.R;

import java.io.File;

public class MesiboEndToEndEncryptionFragment extends BaseFragment implements View.OnClickListener, MessagingActivityListener {
    private boolean mLocalPrivate = false;
    private boolean mOptions = false;
    private View mView = null;
    /* access modifiers changed from: private */
    public MesiboProfile profile = null;

    public void setProfile(MesiboProfile p) {
        this.profile = p;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void replaceFirstName(int id) {
        TextView t = (TextView) this.mView.findViewById(id);
        t.setText(t.getText().toString().replaceAll("FirstName", this.profile.getFirstNameOrAddress("+")));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_e2e_info, container, false);
        if (this.mView == null) {
            return this.mView;
        }
        if (this.profile.isSelfProfile()) {
            this.mView.findViewById(R.id.e2einfo).setVisibility(8);
            this.mView.findViewById(R.id.e2eself).setVisibility(0);
            this.mView.findViewById(R.id.e2eselfexportbutton).setOnClickListener(this);
            this.mView.findViewById(R.id.e2eselfloadbutton).setOnClickListener(this);
            return this.mView;
        }
        this.mView.findViewById(R.id.e2eopt).setVisibility(8);
        if (!this.profile.e2ee().isActive()) {
            replaceFirstName(R.id.e2einactivetext);
            this.mView.findViewById(R.id.e2einfo).setVisibility(8);
            this.mView.findViewById(R.id.e2einactive).setVisibility(0);
            return this.mView;
        }
        replaceFirstName(R.id.e2etexttop);
        replaceFirstName(R.id.e2etextbottom);
        replaceFirstName(R.id.e2etextlast);
        replaceFirstName(R.id.e2etextlast);
        replaceFirstName(R.id.e2eopttext);
        replaceFirstName(R.id.e2epasstext);
        replaceFirstName(R.id.e2ecerttext);
        this.mView.findViewById(R.id.e2eoptbutton).setOnClickListener(this);
        this.mView.findViewById(R.id.e2epassbutton).setOnClickListener(this);
        this.mView.findViewById(R.id.e2eresetbutton).setOnClickListener(this);
        this.mView.findViewById(R.id.e2ecertbutton).setOnClickListener(this);
        String fp = this.profile.e2ee().getFingerprint();
        int length = fp.length();
        String fp_top = Mesibo.e2ee().getFingerprintPart(fp, 0);
        String fp_bottom = Mesibo.e2ee().getFingerprintPart(fp, 1);
        ((TextView) this.mView.findViewById(R.id.e2e_fp_top)).setText(fp_top);
        ((TextView) this.mView.findViewById(R.id.e2e_fp_bottom)).setText(fp_bottom);
        return this.mView;
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.e2eoptbutton) {
            this.mView.findViewById(R.id.e2einfo).setVisibility(8);
            this.mView.findViewById(R.id.e2eopt).setVisibility(0);
            this.mOptions = true;
        } else if (id == R.id.e2epassbutton) {
            setPassword();
        } else if (id == R.id.e2eresetbutton) {
            reset();
        } else if (id == R.id.e2ecertbutton) {
            this.mLocalPrivate = false;
            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILE);
        } else if (id == R.id.e2eselfexportbutton) {
            exportCertificate(getActivity());
        } else if (id == R.id.e2eselfloadbutton) {
            this.mLocalPrivate = true;
            MediaPicker.launchPicker(getActivity(), MediaPicker.TYPE_FILE);
        }
    }

    private void reset() {
        Utils.showAlert(getActivity(), "Reset E2EE", "Do you want to reset E2EE options set for " + this.profile.getFirstNameOrAddress("+") + "?", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case -1:
                        MesiboEndToEndEncryptionFragment.this.profile.e2ee().reset();
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    private void setPassword() {
        final TextView t = (TextView) this.mView.findViewById(R.id.e2epasstext);
        final String pass = t.getText().toString().trim();
        if (pass.length() < 6) {
            Utils.showAlert(getActivity(), "Password is too short", "Enter minimum 6-chars password");
            return;
        }
        Utils.showAlert(getActivity(), "Set Password", "You need to ensure that " + this.profile.getFirstNameOrAddress("+") + " also uses the same password.", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case -1:
                        if (MesiboEndToEndEncryptionFragment.this.profile.e2ee().setPassword(pass)) {
                            Utils.showAlert(MesiboEndToEndEncryptionFragment.this.getActivity(), "Set Password", "Password set successfully");
                            t.setText("");
                            break;
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void exportCertificate(Activity context) {
        String filePath = Mesibo.e2ee().getPublicCertificate();
        if (TextUtils.isEmpty(filePath)) {
            Utils.showAlert(getActivity(), "Error", "Unable to export public certificate");
            return;
        }
        Uri uri = FileProvider.getUriForFile(context, MediaPicker.getAuthority(context), new File(filePath));
        Intent sharingIntent = new Intent("android.intent.action.SEND");
        sharingIntent.setType("application/x-pem-file");
        sharingIntent.putExtra("android.intent.extra.STREAM", uri);
        sharingIntent.putExtra("android.intent.extra.SUBJECT", "My Public Certificate");
        sharingIntent.putExtra("android.intent.extra.TEXT", "Public Certificate");
        sharingIntent.addFlags(1);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Public Certificate"));
    }

    private void loadPublic(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        if (!ext.equalsIgnoreCase("pub") && !ext.equalsIgnoreCase("cer") && !ext.equalsIgnoreCase("crt") && !ext.equalsIgnoreCase("pem")) {
            Utils.showAlert(getActivity(), "Incorrect File", "Select a valid certificate file (.pub)");
        } else if (this.profile.e2ee().setPeerCertificate(filePath)) {
            Utils.showAlert(getActivity(), "Successful", "Certificate is set and active");
        } else {
            Utils.showAlert(getActivity(), "Invalid Certificate File", "Unable to set certificate");
        }
    }

    private void loadPrivate(String filePath) {
        String ext = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
        if (!ext.equalsIgnoreCase("p12") && !ext.equalsIgnoreCase("pfx")) {
            Utils.showAlert(getActivity(), "Incorrect File", "Select a valid private certificate file (.p12/.pfx)");
        } else if (Mesibo.e2ee().setPrivateCertificate(filePath)) {
            Utils.showAlert(getActivity(), "Successful", "Certificate is set and active");
        } else {
            Utils.showAlert(getActivity(), "Invalid Certificate File", "Unable to private set certificate");
        }
    }

    public boolean Mesibo_onActivityResult(int requestCode, int resultCode, Intent data) {
        String filePath;
        if (-1 == resultCode && (filePath = MediaPicker.processOnActivityResult(myActivity(), requestCode, resultCode, data)) != null && MediaPicker.TYPE_FILE == requestCode) {
            if (this.mLocalPrivate) {
                loadPrivate(filePath);
            } else {
                loadPublic(filePath);
            }
        }
        return true;
    }

    public void Mesibo_onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    public int Mesibo_onGetEnabledActionItems() {
        return 0;
    }

    public boolean Mesibo_onActionItemClicked(int item) {
        return false;
    }

    public void Mesibo_onInContextUserInterfaceClosed() {
    }

    public boolean Mesibo_onBackPressed() {
        if (!this.mOptions) {
            return false;
        }
        this.mView.findViewById(R.id.e2einfo).setVisibility(0);
        this.mView.findViewById(R.id.e2eopt).setVisibility(8);
        this.mOptions = false;
        return true;
    }
}
