package com.example.ojt_aada_mockproject1_trint28.domain.di.module;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.room.Room;
import com.example.ojt_aada_mockproject1_trint28.data.local.dao.MovieDao;
import com.example.ojt_aada_mockproject1_trint28.data.local.dao.ReminderDao;
import com.example.ojt_aada_mockproject1_trint28.data.local.database.AppDatabase;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.RetrofitClient;
import com.example.ojt_aada_mockproject1_trint28.data.repository.MovieRepository;
import com.example.ojt_aada_mockproject1_trint28.data.repository.ProfileRepository;
import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.data.repository.SettingsRepositoryImpl;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.SettingsRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.MovieUseCases;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.ProfileUseCases;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.ReminderUseCases;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.SettingsUseCases;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Named;
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
    @Named("apiKey")
    String provideApiKey() {
        return "e7631ffcb8e766993e5ec0c1f4245f93";
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
    MovieRepository provideMovieRepository(ApiService apiService, SettingsUseCases settingsUseCases, @Named("apiKey") String apiKey, MovieDao movieDao) {
        return new MovieRepository(apiService, settingsUseCases, apiKey, movieDao);
    }

    @Provides
    @Singleton
    ProfileRepository provideProfileRepository() {
        return new ProfileRepository();
    }

    @Provides
    @Singleton
    IMovieRepository provideIMovieRepository(MovieRepository movieRepository) {
        return movieRepository;
    }

    @Provides
    @Singleton
    ReminderDao provideReminderDao(AppDatabase appDatabase) {
        return appDatabase.reminderDao();
    }

    @Provides
    @Singleton
    ReminderRepository provideReminderRepository(ReminderDao reminderDao) {
        return new ReminderRepository(reminderDao);
    }

    @Provides
    @Singleton
    MovieUseCases provideMovieUseCases(IMovieRepository movieRepository) {
        return new MovieUseCases(movieRepository);
    }

    @Provides
    @Singleton
    ReminderUseCases provideReminderUseCases(ReminderRepository reminderRepository) {
        return new ReminderUseCases(reminderRepository);
    }

    @Provides
    @Singleton
    ProfileUseCases provideProfileUseCases(ProfileRepository profileRepository) {
        return new ProfileUseCases(profileRepository);
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
    SettingsUseCases provideSettingsUseCases(SettingsRepository settingsRepository) {
        return new SettingsUseCases(settingsRepository);
    }

}