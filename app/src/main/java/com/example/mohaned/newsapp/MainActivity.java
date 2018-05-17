package com.example.mohaned.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
//            http://content.guardianapis.com/search?from-date=2014-08-20&to-date=2017-08-20&show-tags=contributor&q=football&api-key=ece856ca-fc94-4ff5-81c3-cf1541b2c105
            "http://content.guardianapis.com/search";

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
    // onCreateLoader instantiates and returns a new Loader for the given ID
    public Loader<List<Event>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_search_by_key),
                getString(R.string.settings_search_by_default)
        );
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(QUERY_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBulider = baseUri.buildUpon();

        //Append query parameter and its value, for example, the `format=geojson`
        uriBulider.appendQueryParameter("from-date", "2014-08-20");
        uriBulider.appendQueryParameter("to-date", "2017-08-20");
        uriBulider.appendQueryParameter("show-tags", "contributor");
        uriBulider.appendQueryParameter("q", orderBy);
        uriBulider.appendQueryParameter("api-key", "ece856ca-fc94-4ff5-81c3-cf1541b2c105");

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time


        return new MainActivityLoader(this, uriBulider.toString());
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

    /**
     * Step 4: Setup the Menu item in the EarthquakeActivity
     With the xml setup, we’ll need to override a couple of methods in EarthquakeActivity.java in order to inflate the menu, and respond when users click on our menu item.
     */

    /**
     * The first method is onCreateOptionsMenu. Similar to how onCreate() helps us inflate our Activity, onCreateOptionsMenu() inflates the Options Menu we specified in the XML when the EarthquakeActivity opens up.
     */
    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * The second method is onOptionsItemSelected. This method is where we can setup the specific action that occurs when any of the items in the Options Menu are selected.
     */
    @Override
    //This method passes the MenuItem that is selected:
    ////An Options Menu may have one or more items.
    public boolean onOptionsItemSelected(MenuItem item) {
//        To determine which item was selected and what action to take, call getItemId, which returns the unique ID for the menu item (defined by the android:id attribute in the menu resource).
        int id = item.getItemId();
        //You then match the ID against known menu items to perform the appropriate action. In our case, we open the SettingsActivity via an intent.
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
