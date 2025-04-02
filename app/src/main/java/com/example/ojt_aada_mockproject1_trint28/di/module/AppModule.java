package com.example.ojt_aada_mockproject1_trint28.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.RetrofitClient;
import com.example.ojt_aada_mockproject1_trint28.data.repository.MovieRepository;
import com.example.ojt_aada_mockproject1_trint28.data.repository.SettingsRepositoryImpl;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.SettingsRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModelFactory;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    ApiService provideApiService() {
        return RetrofitClient.getApiService();
    }

    @Provides
    @Singleton
    String provideApiKey() {
        return "e7631ffcb8e766993e5ec0c1f4245f93";
    }

    @Provides
    @Singleton
    MovieRepository provideMovieRepository(ApiService apiService, UpdateSettingsUseCase updateSettingsUseCase, String apiKey) {
        return new MovieRepository(apiService, updateSettingsUseCase, apiKey);
    }

    @Provides
    @Singleton
    IMovieRepository provideIMovieRepository(MovieRepository movieRepository) {
        return movieRepository;
    }

    @Provides
    @Singleton
    GetMoviesUseCase provideGetMoviesUseCase(IMovieRepository movieRepository) {
        return new GetMoviesUseCase(movieRepository);
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    SettingsRepository provideSettingsRepository(SharedPreferences sharedPreferences) {
        return new SettingsRepositoryImpl(sharedPreferences);
    }

    @Provides
    @Singleton
    UpdateSettingsUseCase provideUpdateSettingsUseCase(SettingsRepository settingsRepository) {
        return new UpdateSettingsUseCase(settingsRepository);
    }

    @Provides
    @Singleton
    MovieListViewModelFactory provideMovieListViewModelFactory(GetMoviesUseCase getMoviesUseCase, UpdateSettingsUseCase updateSettingsUseCase, String apiKey) {
        return new MovieListViewModelFactory(getMoviesUseCase, updateSettingsUseCase, apiKey);
    }
}