package com.example.android.popularmovies.util;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = "Network Utils";

    private final static String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";

    /**
     * Builds the URL used to talk to the movies db using a query parameter.
     *
     * @param sort   The sort that will be queried for.
     * @param apiKey The developer api key, required for access to the movies api
     * @return The url used for the sort requested.
     */
    public static URL buildUrl(String sort, String apiKey) {
        Log.d(TAG, "buildURL called");
        Uri builtUri = Uri.parse(MOVIES_BASE_URL)
                .buildUpon()
                .appendPath(sort)
                .appendQueryParameter("api_key", apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d(TAG, "URL: " + url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url THe URL to fetch the HTTP response from.
     * @return The contents of the HTTP Response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
