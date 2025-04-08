package com.example.data.module;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.data.local.dao.MovieDao;
import com.example.data.local.dao.ReminderDao;
import com.example.data.local.database.AppDatabase;
import com.example.data.remote.api.ApiService;
import com.example.data.remote.api.RetrofitClient;
import com.example.data.repository.MovieRepository;
import com.example.data.repository.ProfileRepository;
import com.example.data.repository.ReminderRepository;
import com.example.data.repository.SettingsRepositoryImpl;
import com.example.domain.repository.IMovieRepository;
import com.example.domain.repository.IProfileRepository;
import com.example.domain.repository.IReminderRepository;
import com.example.domain.repository.SettingsRepository;
import com.example.domain.usecase.SettingsUseCases;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataModule {

    @Provides
    @Singleton
    ApiService provideApiService() {
        return RetrofitClient.getApiService();
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "movie_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    MovieDao provideMovieDao(AppDatabase appDatabase) {
        return appDatabase.movieDao();
    }

    @Provides
    @Singleton
    ReminderDao provideReminderDao(AppDatabase appDatabase) {
        return appDatabase.reminderDao();
    }

    @Provides
    @Singleton
    IMovieRepository provideMovieRepository(ApiService apiService, SettingsUseCases settingsUseCases, @Named("apiKey") String apiKey, MovieDao movieDao) {
        return new MovieRepository(apiService, settingsUseCases, apiKey, movieDao);
    }

    @Provides
    @Singleton
    IReminderRepository provideReminderRepository(ReminderDao reminderDao) {
        return new ReminderRepository(reminderDao);
    }

    @Provides
    @Singleton
    IProfileRepository provideProfileRepository() {
        return new ProfileRepository();
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
}