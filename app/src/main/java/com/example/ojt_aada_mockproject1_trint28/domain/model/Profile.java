package com.example.ojt_aada_mockproject1_trint28.domain.model;

import android.net.Uri;

public class Profile {
    private String name;
    private String email;
    private String birthday;
    private String gender;
    private Uri avatarUri;

    public Profile(String name, String email, String birthday, String gender, Uri avatarUri) {
        this.name = name;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.avatarUri = avatarUri;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getGender() {
        return gender;
    }

    public Uri getAvatarUri() {
        return avatarUri;
    }
}