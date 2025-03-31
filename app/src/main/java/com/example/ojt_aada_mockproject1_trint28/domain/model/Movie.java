package com.example.ojt_aada_mockproject1_trint28.domain.model;

import java.util.Objects;

public class Movie {
    private final int id;
    private final String title;
    private final String overview;
    private final String releaseDate;
    private final double voteAverage;
    private final boolean adult;
    private final String posterUrl;
    private final boolean isLiked;

    public Movie(int id, String title, String overview, String releaseDate, double voteAverage, boolean adult, String posterUrl, boolean isLiked) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.adult = adult;
        this.posterUrl = posterUrl;
        this.isLiked = isLiked;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public boolean isLiked() { // New getter
        return isLiked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id == movie.id &&
                Double.compare(movie.voteAverage, voteAverage) == 0 &&
                adult == movie.adult &&
                isLiked == movie.isLiked && // Include isLiked in equals
                Objects.equals(title, movie.title) &&
                Objects.equals(overview, movie.overview) &&
                Objects.equals(releaseDate, movie.releaseDate) &&
                Objects.equals(posterUrl, movie.posterUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, overview, releaseDate, voteAverage, adult, posterUrl, isLiked); // Include isLiked in hashCode
    }
}