package com.example.domain.module;

import android.content.Context;

import com.example.domain.repository.IMovieRepository;
import com.example.domain.repository.IProfileRepository;
import com.example.domain.repository.IReminderRepository;
import com.example.domain.repository.SettingsRepository;
import com.example.domain.usecase.MovieUseCases;
import com.example.domain.usecase.ProfileUseCases;
import com.example.domain.usecase.ReminderUseCases;
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
public abstract class AppModule {

    @Provides
    @Singleton
    @Named("apiKey")
    static String provideApiKey() {
        return "e7631ffcb8e766993e5ec0c1f4245f93";
    }

    // Các phương thức cung cấp interface từ domain.repository
    @Provides
    @Singleton
    static MovieUseCases provideMovieUseCases(IMovieRepository movieRepository) {
        return new MovieUseCases(movieRepository);
    }

    @Provides
    @Singleton
    static ReminderUseCases provideReminderUseCases(IReminderRepository reminderRepository) {
        return new ReminderUseCases(reminderRepository);
    }

    @Provides
    @Singleton
    static ProfileUseCases provideProfileUseCases(IProfileRepository profileRepository) {
        return new ProfileUseCases(profileRepository);
    }

    @Provides
    @Singleton
    static SettingsUseCases provideSettingsUseCases(SettingsRepository settingsRepository) {
        return new SettingsUseCases(settingsRepository);
    }
}