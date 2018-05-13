package com.example.mohaned.newsapp;

import android.text.TextUtils;
import android.util.Log;
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

import static com.example.mohaned.newsapp.MainActivity.LOG_TAG;

/**
 * Created by Mohaned on 5/12/2018.
 */

public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
        //empty constructor
    }

    /**
     * Query the Article dataset and return a list of {@link Event} objects
     */
    public static List<Event> fetchArticleData(String requestUrl) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Articles}
        List<Event> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link articles}
        return articles;
    }

    /**
     * Returns new URL object from the given string URL
     */
    private static URL createUrl(String stringUrl) {
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
    private static String makeHttpRequest(URL url) throws IOException {
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
            if (urlConnection.getResponseCode() == 200) {
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
    private static String readFromStream(InputStream inputStream) throws IOException {
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
    public static List<Event> extractFeatureFromJson(String articleJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Event> articles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        try {
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");

            //if there are results in the results array
            for (int i = 0; resultsArray.length() > i; i++) {
                // Extract our results
                JSONObject resultObjects = resultsArray.getJSONObject(i);
                JSONArray tagsArray = resultObjects.getJSONArray("tags");

                String type = resultObjects.getString("type");
                String sectionName = resultObjects.getString("sectionName");
                String url = resultObjects.getString("webUrl");
                String Date = resultObjects.getString("webPublicationDate");
                String title = resultObjects.getString("webTitle");
                String image = "http://via.placeholder.com/48x48";
                String name = "placeholder name";

                // Create a new {@link Event} object
                Event article = new Event(image, name, title, Date, type, sectionName, url);

                // Add the new {@link Article} to the list of articles
                articles.add(article);

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the article JSON results", e);
        }
        return articles;
    }
}
