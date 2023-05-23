/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.qamp.app.Activity.WelcomeScreen;
import com.qamp.app.R;

import java.util.List;

public class WelcomePictureViewPagerAdapter extends PagerAdapter {
    LayoutInflater _inflater;
    List<WelcomeScreen> list;

    public WelcomePictureViewPagerAdapter(final Context context, final List<WelcomeScreen> list) {
        this._inflater = null;
        this._inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    public Object instantiateItem(final ViewGroup container, final int position) {
        final RelativeLayout layout = (RelativeLayout) this._inflater.inflate(R.layout.welcome_launch_picture_pager_layout, (ViewGroup) null);
        final RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(-1, -1);
        final ImageView pictureView = (ImageView) layout.findViewById(R.id.img);
        final ViewGroup.LayoutParams params = pictureView.getLayoutParams();
        params.width = -1;
        params.height = -1;
        pictureView.setLayoutParams(params);
        pictureView.setImageResource(this.list.get(position).getResourceId());
        container.addView((View) layout);
        return layout;
    }

    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        ((ViewPager) container).removeView((View) object);
    }

    public int getCount() {
        return this.list.size();
    }

    public boolean isViewFromObject(final View view, final Object obj) {
        return view.equals(obj);
    }
}