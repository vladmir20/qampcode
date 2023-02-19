// 
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.view.MotionEvent;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import android.widget.RelativeLayout;
import android.text.method.LinkMovementMethod;
import android.content.Intent;
import android.text.Html;
import android.net.Uri;
import android.view.ViewTreeObserver;
import android.content.Context;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.List;
import com.qamp.app.uihelper.Utils.OverLayFrameLayout;
import com.qamp.app.uihelper.Utils.TouchInterceptLayout;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import androidx.fragment.app.Fragment;

import com.qamp.app.R;

public class WelcomeFragment extends Fragment implements View.OnTouchListener
{
    ViewPager mPictureViewPager;
    ViewPager mBannerViewPager;
    TouchInterceptLayout mTouchLayout;
    OverLayFrameLayout mMobilePngView;
    List<WelcomeScreen> mDataList;
    private LinearLayout mPageIndicator;
    private int mPageDotCount;
    private ImageView[] mPageDotView;
    private float imagePagerWidth;
    private float mBannerViewPagerWidth;
    private int mScaleX;
    private int mReverseScale;
    TextView mInfoText;
    TextView mBottomTextLong;
    ImageButton mClosePaneBtn;
    Button mSignInBtn;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    MesiboUiHelperConfig mConfig;
    
    public WelcomeFragment() {
        this.imagePagerWidth = 1.0f;
        this.mBannerViewPagerWidth = 0.0f;
        this.mScaleX = 1;
        this.mReverseScale = 1;
        this.mConfig = MesiboUiHelper.getConfig();
    }
    
