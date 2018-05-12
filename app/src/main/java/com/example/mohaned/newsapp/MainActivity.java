package com.example.mohaned.newsapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** URL to query the article dataset for articles information */
    private static final  String QUERY_REQUEST_URL =
            "http://content.guardianapis.com/search?from-date=2014-08-20&to-date=2017-08-20&show-tags=contributor&q=football&api-key=ece856ca-fc94-4ff5-81c3-cf1541b2c105";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kick off an {@link AsyncTask} to perform the network request
        ArticleAsyncTask task = new ArticleAsyncTask();
        task.execute();
    }

    /**
     * Update the screen to display information from the given {@link Event}.
     */
    private void updateUi(Event article) {
        // Display the Name of the article in the UI
        TextView nameTextView = (TextView) findViewById(R.id.name);
        nameTextView.setText(article.name);

        // Display the Date of the article
        TextView dateTextView = (TextView) findViewById(R.id.date);
        dateTextView.setText(article.date);

        //Display the title of it
        TextView titleTextView = (TextView) findViewById(R.id.web_title);
        titleTextView.setText(article.webTitle);
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread,
     * and then update the UI with the first article in the response
     */
    private class ArticleAsyncTask extends AsyncTask<URL, Void, Event> {

        @Override
        protected Event doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(QUERY_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "error with AsyncTask", e);
            }

            Event article = extractFeatureFromJson(jsonResponse);

            return article;
        }

        /**
         * Update the screen with the given article
         * {@link ArticleAsyncTask}
         */
        @Override
        protected void onPostExecute(Event article) {
            if (article == null) {
                return;
            }
            updateUi(article);
        }
    }

    /**
     * Returns new URL object from the given string URL
     */
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response
     */
    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode()==200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with creating url connection in makeHttpRequest method", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Event} object by parsing out information
     * about the first article from the input articleJSON string
     */
    private Event extractFeatureFromJson(String articleJSON) {
        try {
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            //if there are results in the results array
            if (resultsArray.length() > 0) {
                // Extract our results
                JSONObject firstResult = resultsArray.getJSONObject(0);
                JSONArray tagsArray = firstResult.getJSONArray("tags");
                JSONObject firstTag = tagsArray.getJSONObject(0);

                String name = firstTag.getString("webTitle");
                String Date = firstResult.getString("webPublicationDate");
                String title = firstResult.getString("webTitle");

                // Create a new {@link Event} object
                return new Event(name, Date, title);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG,"Problem parsing the article JSON results", e);
        }
        return null;
    }

}
