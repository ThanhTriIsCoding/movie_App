package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ojt_aada_mockproject1_trint28.domain.usecase.AddFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetFavoriteMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.IsMovieLikedUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.RemoveFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;

public class MovieListViewModelFactory implements ViewModelProvider.Factory {
    private final GetMoviesUseCase getMoviesUseCase;
    private final UpdateSettingsUseCase updateSettingsUseCase;
    private final AddFavoriteMovieUseCase addFavoriteMovieUseCase;
    private final RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase;
    private final GetFavoriteMoviesUseCase getFavoriteMoviesUseCase;
    private final IsMovieLikedUseCase isMovieLikedUseCase;
    private final String apiKey;

    public MovieListViewModelFactory(GetMoviesUseCase getMoviesUseCase, UpdateSettingsUseCase updateSettingsUseCase, String apiKey,
                                     AddFavoriteMovieUseCase addFavoriteMovieUseCase, RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase,
                                     GetFavoriteMoviesUseCase getFavoriteMoviesUseCase, IsMovieLikedUseCase isMovieLikedUseCase) {
        this.getMoviesUseCase = getMoviesUseCase;
        this.updateSettingsUseCase = updateSettingsUseCase;
        this.apiKey = apiKey;
        this.addFavoriteMovieUseCase = addFavoriteMovieUseCase;
        this.removeFavoriteMovieUseCase = removeFavoriteMovieUseCase;
        this.getFavoriteMoviesUseCase = getFavoriteMoviesUseCase;
        this.isMovieLikedUseCase = isMovieLikedUseCase;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieListViewModel.class)) {
            return (T) new MovieListViewModel(getMoviesUseCase, updateSettingsUseCase, apiKey, addFavoriteMovieUseCase, removeFavoriteMovieUseCase, getFavoriteMoviesUseCase, isMovieLikedUseCase);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}