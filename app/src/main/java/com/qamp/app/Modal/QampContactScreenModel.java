package com.qamp.app.Modal;

import android.graphics.Bitmap;

public class QampContactScreenModel {
    private boolean isChecked;
    private String mes_rv_name;
    private String mes_rv_phone;
    private boolean isMesiboProfile;

    private Bitmap mUserImage = null;


    public boolean isChecked() {
        return isChecked;
    }

    public QampContactScreenModel(String mes_rv_name, String mes_rv_phone, boolean isMesiboProfile, Bitmap mUserImage) {
        this.isChecked = false;
        this.mes_rv_name = mes_rv_name;
        this.mes_rv_phone = mes_rv_phone;
        this.isMesiboProfile = isMesiboProfile;
         this.mUserImage = mUserImage;
    }


    public Bitmap getmUserImage() {
        return mUserImage;
    }

    public void setmUserImage(Bitmap mUserImage) {
        this.mUserImage = mUserImage;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getMes_rv_name() {
        return mes_rv_name;
    }

    public void setMes_rv_name(String mes_rv_name) {
        this.mes_rv_name = mes_rv_name;
    }

    public String getMes_rv_phone() {
        return mes_rv_phone;
    }

    public void setMes_rv_phone(String mes_rv_phone) {
        this.mes_rv_phone = mes_rv_phone;
    }

    public boolean isMesiboProfile() {
        return isMesiboProfile;
    }

    public void setMesiboProfile(boolean mesiboProfile) {
        isMesiboProfile = mesiboProfile;
    }

}
