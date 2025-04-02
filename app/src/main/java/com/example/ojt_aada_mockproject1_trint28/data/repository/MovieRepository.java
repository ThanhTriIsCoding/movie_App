package com.example.ojt_aada_mockproject1_trint28.data.repository;

import android.annotation.SuppressLint;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;

import com.example.ojt_aada_mockproject1_trint28.data.paging.MoviePagingSource;
import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MovieRepository implements IMovieRepository {
    private final ApiService apiService;
    private final UpdateSettingsUseCase updateSettingsUseCase;
    private String imageBaseUrl = "https://image.tmdb.org/t/p/";
    private String posterSize = "w1000";

    @SuppressLint("CheckResult")
    public MovieRepository(ApiService apiService, UpdateSettingsUseCase updateSettingsUseCase, String apiKey) {
        this.apiService = apiService;
        this.updateSettingsUseCase = updateSettingsUseCase;
        // Lấy configuration và lưu trữ
        apiService.getConfiguration(apiKey)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        config -> {
                            imageBaseUrl = config.getImages().getSecureBaseUrl();
                            posterSize = config.getImages().getPosterSizes()[3];
                        },
                        throwable -> {
                            // Nếu lỗi, giữ giá trị mặc định
                        }
                );
    }

    @Override
    public Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey) {
        // Đảm bảo imageBaseUrl đã được lấy trước khi gọi API
        return updateSettingsUseCase.getSettings()
                .subscribeOn(Schedulers.io())
                .flatMapPublisher(settings -> apiService.getConfiguration(apiKey)
                        .subscribeOn(Schedulers.io())
                        .map(config -> {
                            imageBaseUrl = config.getImages().getSecureBaseUrl();
                            posterSize = config.getImages().getPosterSizes()[3];
                            return true;
                        })
                        .onErrorReturn(throwable -> true) // Nếu lỗi, giữ giá trị mặc định
                        .flatMapPublisher(ignored -> {
                            Pager<Integer, Movie> pager = new Pager<>(
                                    new PagingConfig(
                                            20, // pageSize
                                            20, // prefetchDistance
                                            false, // enablePlaceholders
                                            20 // initialLoadSize
                                    ),
                                    () -> new MoviePagingSource(apiService, apiKey, movieType, settings, imageBaseUrl, posterSize)
                            );
                            return PagingRx.getFlowable(pager);
                        }));
    }
}