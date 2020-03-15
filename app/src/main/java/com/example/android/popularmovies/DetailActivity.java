package com.example.android.popularmovies;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.popularmovies.databinding.ActivityDetailBinding;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.util.StringUtils;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mMovie = bundle.getParcelable("movie");
        }

        populateUI();
    }

    private void populateUI() {
        Picasso.get().load(mMovie.getBackdropPath())
                .error(R.drawable.ic_photo_black_24dp)
                .placeholder(R.drawable.ic_photo_black_24dp
                ).into(binding.backdrop);

        Picasso.get().load(mMovie.getPosterPath())
                .error(R.drawable.ic_photo_black_24dp)
                .placeholder(R.drawable.ic_photo_black_24dp).into(binding.poster);

        binding.title.setText(mMovie.getOriginalTitle());

        binding.releaseDate.setText(String.format(Locale.getDefault(), getString(R.string.date_released),
                StringUtils.getFormattedDate(mMovie.getReleaseDate())));
        binding.voteAverage.setText(String.format(Locale.getDefault(), getString(R.string.vote_average),
                mMovie.getVoteAverage()));
        binding.overview.setText(mMovie.getOverview());
    }
}
