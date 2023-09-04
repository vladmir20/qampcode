/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 2:39 AM
 *
 */

package com.qamp.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qamp.app.R;
import com.qamp.app.Utils.Utilss;

public class DiscoverFragment extends Fragment {

    private FloatingActionButton floatingActionButton;

    public DiscoverFragment(){

    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_discover, container, false);
        floatingActionButton= root.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DiscoverBottomSheet discoverBottomSheet = new DiscoverBottomSheet();
                discoverBottomSheet.show(getFragmentManager(),"tag");
            }
        });

        Utilss.setLanguage(getActivity());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilss.setLanguage(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        Utilss.setLanguage(getActivity());
    }
}

