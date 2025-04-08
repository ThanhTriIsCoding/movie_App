package com.example.domain.model;

public class MovieDetails {
    private final int id;
    private final String title;
    private final String overview;
    private final String releaseDate;
    private final double voteAverage;
    private final boolean adult;
    private final String posterPath;
    // Thêm các trường khác nếu cần

    public MovieDetails(int id, String title, String overview, String releaseDate, double voteAverage, boolean adult, String posterPath) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.adult = adult;
        this.posterPath = posterPath;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getOverview() { return overview; }
    public String getReleaseDate() { return releaseDate; }
    public double getVoteAverage() { return voteAverage; }
    public boolean isAdult() { return adult; }
    public String getPosterPath() { return posterPath; }
}