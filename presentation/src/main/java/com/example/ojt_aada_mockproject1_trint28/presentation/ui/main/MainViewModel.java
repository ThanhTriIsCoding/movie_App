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
    private final MutableLiveData<Boolean> isGridMode = new MutableLiveData<>(false); // Chế độ mặc định là danh sách
    private final MutableLiveData<String> movieType = new MutableLiveData<>("popular"); // Loại phim mặc định là "popular"
    private final MutableLiveData<Integer> selectedTabPosition = new MutableLiveData<>(0); // Vị trí tab được chọn mặc định là 0 (tab đầu tiên)
    private final MutableLiveData<Boolean> isGridIconVisible = new MutableLiveData<>(true); // Biểu tượng lưới
    private final MutableLiveData<Boolean> isSearchIconVisible = new MutableLiveData<>(false); // Biểu tượng tìm kiếm
    private final MutableLiveData<Boolean> isCloseIconVisible = new MutableLiveData<>(false); // Biểu tượng đóng
    private final MutableLiveData<Boolean> isSearchViewVisible = new MutableLiveData<>(false); // Khung tìm kiếm
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>(""); // Truy vấn tìm kiếm mặc định là rỗng
    private final MutableLiveData<Integer> scrollPositionApi = new MutableLiveData<>(0); // Vị trí cuộn tab movieList, mặc định là 0
    private final MutableLiveData<Integer> scrollPositionFavorite = new MutableLiveData<>(0); // Vị trí cuộn movieListFavourite, mặc định là 0
    private final MutableLiveData<Integer> pageIndexApi = new MutableLiveData<>(1); // Chỉ số trang mặc định là 1
    private final MutableLiveData<Integer> pageIndexFavorite = new MutableLiveData<>(1);  // Chỉ số trang mặc định là 1
    private final MutableLiveData<List<Movie>> loadedMoviesApi = new MutableLiveData<>(new ArrayList<>()); // Lưu trữ danh sách phim đã tải về từ API
    private final MutableLiveData<List<Movie>> loadedMoviesFavorite = new MutableLiveData<>(new ArrayList<>()); // Lưu trữ danh sách phim yêu thích
    private final MutableLiveData<Boolean> shouldResetPosition = new MutableLiveData<>(false); // Biến này sẽ được sử dụng để xác định xem có nên đặt lại vị trí cuộn hay không
    private final MutableLiveData<String> lastMovieTypeApi = new MutableLiveData<>("popular"); // Loại phim cuối cùng được chọn từ API
    private final MutableLiveData<String> lastMovieTypeFavorite = new MutableLiveData<>("favorite"); // Loại phim cuối cùng được chọn từ danh sách yêu thích
    private final MutableLiveData<Movie> lastSelectedMovieApi = new MutableLiveData<>(); // Lưu trữ phim cuối cùng được chọn từ API
    private final MutableLiveData<Movie> lastSelectedMovieFavorite = new MutableLiveData<>();  // Lưu trữ phim cuối cùng được chọn từ danh sách yêu thích

    // Thêm hai biến để theo dõi trạng thái isInMovieDetail riêng biệt cho từng tab
    private final MutableLiveData<Boolean> isInMovieDetailApi = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isInMovieDetailFavorite = new MutableLiveData<>(false);

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

    public LiveData<Boolean> getShouldResetPosition() {
        return shouldResetPosition;
    }

    public void setShouldResetPosition(boolean reset) {
        shouldResetPosition.setValue(reset);
    }

    // Xóa phương thức getIsInMovieDetail và setIsInMovieDetail cũ
    // Thay bằng các phương thức riêng biệt cho từng tab
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