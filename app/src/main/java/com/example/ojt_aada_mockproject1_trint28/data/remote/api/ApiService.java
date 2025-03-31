package com.example.ojt_aada_mockproject1_trint28.data.remote.api;

import com.example.ojt_aada_mockproject1_trint28.data.remote.model.ConfigurationResponse;
import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieListResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("configuration")
    Single<ConfigurationResponse> getConfiguration(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Single<MovieListResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/top_rated")
    Single<MovieListResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/upcoming")
    Single<MovieListResponse> getUpcomingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("movie/now_playing")
    Single<MovieListResponse> getNowPlayingMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );
}