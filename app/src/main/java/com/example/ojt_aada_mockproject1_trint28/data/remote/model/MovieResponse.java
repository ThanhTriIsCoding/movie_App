package com.example.ojt_aada_mockproject1_trint28.data.remote.model;

import com.google.gson.annotations.SerializedName;

public class MovieResponse {
    private boolean adult;
    private String backdrop_path;
    private int[] genre_ids;
    private int id;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private String release_date;
    private String title;
    private boolean video;
    private double vote_average;
    private int vote_count;

    // Getters
    public boolean isAdult() {
        return adult;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public int[] getGenreIds() {
        return genre_ids;
    }

    public int getId() {
        return id;
    }

    public String getOriginalLanguage() {
        return original_language;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public double getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public String getTitle() {
        return title;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public int getVoteCount() {
        return vote_count;
    }
}