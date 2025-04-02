package com.example.ojt_aada_mockproject1_trint28.domain.repository;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface SettingsRepository {
    Single<Settings> getSettings();
    Completable updateSettings(Settings settings);
    Completable resetSettings();
}