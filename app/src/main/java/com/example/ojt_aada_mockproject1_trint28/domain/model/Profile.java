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
    private String avatarBase64;

    public Profile() {
        // Default constructor required for Firebase
    }

    public Profile(String userId, String name, String email, String birthday, String gender, String avatarBase64) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.avatarBase64 = avatarBase64;
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

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }
}