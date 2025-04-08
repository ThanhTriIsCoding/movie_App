package com.example.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class ReminderEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int movieId;
    private String title;
    private String dateTime;
    private String posterUrl;
    private String releaseDate; // New field
    private double voteAverage; // New field

    public ReminderEntity(int userId, int movieId, String title, String dateTime, String posterUrl, String releaseDate, double voteAverage) {
        this.userId = userId;
        this.movieId = movieId;
        this.title = title;
        this.dateTime = dateTime;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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