package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.SettingsRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class SettingsUseCases {
    private final SettingsRepository settingsRepository;
    private Settings currentSettings;

    @Inject
    public SettingsUseCases(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
        this.currentSettings = new Settings("Popular Movies", 5, 2015, "release_date");
        loadSettings();
    }

    private void loadSettings() {
        settingsRepository.getSettings()
                .subscribe(settings -> currentSettings = settings)
                .dispose();
    }

    public Single<Settings> getSettings() {
        return settingsRepository.getSettings();
    }

    public Completable updateCategory(String category) {
        currentSettings.setCategory(category);
        return settingsRepository.updateSettings(currentSettings);
    }

    public Completable updateRating(int rating) {
        currentSettings.setMinRating(rating);
        return settingsRepository.updateSettings(currentSettings);
    }

    public Completable updateReleaseYear(int year) {
        currentSettings.setReleaseYear(year);
        return settingsRepository.updateSettings(currentSettings);
    }

    public Completable updateSortBy(String sortBy) {
        currentSettings.setSortBy(sortBy);
        return settingsRepository.updateSettings(currentSettings);
    }

    public Completable resetSettings() {
        return settingsRepository.resetSettings()
                .doOnComplete(() -> loadSettings());
    }
}