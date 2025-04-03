package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import io.reactivex.rxjava3.core.Single;

public class IsMovieLikedUseCase {
    private final IMovieRepository movieRepository;

    public IsMovieLikedUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Single<Boolean> execute(int movieId, int userId) {
        return movieRepository.isMovieLiked(movieId, userId);
    }
}