// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.widget.Toast;
import java.util.concurrent.TimeUnit;
import java.util.Locale;
import android.text.Editable;
import android.text.TextWatcher;
import android.graphics.drawable.GradientDrawable;
import android.text.method.TransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.InputMethodManager;
import android.text.InputFilter;
import android.widget.LinearLayout;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.content.Context;

import org.mesibo.messenger.R;

public class OtpView
{
    Context mContext;
    OtpViewConfig mConfig;
    PopupWindow mPopup;
    OtpViewListener mOtpViewListener;
    EditText mOtpEditText;
    TextView mGetOtpByText;
    private CountDownTimer mTimer;
    
    OtpView(final Context context, final OtpViewConfig config, final OtpViewListener otpViewListener) {
        this.mGetOtpByText = null;
        this.mTimer = null;
        this.mContext = context;
        this.mConfig = config;
        this.mOtpViewListener = otpViewListener;
    }
    
    public void showPopup(final View parent) {
        final LayoutInflater inflater = (LayoutInflater)this.mContext.getSystemService("layout_inflater");
        final View popUpView = inflater.inflate(R.layout.otp_view_layout, (ViewGroup)null, false);
        (this.mPopup = new PopupWindow(popUpView, -2, -2, true)).setContentView(popUpView);
        this.mPopup.setFocusable(true);
        this.mPopup.update();
        this.mPopup.showAtLocation(parent, 17, 0, 0);
        this.mPopup.setInputMethodMode(1);
        final View container = this.mPopup.getContentView().getRootView();
        final WindowManager wm = (WindowManager)this.mContext.getSystemService("window");
        final WindowManager.LayoutParams p = (WindowManager.LayoutParams)container.getLayoutParams();
        p.flags = 2;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, (ViewGroup.LayoutParams)p);
        this.intializePopup(popUpView);
    }
    
    private void intializePopup(final View view) {
        final TextView title = (TextView)view.findViewById(R.id.vT_ovl_Title);
        final TextView Message = (TextView)view.findViewById(R.id.vT_ovl_message);
        final TextView resendOtp = (TextView)view.findViewById(R.id.vT_ovl_resendOtp);
        this.mGetOtpByText = (TextView)view.findViewById(R.id.vT_ovl_textview);
        final TextView verifyText = (TextView)view.findViewById(R.id.vB_ovl_verify);
        final TextView canceltext = (TextView)view.findViewById(R.id.vB_cancel);
        this.mOtpEditText = (EditText)view.findViewById(R.id.vE_ovl_enterOtp);
        final LinearLayout layoutbelow = (LinearLayout)view.findViewById(R.id.vL_ovl_belowbuttonLay);
        final TextView otpTimer = (TextView)view.findViewById(R.id.vT_ovl_timer);
        final View lineview1 = view.findViewById(R.id.vV_view);
        final View lineview2 = view.findViewById(R.id.vV_view2);
        title.setText((CharSequence)this.mConfig.mOtpTitle);
        title.setTextColor(this.mConfig.mTitleColor);
        Message.setText((CharSequence)(this.mConfig.mOtpMessage1 + this.mConfig.mPhone + this.mConfig.mOtpMessage2));
        Message.setTextColor(this.mConfig.mTextColor);
        resendOtp.setTextColor(this.mConfig.mButtonColor);
        this.mGetOtpByText.setText((CharSequence)this.mConfig.mGetOtpMessage);
        this.mGetOtpByText.setTextColor(this.mConfig.mSecondaryColor);
        verifyText.setText((CharSequence)this.mConfig.mVerify);
        verifyText.setTextColor(this.mConfig.mButtonTextColor);
        verifyText.setAlpha(0.5f);
        canceltext.setText((CharSequence)this.mConfig.mCancel);
        canceltext.setTextColor(this.mConfig.mButtonTextColor);
        lineview1.setBackgroundColor(this.mConfig.mSecondaryColor);
        lineview2.setBackgroundColor(this.mConfig.mButtonTextColor);
        this.mOtpEditText.setHint((CharSequence)this.mConfig.mOtpHint);
        final InputFilter[] fArray = { (InputFilter)new InputFilter.LengthFilter(this.mConfig.mMaxLength) };
        this.mOtpEditText.setFilters(fArray);
        this.mOtpEditText.setOnFocusChangeListener((View.OnFocusChangeListener)new View.OnFocusChangeListener() {
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    final InputMethodManager inputMgr = (InputMethodManager)OtpView.this.mContext.getSystemService("input_method");
                    inputMgr.toggleSoftInput(2, 0);
                    inputMgr.showSoftInput(v, 1);
                }
            }
        });
        this.mOtpEditText.requestFocus();
        if (this.mConfig.IsPassword) {
            this.mOtpEditText.setInputType(130);
            this.mOtpEditText.setTransformationMethod((TransformationMethod)PasswordTransformationMethod.getInstance());
        }
        else {
            this.mOtpEditText.setInputType(2);
        }
        if (this.mConfig.mIsForOtp) {
            resendOtp.setText((CharSequence)this.mConfig.mResendOtp);
        }
        else {
            resendOtp.setText((CharSequence)this.mConfig.mForGotPassword);
        }
        final GradientDrawable drawable = (GradientDrawable)layoutbelow.getBackground();
        drawable.setColor(this.mConfig.mButtonColor);
        this.mOtpEditText.addTextChangedListener((TextWatcher)new TextWatcher() {
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }
            
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }
            
            public void afterTextChanged(final Editable s) {
                if (null != s) {
                    if (s.length() != 0) {
                        if (s.length() >= OtpView.this.mConfig.mOtpLength) {
                            if (OtpView.this.mConfig.mAutoSubmit) {
                                OtpView.this.submitAndClose();
                                return;
                            }
                            verifyText.setEnabled(true);
                            verifyText.setAlpha(1.0f);
                        }
                        else {
                            verifyText.setEnabled(false);
                            verifyText.setAlpha(0.5f);
                        }
                    }
                }
            }
        });
        if (this.mConfig.mCallTimeoutInMiliSeconds > 0) {
            this.mTimer = new CountDownTimer((long)this.mConfig.mCallTimeoutInMiliSeconds, 1000L) {
                public void onTick(final long millisUntilFinished) {
                    final String text = String.format(Locale.getDefault(), " %02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60L, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60L);
                    otpTimer.setText((CharSequence)text);
                }
                
                public void onFinish() {
                    OtpView.this.mGetOtpByText.setText((CharSequence)OtpView.this.mConfig.mTimerFinished);
                    otpTimer.setText((CharSequence)"");
                }
            }.start();
        }
        else {
            this.mGetOtpByText.setVisibility(8);
            otpTimer.setVisibility(8);
        }
        if (this.mConfig.mResendTimeoutInMiliSeconds >= 0) {
            resendOtp.setEnabled(false);
            resendOtp.setAlpha(0.5f);
            new CountDownTimer((long)this.mConfig.mResendTimeoutInMiliSeconds, 1000L) {
                public void onTick(final long millisUntilFinished) {
                }
                
                public void onFinish() {
                    resendOtp.setEnabled(true);
                    resendOtp.setAlpha(1.0f);
                }
            }.start();
        }
        verifyText.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                String otpText = "";
                otpText = OtpView.this.mOtpEditText.getText() + "";
                if (null == otpText || 0 == otpText.length()) {
                    Toast.makeText(OtpView.this.mContext, (CharSequence)OtpView.this.mConfig.mPlaseEnterOtpMessage, 0).show();
                }
                else {
                    OtpView.this.submitAndClose();
                }
            }
        });
        canceltext.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                OtpView.this.cancelTimer();
                OtpView.this.mPopup.dismiss();
                OtpView.this.mOtpViewListener.OtpView_onOtp(null);
            }
        });
        resendOtp.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                OtpView.this.cancelTimer();
                OtpView.this.mPopup.dismiss();
                OtpView.this.mOtpViewListener.OtpView_onResend();
            }
        });
        this.mPopup.setOnDismissListener((PopupWindow.OnDismissListener)new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                OtpView.this.cancelTimer();
            }
        });
    }
    
    private void cancelTimer() {
        if (null != this.mTimer) {
            this.mTimer.cancel();
        }
        this.mTimer = null;
    }
    
    private void submitAndClose() {
        this.cancelTimer();
        this.mPopup.dismiss();
        this.mOtpViewListener.OtpView_onOtp(this.mOtpEditText.getText() + "");
    }
    
    private void getSoftkeyBoard() {
        final InputMethodManager imm = (InputMethodManager)this.mContext.getSystemService("input_method");
        imm.showSoftInput((View)this.mOtpEditText, 1);
    }
    
    public static class OtpViewConfig
    {
        public String mOtpTitle;
        public String mPhone;
        public String mOtpMessage1;
        public String mOtpMessage2;
        public String mOtpHint;
        public String mResendOtp;
        public String mGetOtpMessage;
        public String mVerify;
        public String mCancel;
        public String mPlaseEnterOtpMessage;
        public String mTimerFinished;
        public String mForGotPassword;
        public int mButtonTextColor;
        public int mTitleColor;
        public int mTextColor;
        public int mSecondaryColor;
        public int mButtonColor;
        public int mCallTimeoutInMiliSeconds;
        public int mResendTimeoutInMiliSeconds;
        public int mOtpLength;
        public int mMaxLength;
        public boolean IsPassword;
        public boolean mIsForOtp;
        public boolean mAutoSubmit;
        
        public OtpViewConfig() {
            this.mOtpTitle = "Enter OTP";
            this.mPhone = "";
            this.mOtpMessage1 = "Please enter OTP for ";
            this.mOtpMessage2 = "";
            this.mOtpHint = "Enter OTP";
            this.mResendOtp = " Restart";
            this.mGetOtpMessage = "session expires in";
            this.mVerify = "Verify";
            this.mCancel = "Cancel";
            this.mPlaseEnterOtpMessage = "Please enter OTP";
            this.mTimerFinished = "Session Expired - start again!";
            this.mForGotPassword = "Forgot Password";
            this.mButtonTextColor = -1;
            this.mTitleColor = -16777216;
            this.mTextColor = -12303292;
            this.mSecondaryColor = -7829368;
            this.mButtonColor = -16742773;
            this.mCallTimeoutInMiliSeconds = 600000;
            this.mResendTimeoutInMiliSeconds = 6000;
            this.mOtpLength = 6;
            this.mMaxLength = 6;
            this.IsPassword = false;
            this.mIsForOtp = true;
            this.mAutoSubmit = true;
        }
    }
    
    interface OtpViewListener
    {
        void OtpView_onOtp(final String p0);
        
        void OtpView_onResend();
    }
}
