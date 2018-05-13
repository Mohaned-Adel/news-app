package com.example.mohaned.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Event>> {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the article dataset for articles information */
    private static final  String QUERY_REQUEST_URL =
            "http://content.guardianapis.com/search?from-date=2014-08-20&to-date=2017-08-20&show-tags=contributor&q=football&api-key=ece856ca-fc94-4ff5-81c3-cf1541b2c105";

    /**
     * Adapter for the list of articles
     */
    private EventAdapter mAdapter;

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView articleListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EventAdapter(this,new ArrayList<Event>());

        // Set the adapter on the {@link ListView}
        //so the list can be populated in the user interface
        articleListView.setAdapter(mAdapter);

        // Set an item click listener
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Find the current article that was clicked on
                Event currentArticle = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent)
                Uri articleUri = Uri.parse(currentArticle.getUrl());

                // Create a new intent to view the article URI
                Intent website = new Intent(Intent.ACTION_VIEW, articleUri);

                // Send the intent to launch a new activity
                startActivity(website);
            }
        });

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        articleListView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the ConnectivityManger to check state of network connectivity
        ConnectivityManager connMagr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMagr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will ve visible
            View loadingIndicator = findViewById(R.id.loading);
            loadingIndicator.setVisibility(View.GONE);

            //Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_connection);
        }
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {
        return new MainActivityLoader(this, QUERY_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> data) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No articles found"
        mEmptyStateTextView.setText(R.string.no_article);

        // Clear the adapter of previous article data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        // Loader reset, so we can cleat out our existing data
        mAdapter.clear();
    }
}
