package com.example.ojt_aada_mockproject1_trint28.data.repository;

import android.util.Log;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.ojt_aada_mockproject1_trint28.data.local.dao.MovieDao;
import com.example.ojt_aada_mockproject1_trint28.data.local.entity.MovieEntity;
import com.example.ojt_aada_mockproject1_trint28.data.paging.CastCrewPagingSource;
import com.example.ojt_aada_mockproject1_trint28.data.paging.MoviePagingSource;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieDetailResponse;
import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.SettingsUseCases;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class MovieRepository implements IMovieRepository {
    private final MovieDao movieDao;
    private final ApiService apiService;
    private final SettingsUseCases settingsUseCases; // Thay thế UpdateSettingsUseCase bằng SettingsUseCases
    private final String apiKey;
    private String imageBaseUrl = "https://image.tmdb.org/t/p/";
    private String posterSize = "w500";
    private String profileSize = "w185";

    public MovieRepository(ApiService apiService, SettingsUseCases settingsUseCases, String apiKey, MovieDao movieDao) {
        this.apiService = apiService;
        this.settingsUseCases = settingsUseCases;
        this.apiKey = apiKey;
        this.movieDao = movieDao;
    }

    @Override
    public Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey) {
        return settingsUseCases.getSettings() // Sử dụng settingsUseCases thay vì updateSettingsUseCase
                .flatMapPublisher(settings -> {
                    Pager<Integer, Movie> pager = new Pager<>(
                            new PagingConfig(
                                    20, // Page size
                                    5,  // Prefetch distance
                                    false // Enable placeholders
                            ),
                            () -> new MoviePagingSource(apiService, apiKey, movieType, settings, imageBaseUrl, posterSize, movieDao)
                    );

                    return PagingRx.getFlowable(pager);
                });
    }

    @Override
    public Flowable<PagingData<CastCrew>> getCastCrew(int movieId, String apiKey) {
        CastCrewPagingSource pagingSource = new CastCrewPagingSource(apiService, movieId, apiKey, imageBaseUrl, profileSize);
        Pager<Integer, CastCrew> pager = new Pager<>(
                new PagingConfig(
                        10, // Page size for cast & crew
                        5,  // Prefetch distance
                        false // Enable placeholders
                ),
                () -> pagingSource
        );
        return PagingRx.getFlowable(pager);
    }

    @Override
    public Single<MovieDetailResponse> getMovieDetails(int movieId, String apiKey) {
        return apiService.getMovieDetails(movieId, apiKey);
    }

    @Override
    public Flowable<List<Movie>> getFavoriteMovies(int userId) {
        return movieDao.getFavoriteMovies(userId)
                .map(entities -> {
                    List<Movie> movies = entities.stream()
                            .map(this::toDomainModel)
                            .collect(Collectors.toList());
                    Log.d("MovieRepository", "Fetched favorite movies for userId " + userId + ": " + movies.size());
                    return movies;
                });
    }

    @Override
    public Completable addFavoriteMovie(Movie movie, int userId) {
        MovieEntity entity = toEntity(movie, userId);
        return movieDao.insertFavoriteMovie(entity)
                .doOnComplete(() -> Log.d("MovieRepository", "Added movie to favorites: " + movie.getTitle() + " for userId " + userId));
    }

    @Override
    public Completable removeFavoriteMovie(Movie movie, int userId) {
        return movieDao.deleteFavoriteMovie(movie.getId(), userId)
                .doOnComplete(() -> Log.d("MovieRepository", "Removed movie from favorites: " + movie.getId() + " for userId " + userId));
    }

    @Override
    public Single<Boolean> isMovieLiked(int movieId, int userId) {
        return movieDao.isMovieLiked(movieId, userId)
                .map(count -> count > 0)
                .doOnSuccess(isLiked -> Log.d("MovieRepository", "Checked if movie " + movieId + " is liked for userId " + userId + ": " + isLiked));
    }

    private MovieEntity toEntity(Movie movie, int userId) {
        return new MovieEntity(
                movie.getId(),
                userId,
                movie.getTitle(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getVoteAverage(),
                movie.isAdult(),
                movie.getPosterUrl(),
                movie.isLiked()
        );
    }

    private Movie toDomainModel(MovieEntity entity) {
        return new Movie(
                entity.getId(),
                entity.getTitle(),
                entity.getOverview(),
                entity.getReleaseDate(),
                entity.getVoteAverage(),
                entity.isAdult(),
                entity.getPosterUrl(),
                entity.isLiked()
        );
    }
}