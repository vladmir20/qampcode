/*
 * *
 *  * Created by Shivam Tiwari on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */
package com.qamp.app.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.qamp.app.R;
import com.qamp.app.MessagingModule.BuildConfig;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        View v = inflater.inflate(R.layout.about, container, false);

        TextView tx = (TextView)v.findViewById(R.id.mesibologo);

        Typeface mesiboFont = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/mesibo_regular.otf");

        if(null != mesiboFont)
            tx.setTypeface(mesiboFont);

        TextView version = (TextView)v.findViewById(R.id.version);
        TextView buildDate = (TextView)v.findViewById(R.id.builddate);

        version.setText("Version: " + BuildConfig.BUILD_VERSION);
        buildDate.setText("Build Time: " + BuildConfig.BUILD_TIMESTAMP);

        return v;
    }

}
