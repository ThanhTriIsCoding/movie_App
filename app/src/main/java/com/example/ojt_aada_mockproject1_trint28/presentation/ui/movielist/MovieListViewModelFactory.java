package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;

public class MovieListViewModelFactory implements ViewModelProvider.Factory {
    private final GetMoviesUseCase getMoviesUseCase;
    private final String apiKey;

    public MovieListViewModelFactory(GetMoviesUseCase getMoviesUseCase, String apiKey) {
        this.getMoviesUseCase = getMoviesUseCase;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieListViewModel.class)) {
            return (T) new MovieListViewModel(getMoviesUseCase, apiKey);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}