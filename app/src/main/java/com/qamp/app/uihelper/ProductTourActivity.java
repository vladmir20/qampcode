/*
 * *
 *  * Created by Shivam Tiwari on 21/04/23, 3:40 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/04/23, 8:31 PM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.uihelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qamp.app.R;
import com.qamp.app.uihelper.Utils.Utils;

import java.util.List;

public class ProductTourActivity extends AppCompatActivity {
    static int NUM_PAGES;

    static {
        ProductTourActivity.NUM_PAGES = 1;
    }

    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button skip;
    Button done;
    ImageButton next;
    boolean isOpaque;
    List<WelcomeScreen> mScreenList;
    MesiboUiHelperConfig mConfig;
    private boolean mPermissionAlert;

    public ProductTourActivity() {
        this.isOpaque = true;
        this.mScreenList = null;
        this.mConfig = null;
        this.mPermissionAlert = false;
    }

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mConfig = MesiboUiHelper.getConfig();
        if (null == this.mConfig) {
            return;
        }
        final MesiboUiHelperConfig mConfig = this.mConfig;
        this.mScreenList = MesiboUiHelperConfig.mScreens;
        ProductTourActivity.NUM_PAGES = this.mScreenList.size();
        final Window window = this.getWindow();
        window.setFlags(67108864, 67108864);
        this.setContentView(R.layout.activity_tutorial);
        (this.skip = Button.class.cast(this.findViewById(R.id.skip))).setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View v) {
                ProductTourActivity.this.endTutorial();
            }
        });
        (this.next = ImageButton.class.cast(this.findViewById(R.id.next))).setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View v) {
                if (ProductTourActivity.this.pager.getAdapter() == null) {
                    ProductTourActivity.this.pager.setAdapter(ProductTourActivity.this.pagerAdapter);
                    ProductTourActivity.this.pager.setCurrentItem(0);
                }
                ProductTourActivity.this.pager.setCurrentItem(ProductTourActivity.this.pager.getCurrentItem() + 1, true);
            }
        });
        (this.done = Button.class.cast(this.findViewById(R.id.done))).setOnClickListener((View.OnClickListener) new View.OnClickListener() {
            public void onClick(final View v) {
                ProductTourActivity.this.endTutorial();
            }
        });
        this.pager = (ViewPager) this.findViewById(R.id.pager);
        this.pagerAdapter = (PagerAdapter) new ScreenSlidePagerAdapter(this.getSupportFragmentManager());
        this.pager.setAdapter(this.pagerAdapter);
        final MesiboUiHelperConfig mConfig2 = this.mConfig;
        if (MesiboUiHelperConfig.mScreenAnimation && Build.VERSION.SDK_INT >= 11) {
            this.pager.setPageTransformer(true, (ViewPager.PageTransformer) new CrossfadePageTransformer());
        }
        this.pager.addOnPageChangeListener((ViewPager.OnPageChangeListener) new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
            }

            public void onPageSelected(final int position) {
                ProductTourActivity.this.setIndicator(position);
                if (position == ProductTourActivity.NUM_PAGES - 2) {
                    ProductTourActivity.this.skip.setVisibility(8);
                    ProductTourActivity.this.next.setVisibility(8);
                    ProductTourActivity.this.done.setVisibility(0);
                } else if (position < ProductTourActivity.NUM_PAGES - 2) {
                    ProductTourActivity.this.skip.setVisibility(0);
                    ProductTourActivity.this.next.setVisibility(0);
                    ProductTourActivity.this.done.setVisibility(8);
                } else if (position == ProductTourActivity.NUM_PAGES - 1) {
                    ProductTourActivity.this.endTutorial();
                }
            }

            public void onPageScrollStateChanged(final int state) {
            }
        });
        this.buildCircles();
    }

    private boolean checkPermissions() {
        final List<String> list = null;
        final MesiboUiHelperConfig mConfig = this.mConfig;
        if (list == MesiboUiHelperConfig.mPermissions) {
            return true;
        }
        final MesiboUiHelperConfig mConfig2 = this.mConfig;
        return Utils.aquireUserPermissions((Context) this, MesiboUiHelperConfig.mPermissions, 1);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.pager != null) {
            this.pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles() {
        this.circles = LinearLayout.class.cast(this.findViewById(R.id.circles));
        final float scale = this.getResources().getDisplayMetrics().density;
        final int padding = (int) (5.0f * scale + 0.5f);
        for (int i = 0; i < ProductTourActivity.NUM_PAGES - 1; ++i) {
            final ImageView circle = new ImageView((Context) this);
            circle.setImageResource(R.drawable.ic_location_on_black_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            this.circles.addView((View) circle);
        }
        this.setIndicator(0);
    }

    private void setIndicator(final int index) {
        if (index < ProductTourActivity.NUM_PAGES) {
            for (int i = 0; i < ProductTourActivity.NUM_PAGES - 1; ++i) {
                final ImageView circle = (ImageView) this.circles.getChildAt(i);
                if (i == index) {
                    circle.setColorFilter(this.getResources().getColor(R.color.black));
                } else {
                    circle.setColorFilter(this.getResources().getColor(17170445));
                }
            }
        }
    }

    private void endTutorial() {
        if (!this.checkPermissions()) {
            return;
        }
        if (MesiboUiHelper.mProductTourListener != null) {
            MesiboUiHelper.mProductTourListener.onProductTourCompleted((Context) this);
        }
        this.finish();
        this.overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    public void onBackPressed() {
        if (this.pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            this.pager.setCurrentItem(this.pager.getCurrentItem() - 1);
        }
    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        int i = 0;
        while (i < grantResults.length) {
            if (grantResults[i] == -1) {
                if (this.mPermissionAlert) {
                    final DialogInterface.OnClickListener handler = (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface var1, final int var2) {
                            ProductTourActivity.this.finish();
                        }
                    };
                    final String title = "Permission Error";
                    final MesiboUiHelperConfig mConfig = this.mConfig;
                    Utils.showAlert((Context) this, title, MesiboUiHelperConfig.mPermissionsDeniedMessage, handler, handler);
                    return;
                }
                final DialogInterface.OnClickListener handler = (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface var1, final int var2) {
                        ProductTourActivity.this.mPermissionAlert = true;
                        ProductTourActivity.this.endTutorial();
                    }
                };
                final String title2 = "Permissions required";
                final MesiboUiHelperConfig mConfig2 = this.mConfig;
                Utils.showAlert((Context) this, title2, MesiboUiHelperConfig.mPermissionsRequestMessage, handler, handler);
                return;
            } else {
                ++i;
            }
        }
        this.endTutorial();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(final int position) {
            final ProductTourFragment tp = ProductTourFragment.newInstance(position);
            return tp;
        }

        public int getCount() {
            return ProductTourActivity.NUM_PAGES;
        }

        public Parcelable saveState() {
            return null;
        }
    }

    public class CrossfadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(final View page, final float position) {
            final int pageWidth = page.getWidth();
            final View backgroundView = page.findViewById(R.id.welcome_fragment);
            final View text_head = page.findViewById(R.id.heading);
            final View text_content = page.findViewById(R.id.content);
            final View welcomeImage01 = page.findViewById(R.id.welcomeImage);
            if (0.0f <= position && position < 1.0f) {
                page.setTranslationX(pageWidth * -position);
            }
            if (-1.0f < position && position < 0.0f) {
                page.setTranslationX(pageWidth * -position);
            }
            if (position > -1.0f) {
                if (position < 1.0f) {
                    if (position != 0.0f) {
                        if (backgroundView != null) {
                            backgroundView.setAlpha(1.0f - Math.abs(position));
                        }
                        if (text_head != null) {
                            text_head.setTranslationX(pageWidth * position);
                            text_head.setAlpha(1.0f - Math.abs(position));
                        }
                        if (text_content != null) {
                            text_content.setTranslationX(pageWidth * position);
                            text_content.setAlpha(1.0f - Math.abs(position));
                        }
                        if (welcomeImage01 != null) {
                            welcomeImage01.setTranslationX(pageWidth / 2 * position);
                            welcomeImage01.setAlpha(1.0f - Math.abs(position));
                        }
                    }
                }
            }
        }

        public void transformPage2(final View view, final float position) {
            view.setTranslationX(view.getWidth() * -position);
            if (position <= -1.0f || position >= 1.0f) {
                view.setAlpha(0.0f);
            } else if (position == 0.0f) {
                view.setAlpha(1.0f);
            } else {
                view.setAlpha(1.0f - Math.abs(position));
            }
        }
    }
}
