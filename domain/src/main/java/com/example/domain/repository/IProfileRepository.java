package com.example.domain.repository;

import com.example.domain.model.Profile;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface IProfileRepository {
    Observable<Profile> getProfile(String userId);
    Completable saveProfile(Profile profile);
}