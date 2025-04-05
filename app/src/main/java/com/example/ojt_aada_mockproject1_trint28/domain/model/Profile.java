package com.example.ojt_aada_mockproject1_trint28.domain.model;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {
    private String userId; // Add userId field
    private String name;
    private String email;
    private String birthday;
    private String gender;
    private String avatarUrl; // Store URL instead of Uri for Firebase

    public Profile() {
        // Default constructor required for Firebase
    }

    public Profile(String userId, String name, String email, String birthday, String gender, String avatarUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    // Helper method to convert avatarUrl to Uri for UI binding
    public Uri getAvatarUri() {
        return avatarUrl != null ? Uri.parse(avatarUrl) : null;
    }
}