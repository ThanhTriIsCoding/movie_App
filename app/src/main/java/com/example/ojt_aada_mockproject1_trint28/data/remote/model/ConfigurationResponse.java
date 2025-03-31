package com.example.ojt_aada_mockproject1_trint28.data.remote.model;

public class ConfigurationResponse {
    private Images images;

    public Images getImages() {
        return images;
    }

    public static class Images {
        private String base_url;
        private String secure_base_url;
        private String[] poster_sizes;

        public String getBaseUrl() {
            return base_url;
        }

        public String getSecureBaseUrl() {
            return secure_base_url;
        }

        public String[] getPosterSizes() {
            return poster_sizes;
        }
    }
}