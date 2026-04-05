package com.example.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> images;

    public ImageAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        ImageView img;

        if (view == null) {
            img = new ImageView(context);
            img.setLayoutParams(new GridView.LayoutParams(300, 300));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            img = (ImageView) view;
        }

        img.setImageURI(Uri.parse(images.get(i)));

        return img;
    }
}