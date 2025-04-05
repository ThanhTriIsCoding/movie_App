package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ProfileRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;

import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class GetProfileUseCase {
    private final ProfileRepository profileRepository;
    private static final String DEFAULT_USER_ID = "1"; // Default userId

    @Inject
    public GetProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Observable<Profile> execute() {
        return profileRepository.getProfile(DEFAULT_USER_ID);
    }
}