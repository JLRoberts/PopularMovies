package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.android.popularmovies.databinding.ActivityMainBinding;
import com.example.android.popularmovies.model.Movie;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler, TaskListener {

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
            fetchData(POPULAR_QUERY);
        }

        binding.sortSelection.setSelected(false);
        binding.sortSelection.setSelection(selectionIndex, true);
        binding.sortSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case MOST_POPULAR:
                        selectionIndex = MOST_POPULAR;
                        fetchData(POPULAR_QUERY);
                        binding.rvMovies.smoothScrollToPosition(0);
                        break;
                    case TOP_RATED:
                        selectionIndex = TOP_RATED;
                        fetchData(TOP_RATED_QUERY);
                        binding.rvMovies.smoothScrollToPosition(0);
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

    private void fetchData(String query) {
        if (isOnline()) {
            new FetchMoviesTask(this, getString(R.string.API_KEY)).execute(query);
        } else {
            showNoNetwork();
        }
    }

    @Override
    public void onTaskPreExecute() {
        binding.pbLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTaskPostExecute(ArrayList<Movie> movies) {
        binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        if (movies != null) {
            showMovieDataView();
            mMovieList = movies;
            mMovieAdapter.setMovieData(movies);
        } else {
            showErrorMessage();
        }
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
}
