/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */
package com.qamp.app;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.mesibo.mediapicker.ImageViewTouch;
import com.mesibo.mediapicker.SocialUtilities;
import com.qamp.app.Utils.AppUtils;

import java.io.File;
import java.util.ArrayList;

public class ZoomPictureActivity extends AppCompatActivity {

    private static final String TAG = "zoomVuPictureActivity";

    ImageFragmentPagerAdapter mImageFragmentPagerAdapter = null;
    public ArrayList<String> mImageArraylist = null;
    int mStartPosition = 0;
    ViewPager mViewPager = null;
    ImageView back_btn;
    // These matrices will be used to move and zoom image

    public static String filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_pager);
        AppUtils.setStatusBarColor(ZoomPictureActivity.this, R.color.colorAccent);
        Intent intent = getIntent();

        filePath = intent.getStringExtra("filePath");
        mImageArraylist = intent.getStringArrayListExtra("stringImageArray");
        mStartPosition = intent.getIntExtra("position", 0);

        if (mImageArraylist == null & filePath != null) {
            mImageArraylist = new ArrayList<>();
            mImageArraylist.add(filePath);
            mStartPosition = 0;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_left));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            String title = getIntent().getStringExtra("title");
            actionBar.setTitle(title);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#20000000")));
            // actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_tintTaskBar)));
        }
        //  actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background_tintTaskBar)));
        mImageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager(), mImageArraylist);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mImageFragmentPagerAdapter);
        mViewPager.setCurrentItem(mStartPosition);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ImageFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> mImageArray;

        public ImageFragmentPagerAdapter(FragmentManager fm, ArrayList<String> list) {
            super(fm);
            this.mImageArray = list;
        }

        @Override
        public int getCount() {
            if (null == mImageArray)
                return 0;

            return mImageArray.size();
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(this.mImageArray, position);
        }
    }

    public static class SwipeFragment extends Fragment {

        RelativeLayout mTouchLayout;

        @SuppressLint("RestrictedApi")
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.zoomvu_layout, container, false);
            ImageViewTouch imageView = (ImageViewTouch) swipeView.findViewById(R.id.imageViewz);
            Bundle bundle = getArguments();
            ArrayList<String> imageArray = bundle.getStringArrayList("images");
            int position = bundle.getInt("position");
            final String imageFileName = imageArray.get(position);

            if (SocialUtilities.isImageFile(imageFileName)) {
                View v =  swipeView.findViewById(R.id.video_layer);
                v.setVisibility(GONE);
                ImageViewTouch touchLayout = (ImageViewTouch) swipeView.findViewById(R.id.imageViewz);

                if (null != ((AppCompatActivity) getActivity()).getSupportActionBar())
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(true);

                touchLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AppCompatActivity activity = (AppCompatActivity) getActivity();
                        if (null == activity)
                            return;

                        ActionBar actionBar = activity.getSupportActionBar();
                        if (null == actionBar)
                            return;

                        if (actionBar.isShowing()) {
                            actionBar.hide();
                        } else {
                            actionBar.show();
                        }

                    }
                });
                Bitmap b = BitmapFactory.decodeFile(imageFileName);
                imageView.setImageBitmap(b);

            } else if (SocialUtilities.isVideoFile(imageFileName)) {

                View v = (ImageView) swipeView.findViewById(R.id.video_layer);
                v.setVisibility(View.VISIBLE);
                //ImageViewTouch touchLayout = (ImageViewTouch) swipeView.findViewById(R.id.imageViewz);
                //ImagePicker imh = ImagePicker.getInstance(getActivity());
                Bitmap b = SocialUtilities.createThumbnailAtTime(imageFileName, 0);
                imageView.setImageBitmap(b);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        File playFile = new File(imageFileName);
                        if (playFile.exists()) {
                            intent.setDataAndType(Uri.fromFile(playFile), "video/mp4");
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "The file doesn´t exist!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            return swipeView;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (null != ((AppCompatActivity) getActivity()).getSupportActionBar()) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            }


        }

        static SwipeFragment newInstance(ArrayList<String> ImageArray, int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("images", ImageArray);
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }


    }

}
