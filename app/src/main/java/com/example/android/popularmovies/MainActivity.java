package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.util.JsonUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    // Sort selection spinner entries
    private static final int MOST_POPULAR = 0;
    private static final int TOP_RATED = 1;

    // Query strings to be passed to URL Builder
    private static final String POPULAR_QUERY = "popular";
    private static final String TOP_RATED_QUERY = "top_rated";

    private int selectionIndex = 0;

    private ActivityMainBinding binding;

    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 2 columns for portrait and 4 columns for landscape
        GridLayoutManager layoutManager = new GridLayoutManager(this,
                getResources().getInteger(R.integer.grid_columns));
        binding.rvMovies.setLayoutManager(layoutManager);
        binding.rvMovies.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);
        binding.rvMovies.setAdapter(mMovieAdapter);

        if (savedInstanceState != null) {
            // if savedInstanceState is not null set movie data from bundle
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
            mMovieAdapter.setMovieData(mMovieList);
            selectionIndex = savedInstanceState.getInt("selection");
        } else {
            // else fetch default category data from network
            if (isOnline()) {
                new FetchMoviesTask().execute("popular");
            } else {
                showNoNetwork();
            }
        }

        binding.sortSelection.setSelected(false);
        binding.sortSelection.setSelection(selectionIndex, true);
        binding.sortSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case MOST_POPULAR:
                        // don't crash if no network, show toast to notify user
                        if (isOnline()) {
                            selectionIndex = MOST_POPULAR;
                            new FetchMoviesTask().execute(POPULAR_QUERY);
                            // if selection is changed, scroll to top of list
                            binding.rvMovies.smoothScrollToPosition(0);
                        } else {
                            showNoNetwork();
                        }
                        break;
                    case TOP_RATED:
                        // don't crash if no network, show toast to notify user
                        if (isOnline()) {
                            selectionIndex = TOP_RATED;
                            new FetchMoviesTask().execute(TOP_RATED_QUERY);
                            // if selection is changed, scroll to top of list
                            binding.rvMovies.smoothScrollToPosition(0);
                        } else {
                            showNoNetwork();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", mMovieList);
        outState.putInt("selection", selectionIndex);
    }

    private void showMovieDataView() {
        binding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        binding.rvMovies.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        binding.rvMovies.setVisibility(View.INVISIBLE);
        binding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showNoNetwork() {
        Toast.makeText(this, getString(R.string.no_network_message), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    /**
     * Ran into issues using ping to check network connection on emulator. Then also read
     * about some Samsung devices (and others) not returning expected results from system/bin/ping
     */
    @SuppressWarnings("deprecation")
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.pbLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            URL movieRequestUrl = NetworkUtils.buildUrl(params[0], getString(R.string.API_KEY));
            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                mMovieList = JsonUtils.parseJsonResponse(jsonMovieResponse);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return mMovieList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);
            binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

            if (movies != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movies);
            } else {
                showErrorMessage();
            }
        }
    }
}
