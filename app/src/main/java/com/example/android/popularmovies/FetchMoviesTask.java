package com.example.android.popularmovies;

import android.os.AsyncTask;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.util.JsonUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

    private TaskListener mTaskListener;
    private String mApiKey;

    FetchMoviesTask(TaskListener taskListener, String apiKey) {
        mTaskListener = taskListener;
        mApiKey = apiKey;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mTaskListener.onTaskPreExecute();
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        ArrayList<Movie> movies;
        URL movieRequestUrl = NetworkUtils.buildUrl(params[0], mApiKey);
        try {
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
            movies = JsonUtils.parseJsonResponse(jsonMovieResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return movies;
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movies) {
        super.onPostExecute(movies);
        mTaskListener.onTaskPostExecute(movies);
    }
}
