package com.example.ojt_aada_mockproject1_trint28.domain.model;

public class Reminder {
    private int movieId;
    private String title;
    private String dateTime;

    // Constructor, getters, setters
    public Reminder(int movieId, String title, String dateTime) {
        this.movieId = movieId;
        this.title = title;
        this.dateTime = dateTime;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getTitle() {
        return title;
    }

    public String getDateTime() {
        return dateTime;
    }
}