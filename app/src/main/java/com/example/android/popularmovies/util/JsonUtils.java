package com.example.android.popularmovies.util;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonUtils {

    public static ArrayList<Movie> parseJsonResponse(String json) {
        String posterBasePath = "https://image.tmdb.org/t/p/w185";
        String backdropBasePath = "https://image.tmdb.org/t/p/w500";

        try {
            JSONObject movieJson = new JSONObject(json);
            JSONArray movies = movieJson.getJSONArray("results");
            ArrayList<Movie> movieList = new ArrayList<>();
            for (int i = 0; i < movies.length(); i++) {
                JSONObject movieData = movies.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(movieData.optInt("id"));
                movie.setOriginalTitle(movieData.optString("original_title"));
                movie.setPosterPath(posterBasePath + movieData.optString("poster_path"));
                movie.setBackdropPath(backdropBasePath + movieData.optString("backdrop_path"));
                movie.setOverview(movieData.optString("overview"));
                movie.setVoteAverage(movieData.optDouble("vote_average"));
                movie.setReleaseDate(movieData.optString("release_date"));
                movieList.add(movie);
            }
            return movieList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
