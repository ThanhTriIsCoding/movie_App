package com.example.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieListResponse {
    private int page;
    private List<MovieResponse> results;
    private int total_pages;
    private int total_results;

    public int getPage() {
        return page;
    }

    public List<MovieResponse> getResults() {
        return results;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public int getTotalResults() {
        return total_results;
    }
}