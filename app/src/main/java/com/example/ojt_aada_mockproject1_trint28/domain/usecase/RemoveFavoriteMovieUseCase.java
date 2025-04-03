package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import io.reactivex.rxjava3.core.Completable;

public class RemoveFavoriteMovieUseCase {
    private final IMovieRepository movieRepository;

    public RemoveFavoriteMovieUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Completable execute(Movie movie, int userId) {
        return movieRepository.removeFavoriteMovie(movie, userId);
    }
}