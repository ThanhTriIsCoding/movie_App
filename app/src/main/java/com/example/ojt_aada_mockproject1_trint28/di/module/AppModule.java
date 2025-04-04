package com.example.ojt_aada_mockproject1_trint28.di.module;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.ojt_aada_mockproject1_trint28.data.local.dao.MovieDao;
import com.example.ojt_aada_mockproject1_trint28.data.local.database.AppDatabase;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.RetrofitClient;
import com.example.ojt_aada_mockproject1_trint28.data.repository.MovieRepository;
import com.example.ojt_aada_mockproject1_trint28.data.repository.SettingsRepositoryImpl;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.SettingsRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.AddFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetCastCrewUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetFavoriteMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMovieDetailsUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.IsMovieLikedUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.RemoveFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModelFactory;

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
                .fallbackToDestructiveMigration() // This will delete the database and recreate it
                .build();
    }

    @Provides
    @Singleton
    MovieDao provideMovieDao(AppDatabase appDatabase) {
        return appDatabase.movieDao();
    }

    @Provides
    @Singleton
    MovieRepository provideMovieRepository(ApiService apiService, UpdateSettingsUseCase updateSettingsUseCase, @Named("apiKey") String apiKey, MovieDao movieDao) {
        return new MovieRepository(apiService, updateSettingsUseCase, apiKey, movieDao);
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
    GetMovieDetailsUseCase provideGetMovieDetailsUseCase(IMovieRepository movieRepository) {
        return new GetMovieDetailsUseCase(movieRepository);
    }

    @Provides
    @Singleton
    GetCastCrewUseCase provideGetCastCrewUseCase(IMovieRepository movieRepository) {
        return new GetCastCrewUseCase(movieRepository);
    }

    @Provides
    @Singleton
    AddFavoriteMovieUseCase provideAddFavoriteMovieUseCase(IMovieRepository movieRepository) {
        return new AddFavoriteMovieUseCase(movieRepository);
    }

    @Provides
    @Singleton
    RemoveFavoriteMovieUseCase provideRemoveFavoriteMovieUseCase(IMovieRepository movieRepository) {
        return new RemoveFavoriteMovieUseCase(movieRepository);
    }

    @Provides
    @Singleton
    GetFavoriteMoviesUseCase provideGetFavoriteMoviesUseCase(IMovieRepository movieRepository) {
        return new GetFavoriteMoviesUseCase(movieRepository);
    }

    @Provides
    @Singleton
    IsMovieLikedUseCase provideIsMovieLikedUseCase(IMovieRepository movieRepository) {
        return new IsMovieLikedUseCase(movieRepository);
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
    public MovieListViewModelFactory provideMovieListViewModelFactory(
            GetMoviesUseCase getMoviesUseCase,
            UpdateSettingsUseCase updateSettingsUseCase,
            @Named("apiKey") String apiKey,
            AddFavoriteMovieUseCase addFavoriteMovieUseCase,
            RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase,
            GetFavoriteMoviesUseCase getFavoriteMoviesUseCase,
            IsMovieLikedUseCase isMovieLikedUseCase) {
        return new MovieListViewModelFactory(getMoviesUseCase, updateSettingsUseCase, apiKey, addFavoriteMovieUseCase, removeFavoriteMovieUseCase, getFavoriteMoviesUseCase, isMovieLikedUseCase);
    }
}