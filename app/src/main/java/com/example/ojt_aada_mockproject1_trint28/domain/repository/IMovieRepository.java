package com.example.ojt_aada_mockproject1_trint28.domain.repository;

import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface IMovieRepository {
    Flowable<PagingData<Movie>> getMovies(String movieType, String apiKey);
}
