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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qamp.app.R;

import java.util.List;

public class WelcomeBannerViewPagerAdapter extends PagerAdapter
{
    LayoutInflater _inflater;
    List<WelcomeScreen> list;
    private int mTextColor;
    
    public WelcomeBannerViewPagerAdapter(final Context context, final List<WelcomeScreen> list, final int textColor) {
        this._inflater = null;
        this._inflater = (LayoutInflater)context.getSystemService("layout_inflater");
        this.list = list;
        this.mTextColor = textColor;
    }
    
    public Object instantiateItem(final ViewGroup container, final int position) {
        final RelativeLayout layout = (RelativeLayout)this._inflater.inflate(R.layout.welcome_launch_banner_layout, (ViewGroup)null);
        final TextView nameView = (TextView)layout.findViewById(R.id.banner_text);
        nameView.setText((CharSequence)this.list.get(position).getDescription());
        if (0 != this.mTextColor) {
            nameView.setTextColor(this.mTextColor);
        }
        container.addView((View)layout);
        return layout;
    }
    
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        ((ViewPager)container).removeView((View)object);
    }
    
    public int getCount() {
        return this.list.size();
    }
    
    public boolean isViewFromObject(final View view, final Object obj) {
        return view.equals(obj);
    }
}
