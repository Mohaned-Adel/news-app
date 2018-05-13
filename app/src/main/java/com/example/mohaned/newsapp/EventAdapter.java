package com.example.mohaned.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.mohaned.newsapp.MainActivity.LOG_TAG;

/**
 * Created by Mohaned on 5/12/2018.
 */

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, ArrayList<Event> eventList) { super(context, 0, eventList);}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_view, parent, false
            );
        }

        Event currentEvent = getItem(position);

        // Making an image from the image String url
        URL url = null;
        Bitmap bmp = null;

        try {
             url = new URL(currentEvent.getImageUrl());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "error with image url in EventAdapter");
        }

        try {
             bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            Log.e(LOG_TAG, "error with the connection bitmap", e);
        }

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        if (bmp != null) {
            imageView.setImageBitmap(bmp);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
        }

        // Get the name
        String name = new String(currentEvent.getArticleName());
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);

        if (name.isEmpty()) {
            nameTextView.setText("there is no Name");
        } else {
            nameTextView.setText(name);
        }

        // Get the title
        String webTitle = new String(currentEvent.getWebTitle());
        TextView titleTextview = (TextView) listItemView.findViewById(R.id.title);
        titleTextview.setText(webTitle);

        // Get the date and split it in Date and Time
        String dateAndTime = new String(currentEvent.getDate());
        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        TextView timeView = (TextView) listItemView.findViewById(R.id.time);

        String[] dates = dateAndTime.split("T");
        dateView.setText(dates[0]);
        timeView.setText(dates[1]);

        // Get the article Type
        String articleType = new String(currentEvent.getType());
        TextView typeView = (TextView) listItemView.findViewById(R.id.article);
        typeView.setText(articleType);

        // Get the article section name
        String sectionName = new String(currentEvent.getSection());
        TextView sectionView = (TextView) listItemView.findViewById(R.id.sectionName);

        return listItemView;
    }
}
