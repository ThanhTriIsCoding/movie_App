package com.example.domain.model;

public class Settings {
    private String category;
    private int minRating;
    private int releaseYear;
    private String sortBy;
    private int pagesPerLoad; // New field

    public Settings(String category, int minRating, int releaseYear, String sortBy, int pagesPerLoad) {
        this.category = category;
        this.minRating = minRating;
        this.releaseYear = releaseYear;
        this.sortBy = sortBy;
        this.pagesPerLoad = pagesPerLoad;
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

    public int getPagesPerLoad() {
        return pagesPerLoad;
    }

    public void setPagesPerLoad(int pagesPerLoad) {
        this.pagesPerLoad = pagesPerLoad;
    }
}