/*
 * *
 *  *  on 20/05/23, 3:30 AM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 20/05/23, 3:25 AM
 *
 */

//
// Decompiled by Procyon v0.5.36
// 

package com.qamp.app.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.qamp.app.R;

import java.util.List;

public class CountryListFragment extends BaseDialogFragment
{
    protected String TAG;
    CountryListerer mListener;
    CountryRecyclerViewAdapter mAdapter;
    RecyclerView mRecyclerView;
    int mLastPosition;
    boolean mListenerInvoked;
    
    public CountryListFragment() {
        this.TAG = "CountryListFragment";
        this.mListener = null;
        this.mAdapter = null;
        this.mLastPosition = 0;
        this.mListenerInvoked = false;
    }
    
    @Nullable
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final LinearLayout l = (LinearLayout)inflater.inflate(R.layout.country_list, container, false);
        this.mRecyclerView = (RecyclerView)l.findViewById(R.id.recyclerview);
        this.setupRecyclerView();
        final Dialog d = this.getDialog();
        if (null != d) {
            d.setTitle((CharSequence)"Select Country");
        }
        return (View)l;
    }
    
    private void setupRecyclerView() {
        this.mRecyclerView.setLayoutManager((RecyclerView.LayoutManager)new LinearLayoutManager(this.mRecyclerView.getContext()));
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setItemAnimator((RecyclerView.ItemAnimator)new DefaultItemAnimator());
        this.mAdapter = new CountryRecyclerViewAdapter((Context)this.getActivity(), R.layout.country_list_item, R.id.country_name, R.id.country_code);
        this.mLastPosition = this.mAdapter.getCountryPosition() - 5;
        if (this.mLastPosition < 0) {
            this.mLastPosition = 0;
        }
        this.showCountries();
    }
    
    private void showCountries() {
        this.mRecyclerView.setAdapter((RecyclerView.Adapter)this.mAdapter);
        this.mRecyclerView.scrollToPosition(this.mLastPosition);
    }
    
    public void setOnCountrySelected(final CountryListerer listener) {
        this.mListener = listener;
    }
    
    public void onPause() {
        super.onPause();
        this.dismiss();
    }
    
    private void informActivity() {
        if (this.mListenerInvoked) {
            return;
        }
        this.mListenerInvoked = true;
        this.mListener.onCountryCanceled();
    }
    
    public void onCancel(final DialogInterface dialog) {
        super.onCancel(dialog);
        this.informActivity();
    }
    
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        this.informActivity();
    }
    
    public class CountryRecyclerViewAdapter extends RecyclerView.Adapter<CountryRecyclerViewAdapter.ViewHolder>
    {
        private final TypedValue mTypedValue;
        private int mBackground;
        private List<String> mValues;
        private int mLayoutId;
        private int mNameId;
        private int mCodeId;
        CountyCodeUtils.CountryInfo[] countryInfo;
        Context mContext;
        int mCountryPosition;
        
        public String getValueAt(final int position) {
            return this.mValues.get(position);
        }
        
        public CountyCodeUtils.CountryInfo[] getCountries() {
            return CountyCodeUtils.countryInfo;
        }
        
        public CountryRecyclerViewAdapter(final Context context, final int layoutid, final int nameid, final int codeid) {
            this.mTypedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, this.mTypedValue, true);
            this.mBackground = this.mTypedValue.resourceId;
            this.mLayoutId = layoutid;
            this.mNameId = nameid;
            this.mCodeId = codeid;
            this.mContext = context;
            this.countryInfo = this.getCountries();
        }
        
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(this.mLayoutId, parent, false);
            view.setBackgroundResource(this.mBackground);
            return new ViewHolder(view, this.mNameId, this.mCodeId);
        }
        
        public String getCountryCode(final int position) {
            return Integer.toString(this.countryInfo[position].countryCode);
        }
        
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final int color = ContextCompat.getColor((Context)CountryListFragment.this.getActivity(), R.color.myPrimaryColor);
            if (position == this.mCountryPosition && this.mCountryPosition >= 0) {
                holder.mNameView.setTextColor(color);
                if (this.mCodeId > 0) {
                    holder.mCodeView.setTextColor(color);
                }
            }
            else {
                holder.mNameView.setTextColor(-16777216);
                if (this.mCodeId > 0) {
                    holder.mCodeView.setTextColor(-16777216);
                }
            }
            holder.name = this.countryInfo[position].countryName;
            holder.code = this.getCountryCode(position);
            holder.mNameView.setText((CharSequence)this.countryInfo[position].countryName);
            if (this.mCodeId > 0) {
                holder.mCodeView.setText((CharSequence)("+" + this.getCountryCode(position)));
            }
            holder.position = position;
            final int position_f = position;
            holder.mView.setOnClickListener((View.OnClickListener)new View.OnClickListener() {
                public void onClick(final View v) {
                    final ViewHolder holder = (ViewHolder)v.getTag();
                    CountryListFragment.this.mLastPosition = position_f - 5;
                    if (CountryListFragment.this.mLastPosition < 0) {
                        CountryListFragment.this.mLastPosition = 0;
                    }
                    if (null == CountryListFragment.this.mListener) {
                        return;
                    }
                    CountryListFragment.this.mListenerInvoked = true;
                    CountryListFragment.this.mListener.onCountrySelected(holder.name, holder.code);
                    CountryListFragment.this.dismiss();
                }
            });
        }
        
        public int getItemCount() {
            return this.countryInfo.length;
        }
        
        public int getCountryPosition() {
            return this.mCountryPosition;
        }
        
        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public View mView;
            public TextView mNameView;
            public TextView mCodeView;
            public String name;
            public String code;
            public int position;
            
            public ViewHolder(final View view, final int nameid, final int codeid) {
                super(view);
                (this.mView = view).setTag((Object)this);
                if (nameid > 0) {
                    this.mNameView = (TextView)view.findViewById(nameid);
                }
                if (codeid > 0) {
                    this.mCodeView = (TextView)view.findViewById(codeid);
                }
            }
        }
    }
    
    public interface CountryListerer
    {
        void onCountrySelected(final String p0, final String p1);
        
        void onCountryCanceled();
    }
}
