package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import io.reactivex.rxjava3.core.Flowable;

public class GetMoviesUseCase {
    private final IMovieRepository movieRepository;

    public GetMoviesUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flowable<PagingData<Movie>> execute(String movieType, String apiKey) {
        return movieRepository.getMovies(movieType, apiKey);
    }
}