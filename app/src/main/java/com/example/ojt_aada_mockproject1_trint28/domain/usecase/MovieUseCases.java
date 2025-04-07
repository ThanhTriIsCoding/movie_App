package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import androidx.paging.PagingData;
import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieDetailResponse;
import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class MovieUseCases {
    private final IMovieRepository movieRepository;

    @Inject
    public MovieUseCases(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey) {
        return movieRepository.getMovies(movieType, apiKey);
    }

    public Single<MovieDetailResponse> getMovieDetails(int movieId, String apiKey) {
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