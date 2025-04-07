package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ProfileRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;
import javax.inject.Inject;
import io.reactivex.rxjava3.core.Observable;

public class ProfileUseCases {
    private final ProfileRepository profileRepository;

    @Inject
    public ProfileUseCases(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Observable<Profile> getProfile() {
        return profileRepository.getProfile("1"); // Default userId
    }
}