package com.example.data.repository;

import android.os.Build;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.data.local.dao.MovieDao;
import com.example.data.local.entity.MovieEntity;
import com.example.data.paging.CastCrewPagingSource;
import com.example.data.paging.MoviePagingSource;
import com.example.data.remote.api.ApiService;
import com.example.data.remote.model.MovieDetailResponse;
import com.example.domain.model.CastCrew;
import com.example.domain.model.Movie;
import com.example.domain.model.MovieDetails;
import com.example.domain.repository.IMovieRepository;
import com.example.domain.usecase.SettingsUseCases;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

public class MovieRepository implements IMovieRepository {
    private final MovieDao movieDao;
    private final ApiService apiService;
    private final SettingsUseCases settingsUseCases;
    private final String apiKey;
    private String imageBaseUrl = "https://image.tmdb.org/t/p/";
    private String posterSize = "w500";
    private String profileSize = "w185";

    public MovieRepository(ApiService apiService, SettingsUseCases settingsUseCases, @Named("apiKey") String apiKey, MovieDao movieDao) {
        this.apiService = apiService;
        this.settingsUseCases = settingsUseCases;
        this.apiKey = apiKey;
        this.movieDao = movieDao;
    }

    @Override
    public Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey) {
        return settingsUseCases.getSettings()
                .flatMapPublisher(settings -> {
                    Pager<Integer, Movie> pager = new Pager<>(
                            new PagingConfig(20, 5, false),
                            () -> new MoviePagingSource(apiService, apiKey, movieType, settings, imageBaseUrl, posterSize, movieDao)
                    );
                    return PagingRx.getFlowable(pager);
                });
    }

    @Override
    public Flowable<PagingData<CastCrew>> getCastCrew(int movieId, String apiKey) {
        CastCrewPagingSource pagingSource = new CastCrewPagingSource(apiService, movieId, apiKey, imageBaseUrl, profileSize);
        Pager<Integer, CastCrew> pager = new Pager<>(
                new PagingConfig(10, 5, false),
                () -> pagingSource
        );
        return PagingRx.getFlowable(pager);
    }

    @Override
    public Single<MovieDetails> getMovieDetails(int movieId, String apiKey) {
        return apiService.getMovieDetails(movieId, apiKey)
                .map(response -> new MovieDetails(
                        response.getId(),
                        response.getTitle(),
                        response.getOverview(),
                        response.getReleaseDate(),
                        response.getVoteAverage(),
                        response.isAdult(),
                        response.getPosterPath()
                ));
    }

    @Override
    public Flowable<List<Movie>> getFavoriteMovies(int userId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return movieDao.getFavoriteMovies(userId)
                    .map(entities -> entities.stream()
                            .map(this::toDomainModel)
                            .collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public Completable addFavoriteMovie(Movie movie, int userId) {
        MovieEntity entity = toEntity(movie, userId);
        return movieDao.insertFavoriteMovie(entity);
    }

    @Override
    public Completable removeFavoriteMovie(Movie movie, int userId) {
        return movieDao.deleteFavoriteMovie(movie.getId(), userId);
    }

    @Override
    public Single<Boolean> isMovieLiked(int movieId, int userId) {
        return movieDao.isMovieLiked(movieId, userId)
                .map(count -> count > 0);
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