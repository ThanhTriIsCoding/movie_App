package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;
public class MainViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isGridMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> movieType = new MutableLiveData<>("popular");

    @Inject
    public MainViewModel() {
        // Set default movie type to "popular"
        movieType.setValue("popular");
    }

    public LiveData<Boolean> getIsGridMode() {
        return isGridMode;
    }

    public void setGridMode(boolean gridMode) {
        isGridMode.setValue(gridMode);
    }

    public void toggleDisplayMode() {
        Boolean currentMode = isGridMode.getValue();
        if (currentMode != null) {
            isGridMode.setValue(!currentMode);
        }
    }

    public LiveData<String> getMovieType() {
        return movieType;
    }

    public void setMovieType(String type) {
        movieType.setValue(type);
    }
}