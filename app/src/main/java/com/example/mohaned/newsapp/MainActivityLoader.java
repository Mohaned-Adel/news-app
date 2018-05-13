package com.example.mohaned.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Mohaned on 5/12/2018.
 */

public class MainActivityLoader extends AsyncTaskLoader<List<Event>> {

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link MainActivityLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public MainActivityLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Event> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract ; list of articles
        List<Event> articles = QueryUtils.fetchArticleData(mUrl);
        return articles;
    }
}
