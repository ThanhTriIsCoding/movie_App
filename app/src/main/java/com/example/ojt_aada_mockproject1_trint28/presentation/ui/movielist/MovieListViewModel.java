package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Inject;

@HiltViewModel
public class MovieListViewModel extends ViewModel {
    private final GetMoviesUseCase getMoviesUseCase;
    private final UpdateSettingsUseCase updateSettingsUseCase;
    private final String apiKey;
    private Settings currentSettings;
    private final MutableLiveData<Integer> scrollPosition = new MutableLiveData<>(0); // Default to 0

    @Inject
    public MovieListViewModel(GetMoviesUseCase getMoviesUseCase, UpdateSettingsUseCase updateSettingsUseCase, String apiKey) {
        this.getMoviesUseCase = getMoviesUseCase;
        this.updateSettingsUseCase = updateSettingsUseCase;
        this.apiKey = apiKey;
        loadSettings();
    }

    private void loadSettings() {
        updateSettingsUseCase.getSettings()
                .subscribeOn(Schedulers.io())
                .subscribe(settings -> currentSettings = settings)
                .dispose();
    }

    public Flowable<PagingData<Movie>> getMovies(String movieType) {
        return getMoviesUseCase.execute(movieType, apiKey);
    }

    public Settings getCurrentSettings() {
        return currentSettings;
    }

    public LiveData<Integer> getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int position) {
        scrollPosition.setValue(position);
    }
}