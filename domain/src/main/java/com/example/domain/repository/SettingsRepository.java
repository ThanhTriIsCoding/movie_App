package com.example.domain.repository;


import com.example.domain.model.Settings;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface SettingsRepository {
    Single<Settings> getSettings();
    Completable updateSettings(Settings settings);
    Completable resetSettings();
}