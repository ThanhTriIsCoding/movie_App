package com.example.data.paging;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;


import com.example.data.local.dao.MovieDao;
import com.example.data.remote.api.ApiService;
import com.example.data.remote.model.MovieListResponse;
import com.example.data.remote.model.MovieResponse;
import com.example.domain.model.Movie;
import com.example.domain.model.Settings;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, Movie> {

    private final ApiService apiService;
    private final String apiKey;
    private final String movieType;
    private final Settings settings;
    private final MovieDao movieDao;
    private String imageBaseUrl = "https://image.tmdb.org/t/p/";
    private String posterSize = "w500";

    public MoviePagingSource(ApiService apiService, String apiKey, String movieType, Settings settings, String imageBaseUrl, String posterSize, MovieDao movieDao) {
        this.apiService = apiService;
        this.apiKey = apiKey;
        this.movieType = movieType;
        this.settings = settings;
        this.movieDao = movieDao;
        this.imageBaseUrl = imageBaseUrl != null ? imageBaseUrl : this.imageBaseUrl;
        this.posterSize = posterSize != null ? posterSize : this.posterSize;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(@NonNull LoadParams<Integer> params) {
        int startPage = params.getKey() != null ? params.getKey() : 1;
        int pagesToLoad = settings.getPagesPerLoad(); // Use the setting
        int userId = 1;

        List<Single<MovieListResponse>> requests = new ArrayList<>();
        for (int i = 0; i < pagesToLoad; i++) {
            int page = startPage + i;
            Single<MovieListResponse> request;
            switch (movieType) {
                case "top_rated":
                    request = apiService.getTopRatedMovies(apiKey, page);
                    break;
                case "upcoming":
                    request = apiService.getUpcomingMovies(apiKey, page);
                    break;
                case "now_playing":
                    request = apiService.getNowPlayingMovies(apiKey, page);
                    break;
                case "popular":
                default:
                    request = apiService.getPopularMovies(apiKey, page);
                    break;
            }
            requests.add(request);
        }

        return Single.zip(requests, responses -> {
                    List<MovieResponse> allMovieResponses = new ArrayList<>();
                    for (Object response : responses) {
                        allMovieResponses.addAll(((MovieListResponse) response).getResults());
                    }
                    return allMovieResponses;
                })
                .subscribeOn(Schedulers.io())
                .flatMap(movieResponses -> {
                    List<Single<Movie>> movieSingles = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        movieSingles = movieResponses.stream()
                                .map(movieResponse -> {
                                    Movie movie = mapToDomain(movieResponse);
                                    return movieDao.isMovieLiked(movie.getId(), userId)
                                            .map(count -> new Movie(
                                                    movie.getId(),
                                                    movie.getTitle(),
                                                    movie.getOverview(),
                                                    movie.getReleaseDate(),
                                                    movie.getVoteAverage(),
                                                    movie.isAdult(),
                                                    movie.getPosterUrl(),
                                                    count > 0
                                            ));
                                })
                                .collect(Collectors.toList());
                    }

                    return Single.zip(movieSingles, objects -> {
                        List<Movie> movies = new ArrayList<>();
                        for (Object obj : objects) {
                            movies.add((Movie) obj);
                        }
                        return movies;
                    });
                })
                .map(movies -> {
                    List<Movie> filteredMovies = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        filteredMovies = movies.stream()
                                .filter(movie -> {
                                    boolean matchesRating = movie.getVoteAverage() >= settings.getMinRating();
                                    boolean matchesYear = true;
                                    try {
                                        String releaseDate = movie.getReleaseDate();
                                        if (releaseDate != null && !releaseDate.isEmpty()) {
                                            int year = Integer.parseInt(releaseDate.split("-")[0]);
                                            matchesYear = year >= settings.getReleaseYear();
                                        }
                                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                        matchesYear = false;
                                    }
                                    return matchesRating && matchesYear;
                                })
                                .sorted(settings.getSortBy().equals("rating") ?
                                        Comparator.comparing(Movie::getVoteAverage).reversed() :
                                        Comparator.comparing(Movie::getReleaseDate, Comparator.nullsLast(String::compareTo)).reversed())
                                .collect(Collectors.toList());
                    }

                    System.out.println("MoviePagingSource: Loaded " + movies.size() + " movies, filtered to " + filteredMovies.size() + " movies for pages " + startPage + " to " + (startPage + pagesToLoad - 1));
                    Integer nextKey = filteredMovies.isEmpty() ? null : startPage + pagesToLoad;
                    return new LoadResult.Page<>(
                            filteredMovies,
                            startPage == 1 ? null : startPage - 1,
                            nextKey
                    );
                });
    }


    private Movie mapToDomain(MovieResponse response) {
        String posterUrl = imageBaseUrl + posterSize + response.getPosterPath();
        return new Movie(
                response.getId(),
                response.getTitle(),
                response.getOverview(),
                response.getReleaseDate(),
                response.getVoteAverage(),
                response.isAdult(),
                posterUrl,
                false
        );
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Movie> state) {
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, Movie> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }

        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1;
        }

        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1;
        }

        return null;
    }
}