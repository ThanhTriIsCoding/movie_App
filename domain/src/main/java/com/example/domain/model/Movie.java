package com.example.domain.model;

import java.io.Serializable;
import java.util.Objects;

public class Movie implements Serializable {
    private  int id;
    private  String title;
    private  String overview;
    private  String releaseDate;
    private  double voteAverage;
    private  boolean adult;
    private  String posterUrl;
    private  boolean isLiked;

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

    public boolean isLiked() {
        return isLiked;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
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