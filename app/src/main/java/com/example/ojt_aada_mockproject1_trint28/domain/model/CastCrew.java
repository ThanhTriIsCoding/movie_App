package com.example.ojt_aada_mockproject1_trint28.domain.model;

public class CastCrew {
    private int id;
    private String name;
    private String role; // Character for cast, job for crew
    private String profilePath;

    public CastCrew(int id, String name, String role, String profilePath) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.profilePath = profilePath;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public String getProfilePath() {
        return profilePath;
    }
}