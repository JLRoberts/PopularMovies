package com.example.android.popularmovies;

import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;

public interface TaskListener {
    void onTaskPreExecute();
    void onTaskPostExecute(ArrayList<Movie> movies);
}
