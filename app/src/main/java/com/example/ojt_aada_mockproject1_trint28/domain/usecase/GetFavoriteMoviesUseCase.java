package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

public class GetFavoriteMoviesUseCase {
    private final IMovieRepository movieRepository;

    public GetFavoriteMoviesUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flowable<List<Movie>> execute(int userId) {
        return movieRepository.getFavoriteMovies(userId);
    }
}