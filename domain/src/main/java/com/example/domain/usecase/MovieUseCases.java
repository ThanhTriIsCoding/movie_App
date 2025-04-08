package com.example.domain.usecase;

import androidx.paging.PagingData;
import com.example.domain.model.CastCrew;
import com.example.domain.model.Movie;
import com.example.domain.model.MovieDetails;
import com.example.domain.repository.IMovieRepository;

import javax.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public class MovieUseCases {
    private final IMovieRepository movieRepository;

    @Inject
    public MovieUseCases(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey) {
        return movieRepository.getMovies(movieType, apiKey);
    }

    public Single<MovieDetails> getMovieDetails(int movieId, String apiKey) {
        return movieRepository.getMovieDetails(movieId, apiKey);
    }

    public Flowable<PagingData<CastCrew>> getCastCrew(int movieId, String apiKey) {
        return movieRepository.getCastCrew(movieId, apiKey);
    }

    public Completable addFavoriteMovie(Movie movie, int userId) {
        return movieRepository.addFavoriteMovie(movie, userId);
    }

    public Completable removeFavoriteMovie(Movie movie, int userId) {
        return movieRepository.removeFavoriteMovie(movie, userId);
    }

    public Flowable<List<Movie>> getFavoriteMovies(int userId) {
        return movieRepository.getFavoriteMovies(userId);
    }

    public Single<Boolean> isMovieLiked(int movieId, int userId) {
        return movieRepository.isMovieLiked(movieId, userId);
    }
}