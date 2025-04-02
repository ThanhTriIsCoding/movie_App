package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isGridMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> movieType = new MutableLiveData<>("popular");
    private final MutableLiveData<Integer> selectedTabPosition = new MutableLiveData<>(0); // Default to 0 (first tab)

    @Inject
    public MainViewModel() {
        // Set default movie type to "popular"
        movieType.setValue("popular");
        // Set default selected tab position to 0
        selectedTabPosition.setValue(0);
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

    public LiveData<Integer> getSelectedTabPosition() {
        return selectedTabPosition;
    }

    public void setSelectedTabPosition(int position) {
        selectedTabPosition.setValue(position);
    }
}