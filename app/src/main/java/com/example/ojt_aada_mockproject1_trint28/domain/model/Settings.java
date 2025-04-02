package com.example.ojt_aada_mockproject1_trint28.domain.model;

public class Settings {
    private String category;
    private int minRating;
    private int releaseYear;
    private String sortBy;

    public Settings(String category, int minRating, int releaseYear, String sortBy) {
        this.category = category;
        this.minRating = minRating;
        this.releaseYear = releaseYear;
        this.sortBy = sortBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getMinRating() {
        return minRating;
    }

    public void setMinRating(int minRating) {
        this.minRating = minRating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}