    public static WelcomeFragment newInstance(final String param1, final String param2) {
        final WelcomeFragment fragment = new WelcomeFragment();
        final Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getArguments() != null) {
            this.mParam1 = this.getArguments().getString("param1");
            this.mParam2 = this.getArguments().getString("param2");
        }
    }
    
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.welcome_launch_fragment, container, false);
        final MesiboUiHelperConfig config = MesiboUiHelper.getConfig();
        if (null == config) {
            return v;
        }
        this.mDataList = MesiboUiHelperConfig.mScreens;
        this.mPictureViewPager = (ViewPager)v.findViewById(R.id.viewpager_picture);
        final PagerAdapter pagerAdapter = new WelcomePictureViewPagerAdapter((Context)this.getActivity(), this.mDataList);
        this.mPictureViewPager.setAdapter(pagerAdapter);
        this.mPictureViewPager.setPageMargin(0);
        final ViewTreeObserver vto = this.mPictureViewPager.getViewTreeObserver();
        vto.addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                WelcomeFragment.this.mPictureViewPager.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)this);
                WelcomeFragment.this.imagePagerWidth = (float)WelcomeFragment.this.mPictureViewPager.getMeasuredWidth();
            }
        });
        (this.mMobilePngView = (OverLayFrameLayout)v.findViewById(R.id.overlay_frame)).requestLayout();
        final ViewTreeObserver vto2 = this.mMobilePngView.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                WelcomeFragment.this.mMobilePngView.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)this);
                final ViewGroup.LayoutParams layoutParams = WelcomeFragment.this.mMobilePngView.getLayoutParams();
                layoutParams.width = (int)(0.66 * WelcomeFragment.this.mMobilePngView.getMeasuredHeight());
                layoutParams.height = WelcomeFragment.this.mMobilePngView.getMeasuredHeight();
                WelcomeFragment.this.mMobilePngView.setLayoutParams(layoutParams);
            }
        });
        this.mTouchLayout = (TouchInterceptLayout)v.findViewById(R.id.main_relative_layout);
        this.mSignInBtn = (Button)v.findViewById(R.id.signin);
        final Button mSignInBtn = this.mSignInBtn;
        final MesiboUiHelperConfig mConfig = this.mConfig;
        mSignInBtn.setBackgroundColor(MesiboUiHelperConfig.mWelcomeBackgroundColor);
        final Button mSignInBtn2 = this.mSignInBtn;
        final MesiboUiHelperConfig mConfig2 = this.mConfig;
        mSignInBtn2.setTextColor(MesiboUiHelperConfig.mWelcomeTextColor);
        final Button mSignInBtn3 = this.mSignInBtn;
        final MesiboUiHelperConfig mConfig3 = this.mConfig;
        mSignInBtn3.setText((CharSequence)MesiboUiHelperConfig.mWelcomeButtonName);
        this.mSignInBtn.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                WelcomeFragment.this.mListener.onFragmentInteraction(null);
            }
        });
        this.mTouchLayout.setOnTouchListener((View.OnTouchListener)this);
        final View view = v;
        final MesiboUiHelperConfig mConfig4 = this.mConfig;
        view.setBackgroundColor(MesiboUiHelperConfig.mWelcomeBackgroundColor);
        this.mBottomTextLong = (TextView)v.findViewById(R.id.bottom_textview);
        final TextView mBottomTextLong = this.mBottomTextLong;
        final MesiboUiHelperConfig mConfig5 = this.mConfig;
        mBottomTextLong.setText((CharSequence)MesiboUiHelperConfig.mWelcomeBottomTextLong);
        this.mClosePaneBtn = (ImageButton)v.findViewById(R.id.close_pane);
        final TextView textView;
        final TextView termsView = textView = (TextView)v.findViewById(R.id.terms);
        final MesiboUiHelperConfig mConfig6 = this.mConfig;
        textView.setText((CharSequence)Html.fromHtml(MesiboUiHelperConfig.mWelcomeTermsText));
        termsView.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                final String s = "android.intent.action.VIEW";
                final MesiboUiHelperConfig mConfig = WelcomeFragment.this.mConfig;
                final Intent in = new Intent(s, Uri.parse(MesiboUiHelperConfig.mTermsUrl));
                WelcomeFragment.this.startActivity(in);
            }
        });
        termsView.setMovementMethod(LinkMovementMethod.getInstance());
        this.mInfoText = (TextView)v.findViewById(R.id.info_text);
        final TextView mInfoText = this.mInfoText;
        final MesiboUiHelperConfig mConfig7 = this.mConfig;
        mInfoText.setText((CharSequence)MesiboUiHelperConfig.mWelcomeBottomText);
        this.mInfoText.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                WelcomeFragment.this.mBottomTextLong.setVisibility(0);
                WelcomeFragment.this.mClosePaneBtn.setVisibility(0);
            }
        });
        this.mClosePaneBtn.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                WelcomeFragment.this.mBottomTextLong.setVisibility(8);
                WelcomeFragment.this.mClosePaneBtn.setVisibility(8);
            }
        });
        final RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.main_relative_layout);
        rl.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
            public void onClick(final View v) {
                WelcomeFragment.this.mBottomTextLong.setVisibility(8);
                WelcomeFragment.this.mClosePaneBtn.setVisibility(8);
            }
        });
        this.mBannerViewPager = (ViewPager)v.findViewById(R.id.viewpager_text);
        final FragmentActivity activity = this.getActivity();
        final List<WelcomeScreen> mDataList = this.mDataList;
        final MesiboUiHelperConfig mConfig8 = this.mConfig;
        final PagerAdapter pagerAdapterT = new WelcomeBannerViewPagerAdapter((Context)activity, mDataList, MesiboUiHelperConfig.mWelcomeTextColor);
        this.mBannerViewPager.setAdapter(pagerAdapterT);
        this.mBannerViewPager.setPageMargin(0);
        final ViewTreeObserver vto3 = this.mBannerViewPager.getViewTreeObserver();
        vto3.addOnGlobalLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                WelcomeFragment.this.mBannerViewPager.getViewTreeObserver().removeGlobalOnLayoutListener((ViewTreeObserver.OnGlobalLayoutListener)this);
                WelcomeFragment.this.mBannerViewPagerWidth = (float)WelcomeFragment.this.mBannerViewPager.getMeasuredWidth();
            }
        });
        this.mPageDotCount = pagerAdapter.getCount();
        this.mPageDotView = new ImageView[this.mPageDotCount];
        this.mPageIndicator = (LinearLayout)v.findViewById(R.id.viewPagerIndicator);
        for (int i = 0; i < this.mPageDotCount; ++i) {
            (this.mPageDotView[i] = new ImageView((Context)this.getActivity())).setImageDrawable(this.getResources().getDrawable(R.drawable.unselected_dot));
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.setMargins(4, 0, 4, 0);
            this.mPageIndicator.addView((View)this.mPageDotView[i], (ViewGroup.LayoutParams)params);
        }
        this.mPageDotView[0].setImageDrawable(this.getResources().getDrawable(R.drawable.selected_dot));
        this.mBannerViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener)new ViewPager.OnPageChangeListener() {
            private int mScrollState = 0;
            
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                WelcomeFragment.this.mBottomTextLong.setVisibility(8);
                WelcomeFragment.this.mClosePaneBtn.setVisibility(8);
                if (this.mScrollState == 0) {
                    return;
                }
                final float scaleXX = WelcomeFragment.this.imagePagerWidth / WelcomeFragment.this.mBannerViewPagerWidth;
                final double dummy = scaleXX * WelcomeFragment.this.mBannerViewPager.getScrollX();
                final int scrolled = (int)Math.round(dummy);
                WelcomeFragment.this.mPictureViewPager.scrollTo(scrolled, WelcomeFragment.this.mPictureViewPager.getScrollY());
            }
            
            public void onPageSelected(final int position) {
            }
            
            public void onPageScrollStateChanged(final int state) {
                this.mScrollState = state;
                if (state == 0) {
                    WelcomeFragment.this.mPictureViewPager.setCurrentItem(WelcomeFragment.this.mBannerViewPager.getCurrentItem(), false);
                }
            }
        });
        this.mPictureViewPager.addOnPageChangeListener((ViewPager.OnPageChangeListener)new ViewPager.OnPageChangeListener() {
            private int mScrollState = 0;
            
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
                if (this.mScrollState == 0) {
                    return;
                }
                final float scaleXX = WelcomeFragment.this.mBannerViewPagerWidth / WelcomeFragment.this.imagePagerWidth;
                final double dummy = scaleXX * WelcomeFragment.this.mPictureViewPager.getScrollX();
                final int scrolled = (int)Math.round(dummy);
                WelcomeFragment.this.mBannerViewPager.scrollTo(scrolled, WelcomeFragment.this.mBannerViewPager.getScrollY());
            }
            
            public void onPageSelected(final int position) {
                for (int i = 0; i < WelcomeFragment.this.mPageDotCount; ++i) {
                    WelcomeFragment.this.mPageDotView[i].setImageDrawable(WelcomeFragment.this.getResources().getDrawable(R.drawable.unselected_dot));
                }
                WelcomeFragment.this.mPageDotView[position].setImageDrawable(WelcomeFragment.this.getResources().getDrawable(R.drawable.selected_dot));
            }
            
            public void onPageScrollStateChanged(final int state) {
                this.mScrollState = state;
                if (state == 0) {
                    WelcomeFragment.this.mBannerViewPager.setCurrentItem(WelcomeFragment.this.mPictureViewPager.getCurrentItem(), false);
                }
            }
        });
        return v;
    }
    
    public void onButtonPressed(final Uri uri) {
        if (this.mListener != null) {
            this.mListener.onFragmentInteraction(uri);
        }
    }
    
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            this.mListener = (OnFragmentInteractionListener)context;
            return;
        }
        throw new RuntimeException(context.toString() + " must implement OnLoginInteactionListener");
    }
    
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }
    
    public boolean onTouch(final View view, final MotionEvent event) {
        this.mBannerViewPager.onTouchEvent(event);
        return false;
    }
    
    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(final Uri p0);
    }
}
