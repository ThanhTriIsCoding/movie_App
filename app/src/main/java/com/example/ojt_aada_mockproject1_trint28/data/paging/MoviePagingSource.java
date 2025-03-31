package com.example.ojt_aada_mockproject1_trint28.data.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieListResponse;
import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieResponse;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoviePagingSource extends RxPagingSource<Integer, Movie> {

    private final ApiService apiService;
    private final String apiKey;
    private final String movieType;
    private String imageBaseUrl = "https://image.tmdb.org/t/p/"; // Giá trị mặc định
    private String posterSize = "w500"; // Giá trị mặc định

    public MoviePagingSource(ApiService apiService, String apiKey, String movieType, String imageBaseUrl, String posterSize) {
        this.apiService = apiService;
        this.apiKey = apiKey;
        this.movieType = movieType;
        this.imageBaseUrl = imageBaseUrl != null ? imageBaseUrl : this.imageBaseUrl;
        this.posterSize = posterSize != null ? posterSize : this.posterSize;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(@NonNull LoadParams<Integer> params) {
        int page = params.getKey() != null ? params.getKey() : 1;

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

        return request
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    Integer nextKey = (page < response.getTotalPages()) ? page + 1 : null;
                    return (LoadResult<Integer, Movie>) new LoadResult.Page<>(
                            response.getResults().stream()
                                    .map(this::mapToDomain)
                                    .collect(java.util.stream.Collectors.toList()),
                            page == 1 ? null : page - 1, // prevKey
                            nextKey // nextKey
                    );
                })
                .onErrorResumeNext(throwable ->
                        Single.just(new LoadResult.Error<Integer, Movie>(throwable))
                );
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