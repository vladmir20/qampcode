package com.qamp.app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import org.mesibo.messenger.R;

public class DiscoverFragment extends Fragment {
    public DiscoverFragment(){

    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_discover, container, false);
        //Utils.setLanguage(getActivity());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
      //  Utils.setLanguage(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
       // Utils.setLanguage(getActivity());
    }
}

