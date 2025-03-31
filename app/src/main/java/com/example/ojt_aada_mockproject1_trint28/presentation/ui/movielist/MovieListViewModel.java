package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;

import javax.inject.Inject;

@HiltViewModel
public class MovieListViewModel extends ViewModel {
    private final GetMoviesUseCase getMoviesUseCase;
    private final String apiKey;

    @Inject
    public MovieListViewModel(GetMoviesUseCase getMoviesUseCase, String apiKey) {
        this.getMoviesUseCase = getMoviesUseCase;
        this.apiKey = apiKey;
    }

    public Flowable<PagingData<Movie>> getMovies(String movieType) {
        return getMoviesUseCase.execute(movieType, apiKey);
    }
}