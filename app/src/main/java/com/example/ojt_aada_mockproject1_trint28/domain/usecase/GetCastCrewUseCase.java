package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;
import com.example.ojt_aada_mockproject1_trint28.domain.repository.IMovieRepository;

import io.reactivex.rxjava3.core.Flowable;

public class GetCastCrewUseCase {
    private final IMovieRepository movieRepository;

    public GetCastCrewUseCase(IMovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Flowable<PagingData<CastCrew>> execute(int movieId, String apiKey) {
        return movieRepository.getCastCrew(movieId, apiKey);
    }
}