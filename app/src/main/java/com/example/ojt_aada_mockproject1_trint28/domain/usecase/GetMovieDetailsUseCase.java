package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieDetailResponse;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import io.reactivex.rxjava3.core.Single;

public class GetMovieDetailsUseCase {
    private final IMovieRepository movieRepository;

    public GetMovieDetailsUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Single<MovieDetailResponse> execute(int movieId, String apiKey) {
        return movieRepository.getMovieDetails(movieId, apiKey);
    }
}