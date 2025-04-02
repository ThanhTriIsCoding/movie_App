package com.example.ojt_aada_mockproject1_trint28.data.repository;

import android.content.SharedPreferences;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.SettingsRepository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class SettingsRepositoryImpl implements SettingsRepository {

    private final SharedPreferences sharedPreferences;

    public SettingsRepositoryImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Single<Settings> getSettings() {
        return Single.just(new Settings(
                sharedPreferences.getString("category", "Popular Movies"),
                sharedPreferences.getInt("rate", 5),
                Integer.parseInt(sharedPreferences.getString("release_year", "2015")),
                sharedPreferences.getInt("sort_by", R.id.rb_release_date) == R.id.rb_release_date ? "release_date" : "rating"
        ));
    }

    @Override
    public Completable updateSettings(Settings settings) {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("category", settings.getCategory());
            editor.putInt("rate", settings.getMinRating());
            editor.putString("release_year", String.valueOf(settings.getReleaseYear()));
            editor.putInt("sort_by", settings.getSortBy().equals("release_date") ? R.id.rb_release_date : R.id.rb_rating);
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
            editor.putInt("sort_by", R.id.rb_release_date);
            editor.apply();
        });
    }
}