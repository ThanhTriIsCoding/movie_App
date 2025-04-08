package com.example.domain.repository;

import androidx.paging.PagingData;


import com.example.domain.model.CastCrew;
import com.example.domain.model.Movie;
import com.example.domain.model.MovieDetails;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface IMovieRepository {
    Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey);
    Flowable<List<Movie>> getFavoriteMovies(int userId);
    Completable addFavoriteMovie(Movie movie, int userId);
    Completable removeFavoriteMovie(Movie movie, int userId);
    Single<Boolean> isMovieLiked(int movieId, int userId);
    Single<MovieDetails> getMovieDetails(int movieId, String apiKey);
    Flowable<PagingData<CastCrew>> getCastCrew(int movieId, String apiKey);
}