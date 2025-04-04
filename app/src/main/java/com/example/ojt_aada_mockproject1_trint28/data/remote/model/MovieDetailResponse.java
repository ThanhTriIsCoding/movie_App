package com.example.ojt_aada_mockproject1_trint28.data.remote.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetailResponse {
    private boolean adult;
    private String backdrop_path;
    private int budget;
    private List<Genre> genres;
    private String homepage;
    private int id;
    private String imdb_id;
    private List<String> origin_country;
    private String original_language;
    private String original_title;
    private String overview;
    private double popularity;
    private String poster_path;
    private List<ProductionCompany> production_companies;
    private List<ProductionCountry> production_countries;
    private String release_date;
    private long revenue;
    private int runtime;
    private List<SpokenLanguage> spoken_languages;
    private String status;
    private String tagline;
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

    public int getBudget() {
        return budget;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public int getId() {
        return id;
    }

    public String getImdbId() {
        return imdb_id;
    }

    public List<String> getOriginCountry() {
        return origin_country;
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

    public List<ProductionCompany> getProductionCompanies() {
        return production_companies;
    }

    public List<ProductionCountry> getProductionCountries() {
        return production_countries;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public long getRevenue() {
        return revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<SpokenLanguage> getSpokenLanguages() {
        return spoken_languages;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
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

    public static class Genre {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class ProductionCompany {
        private int id;
        private String logo_path;
        private String name;
        private String origin_country;

        public int getId() {
            return id;
        }

        public String getLogoPath() {
            return logo_path;
        }

        public String getName() {
            return name;
        }

        public String getOriginCountry() {
            return origin_country;
        }
    }

    public static class ProductionCountry {
        private String iso_3166_1;
        private String name;

        public String getIso31661() {
            return iso_3166_1;
        }

        public String getName() {
            return name;
        }
    }

    public static class SpokenLanguage {
        private String english_name;
        private String iso_639_1;
        private String name;

        public String getEnglishName() {
            return english_name;
        }

        public String getIso6391() {
            return iso_639_1;
        }

        public String getName() {
            return name;
        }
    }
}