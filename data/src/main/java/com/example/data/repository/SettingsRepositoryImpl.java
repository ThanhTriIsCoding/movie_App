package com.example.data.repository;

import android.content.SharedPreferences;

import com.example.domain.model.Settings;
import com.example.domain.repository.SettingsRepository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class SettingsRepositoryImpl implements SettingsRepository {

    private final SharedPreferences sharedPreferences;
    private static final String SORT_BY_RELEASE_DATE = "release_date";
    private static final String SORT_BY_RATING = "rating";
    private static final int DEFAULT_SORT_BY_VALUE = 0; // 0 cho release_date, 1 cho rating

    public SettingsRepositoryImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Single<Settings> getSettings() {
        return Single.just(new Settings(
                sharedPreferences.getString("category", "Popular Movies"),
                sharedPreferences.getInt("rate", 5),
                Integer.parseInt(sharedPreferences.getString("release_year", "2015")),
                sharedPreferences.getInt("sort_by", DEFAULT_SORT_BY_VALUE) == 0 ? SORT_BY_RELEASE_DATE : SORT_BY_RATING
        ));
    }

    @Override
    public Completable updateSettings(Settings settings) {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("category", settings.getCategory());
            editor.putInt("rate", settings.getMinRating());
            editor.putString("release_year", String.valueOf(settings.getReleaseYear()));
            editor.putInt("sort_by", settings.getSortBy().equals(SORT_BY_RELEASE_DATE) ? 0 : 1);
            editor.apply();
        });
    }

    @Override
    public Completable resetSettings() {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("category", "Popular Movies");
            editor.putInt("rate", 5);
            editor.putString("release_year", "2015");
            editor.putInt("sort_by", 0); // Mặc định là release_date
            editor.apply();
        });
    }
}