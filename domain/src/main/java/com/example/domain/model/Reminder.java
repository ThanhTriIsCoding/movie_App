package com.example.domain.model;

public class Reminder {
    private int movieId;
    private String title;
    private String dateTime;
    private String posterUrl;
    private String releaseDate; // New field
    private double voteAverage; // New field

    public Reminder(int movieId, String title, String dateTime, String posterUrl, String releaseDate, double voteAverage) {
        this.movieId = movieId;
        this.title = title;
        this.dateTime = dateTime;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    // Getters and setters
    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }
}