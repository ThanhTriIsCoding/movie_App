package com.example.ojt_aada_mockproject1_trint28.data.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.example.ojt_aada_mockproject1_trint28.data.remote.api.ApiService;
import com.example.ojt_aada_mockproject1_trint28.data.remote.model.CastCrewResponse;
import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CastCrewPagingSource extends RxPagingSource<Integer, CastCrew> {
    private final ApiService apiService;
    private final int movieId;
    private final String apiKey;
    private final String imageBaseUrl;
    private final String profileSize;

    public CastCrewPagingSource(ApiService apiService, int movieId, String apiKey, String imageBaseUrl, String profileSize) {
        this.apiService = apiService;
        this.movieId = movieId;
        this.apiKey = apiKey;
        this.imageBaseUrl = imageBaseUrl != null ? imageBaseUrl : "https://image.tmdb.org/t/p/";
        this.profileSize = profileSize != null ? profileSize : "w185";
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, CastCrew>> loadSingle(@NonNull LoadParams<Integer> params) {
        int page = params.getKey() != null ? params.getKey() : 1;
        int pageSize = params.getLoadSize();

        return apiService.getCastCrew(movieId, apiKey)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    List<CastCrew> castCrewList = new ArrayList<>();

                    // Add cast members
                    for (CastCrewResponse.Cast cast : response.getCast()) {
                        String profileUrl = cast.getProfilePath() != null ? imageBaseUrl + profileSize + cast.getProfilePath() : null;
                        castCrewList.add(new CastCrew(cast.getId(), cast.getName(), cast.getCharacter(), profileUrl));
                    }

                    // Add crew members
                    for (CastCrewResponse.Crew crew : response.getCrew()) {
                        String profileUrl = crew.getProfilePath() != null ? imageBaseUrl + profileSize + crew.getProfilePath() : null;
                        castCrewList.add(new CastCrew(crew.getId(), crew.getName(), crew.getJob(), profileUrl));
                    }

                    // Simulate paging by slicing the list
                    int start = (page - 1) * pageSize;
                    int end = Math.min(start + pageSize, castCrewList.size());
                    List<CastCrew> pageItems = start < castCrewList.size() ? castCrewList.subList(start, end) : new ArrayList<>();

                    Integer nextKey = end < castCrewList.size() ? page + 1 : null;
                    return (LoadResult<Integer, CastCrew>) new LoadResult.Page<>(pageItems, page == 1 ? null : page - 1, nextKey);
                })
                .onErrorResumeNext(throwable -> Single.just(new LoadResult.Error<>(throwable)));
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, CastCrew> state) {
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, CastCrew> anchorPage = state.closestPageToPosition(anchorPosition);
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