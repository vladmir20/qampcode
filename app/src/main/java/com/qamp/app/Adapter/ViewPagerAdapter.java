package com.qamp.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.qamp.app.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] images;
    private String[] headings;
    private String[] descriptions;

    public ViewPagerAdapter(Context context, int[] images, String[] headings, String[] descriptions) {
        this.context = context;
        this.images = images;
        this.headings = headings;
        this.descriptions = descriptions;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.view_pager_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        TextView textViewHeading = itemView.findViewById(R.id.textViewHeading);
        TextView textViewDescription = itemView.findViewById(R.id.textViewDescription);

        imageView.setImageResource(images[position]);
        textViewHeading.setText(headings[position]);
        textViewDescription.setText(descriptions[position]);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
