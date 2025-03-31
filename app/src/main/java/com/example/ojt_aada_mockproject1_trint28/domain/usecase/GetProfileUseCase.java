package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;

import io.reactivex.rxjava3.core.Observable;

public class GetProfileUseCase {
    public Observable<Profile> execute() {
        return Observable.just(new Profile("Thang Nguyen", "abc@abc.com", "2015/11/27", "Female", null));
    }
}