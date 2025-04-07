package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListFragment;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isGridMode = new MutableLiveData<>(false);
    private final MutableLiveData<String> movieType = new MutableLiveData<>("popular");
    private final MutableLiveData<Integer> selectedTabPosition = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isGridIconVisible = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isSearchIconVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isCloseIconVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSearchViewVisible = new MutableLiveData<>(false);
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Integer> scrollPositionApi = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> scrollPositionFavorite = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> shouldResetPosition = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isInMovieDetail = new MutableLiveData<>(false);

    @Inject
    public MainViewModel() {
        movieType.setValue("popular");
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

    public LiveData<Boolean> getIsGridIconVisible() {
        return isGridIconVisible;
    }

    public void setGridIconVisible(boolean visible) {
        isGridIconVisible.setValue(visible);
    }

    public LiveData<Boolean> getIsSearchIconVisible() {
        return isSearchIconVisible;
    }

    public void setSearchIconVisible(boolean visible) {
        isSearchIconVisible.setValue(visible);
    }

    public LiveData<Boolean> getIsCloseIconVisible() {
        return isCloseIconVisible;
    }

    public void setCloseIconVisible(boolean visible) {
        isCloseIconVisible.setValue(visible);
    }

    public LiveData<Boolean> getIsSearchViewVisible() {
        return isSearchViewVisible;
    }

    public void setSearchViewVisible(boolean visible) {
        isSearchViewVisible.setValue(visible);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void clearSearch() {
        searchQuery.setValue("");
        setSearchViewVisible(false);
        setSearchIconVisible(true);
        setCloseIconVisible(false);
    }

    public LiveData<Integer> getScrollPosition(String mode) {
        return mode.equals(MovieListFragment.MODE_API) ? scrollPositionApi : scrollPositionFavorite;
    }

    public void setScrollPosition(String mode, int position) {
        if (mode.equals(MovieListFragment.MODE_API)) {
            scrollPositionApi.setValue(position);
        } else {
            scrollPositionFavorite.setValue(position);
        }
    }

    public LiveData<Boolean> getShouldResetPosition() {
        return shouldResetPosition;
    }

    public void setShouldResetPosition(boolean reset) {
        shouldResetPosition.setValue(reset);
    }

    public LiveData<Boolean> getIsInMovieDetail() {
        return isInMovieDetail;
    }

    public void setIsInMovieDetail(boolean isInDetail) {
        isInMovieDetail.setValue(isInDetail);
    }
}