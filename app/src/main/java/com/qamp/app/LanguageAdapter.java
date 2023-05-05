/*
 * *
 *  * Created by Shivam Tiwari on 05/05/23, 3:15 PM
 *  * Copyright (c) 2023 . All rights reserved.
 *  * Last modified 21/04/23, 3:40 AM
 *
 */

package com.qamp.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.qamp.app.Utils.AppUtils;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private final Context context;
    private final List<Language> personUtils;
    int row_index = -1;

    public LanguageAdapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(personUtils.get(position));
        Language pu = personUtils.get(position);
        holder.language.setText(pu.getLanguage());

        holder.language_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                AppUtils.setLanguage(pu.getLanguageCode(), (Activity) context);
            }
        });

        if (row_index == position) {
            holder.language_layout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.language_item_background_outline));
            holder.language_layout.setBackgroundResource(R.color.colorPrimaryDark);
            holder.language.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.language_layout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.language_item_background_outline));
            holder.language.setTextColor(Color.parseColor("#000000"));
        }

    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView language;
        public LinearLayout language_layout;

        public ViewHolder(View itemView) {
            super(itemView);

            language = (TextView) itemView.findViewById(R.id.language);
            language_layout = (LinearLayout) itemView.findViewById(R.id.language_layout);

        }
    }

}