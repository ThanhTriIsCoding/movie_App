// com.example.domain.usecase.ProfileUseCases
package com.example.domain.usecase;

import com.example.domain.model.Profile;
import com.example.domain.repository.IProfileRepository;
import io.reactivex.rxjava3.core.Observable;
import javax.inject.Inject;

public class ProfileUseCases {
    private final IProfileRepository profileRepository;

    @Inject
    public ProfileUseCases(IProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Observable<Profile> getProfile() {
        return profileRepository.getProfile("1"); // Default userId
    }
}