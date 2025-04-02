package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;

public class MovieListViewModelFactory implements ViewModelProvider.Factory {
    private final GetMoviesUseCase getMoviesUseCase;
    private final UpdateSettingsUseCase updateSettingsUseCase;
    private final String apiKey;

    public MovieListViewModelFactory(GetMoviesUseCase getMoviesUseCase, UpdateSettingsUseCase updateSettingsUseCase, String apiKey) {
        this.getMoviesUseCase = getMoviesUseCase;
        this.updateSettingsUseCase = updateSettingsUseCase;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MovieListViewModel.class)) {
            return (T) new MovieListViewModel(getMoviesUseCase, updateSettingsUseCase, apiKey);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}