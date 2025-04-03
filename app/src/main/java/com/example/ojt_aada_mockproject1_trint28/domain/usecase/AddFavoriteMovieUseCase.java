package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import io.reactivex.rxjava3.core.Completable;

public class AddFavoriteMovieUseCase {
    private final IMovieRepository movieRepository;

    public AddFavoriteMovieUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Completable execute(Movie movie, int userId) {
        return movieRepository.addFavoriteMovie(movie, userId);
    }
}