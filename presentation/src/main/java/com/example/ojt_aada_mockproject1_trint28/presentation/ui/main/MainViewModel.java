package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainViewModel extends ViewModel {

    // Biến lưu trạng thái hiển thị danh sách hoặc lưới
    private final MutableLiveData<Boolean> isGridMode = new MutableLiveData<>(false);

    // Biến lưu loại phim hiện tại (popular, top-rated, v.v.)
    private final MutableLiveData<String> movieType = new MutableLiveData<>("popular");

    // Biến lưu tab đang được chọn
    private final MutableLiveData<Integer> selectedTabPosition = new MutableLiveData<>(0);

    // Biến lưu trạng thái hiển thị các biểu tượng trên thanh công cụ
    private final MutableLiveData<Boolean> isGridIconVisible = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isSearchIconVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isCloseIconVisible = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSearchViewVisible = new MutableLiveData<>(false);

    // Biến lưu truy vấn tìm kiếm
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    // Biến lưu vị trí cuộn của danh sách phim
    private final MutableLiveData<Integer> scrollPositionApi = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> scrollPositionFavorite = new MutableLiveData<>(0);

    // Biến lưu chỉ số trang hiện tại
    private final MutableLiveData<Integer> pageIndexApi = new MutableLiveData<>(1);
    private final MutableLiveData<Integer> pageIndexFavorite = new MutableLiveData<>(1);

    // Biến lưu danh sách phim đã tải
    private final MutableLiveData<List<Movie>> loadedMoviesApi = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Movie>> loadedMoviesFavorite = new MutableLiveData<>(new ArrayList<>());

    // Biến kiểm tra xem có cần reset vị trí cuộn không
    private final MutableLiveData<Boolean> shouldResetPosition = new MutableLiveData<>(false);

    // Biến lưu loại phim cuối cùng được chọn
    private final MutableLiveData<String> lastMovieTypeApi = new MutableLiveData<>("popular");
    private final MutableLiveData<String> lastMovieTypeFavorite = new MutableLiveData<>("favorite");

    // Biến lưu phim cuối cùng được chọn
    private final MutableLiveData<Movie> lastSelectedMovieApi = new MutableLiveData<>();
    private final MutableLiveData<Movie> lastSelectedMovieFavorite = new MutableLiveData<>();

    // Biến lưu trạng thái đang ở chi tiết phim
    private final MutableLiveData<Boolean> isInMovieDetailApi = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isInMovieDetailFavorite = new MutableLiveData<>(false);

    @Inject
    public MainViewModel() {
        movieType.setValue("popular");
        selectedTabPosition.setValue(0);
    }

    // Trạng thái hiển thị lưới hoặc danh sách
    public LiveData<Boolean> getIsGridMode() {
        return isGridMode;
    }

    public void setGridMode(boolean gridMode) {
        isGridMode.setValue(gridMode);
    }

    public void toggleDisplayMode() {
        Boolean currentMode = isGridMode.getValue();
        if (currentMode != null) {
            isGridMode.setValue(!currentMode); // Chuyển đổi giữa lưới và danh sách
        }
    }

    // Loại phim hiện tại
    public LiveData<String> getMovieType() {
        return movieType;
    }

    public void setMovieType(String type) {
        movieType.setValue(type);
    }

    // Tab đang được chọn
    public LiveData<Integer> getSelectedTabPosition() {
        return selectedTabPosition;
    }

    public void setSelectedTabPosition(int position) {
        selectedTabPosition.setValue(position);
    }

    // Biểu tượng lưới
    public LiveData<Boolean> getIsGridIconVisible() {
        return isGridIconVisible;
    }

    public void setGridIconVisible(boolean visible) {
        isGridIconVisible.setValue(visible);
    }

    // Biểu tượng tìm kiếm
    public LiveData<Boolean> getIsSearchIconVisible() {
        return isSearchIconVisible;
    }

    public void setSearchIconVisible(boolean visible) {
        isSearchIconVisible.setValue(visible);
    }

    // Biểu tượng đóng
    public LiveData<Boolean> getIsCloseIconVisible() {
        return isCloseIconVisible;
    }

    public void setCloseIconVisible(boolean visible) {
        isCloseIconVisible.setValue(visible);
    }

    // Thanh tìm kiếm
    public LiveData<Boolean> getIsSearchViewVisible() {
        return isSearchViewVisible;
    }

    public void setSearchViewVisible(boolean visible) {
        isSearchViewVisible.setValue(visible);
    }

    // Truy vấn tìm kiếm
    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void clearSearch() {
        searchQuery.setValue(""); // Xóa nội dung tìm kiếm
        setSearchViewVisible(false); // Ẩn thanh tìm kiếm
        setSearchIconVisible(true); // Hiển thị biểu tượng tìm kiếm
        setCloseIconVisible(false); // Ẩn biểu tượng đóng
    }

    // Vị trí cuộn
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

    // Chỉ số trang
    public LiveData<Integer> getPageIndex(String mode) {
        return mode.equals(MovieListFragment.MODE_API) ? pageIndexApi : pageIndexFavorite;
    }

    public void setPageIndex(String mode, int pageIndex) {
        if (mode.equals(MovieListFragment.MODE_API)) {
            pageIndexApi.setValue(pageIndex);
        } else {
            pageIndexFavorite.setValue(pageIndex);
        }
    }

    // Danh sách phim đã tải
    public LiveData<List<Movie>> getLoadedMovies(String mode) {
        return mode.equals(MovieListFragment.MODE_API) ? loadedMoviesApi : loadedMoviesFavorite;
    }

    public void setLoadedMovies(String mode, List<Movie> movies) {
        if (movies == null) {
            movies = new ArrayList<>();
        }
        if (mode.equals(MovieListFragment.MODE_API)) {
            loadedMoviesApi.setValue(movies);
        } else {
            loadedMoviesFavorite.setValue(movies);
        }
    }

    // Trạng thái reset vị trí
    public LiveData<Boolean> getShouldResetPosition() {
        return shouldResetPosition;
    }

    public void setShouldResetPosition(boolean reset) {
        shouldResetPosition.setValue(reset);
    }

    // Trạng thái chi tiết phim
    public LiveData<Boolean> getIsInMovieDetail(String mode) {
        return mode.equals(MovieListFragment.MODE_API) ? isInMovieDetailApi : isInMovieDetailFavorite;
    }

    public void setIsInMovieDetail(String mode, boolean isInDetail) {
        if (mode.equals(MovieListFragment.MODE_API)) {
            isInMovieDetailApi.setValue(isInDetail);
        } else {
            isInMovieDetailFavorite.setValue(isInDetail);
        }
    }

    // Loại phim cuối cùng được chọn
    public LiveData<String> getLastMovieType(String mode) {
        return mode.equals(MovieListFragment.MODE_API) ? lastMovieTypeApi : lastMovieTypeFavorite;
    }

    public void setLastMovieType(String mode, String movieType) {
        if (mode.equals(MovieListFragment.MODE_API)) {
            lastMovieTypeApi.setValue(movieType);
        } else {
            lastMovieTypeFavorite.setValue(movieType);
        }
    }

    // Phim cuối cùng được chọn
    public LiveData<Movie> getLastSelectedMovie(String mode) {
        return mode.equals(MovieListFragment.MODE_API) ? lastSelectedMovieApi : lastSelectedMovieFavorite;
    }

    public void setLastSelectedMovie(String mode, Movie movie) {
        if (mode.equals(MovieListFragment.MODE_API)) {
            lastSelectedMovieApi.setValue(movie);
        } else {
            lastSelectedMovieFavorite.setValue(movie);
        }
    }

    public void clearLastSelectedMovie(String mode) {
        if (mode.equals(MovieListFragment.MODE_API)) {
            lastSelectedMovieApi.setValue(null);
        } else {
            lastSelectedMovieFavorite.setValue(null);
        }
    }
}