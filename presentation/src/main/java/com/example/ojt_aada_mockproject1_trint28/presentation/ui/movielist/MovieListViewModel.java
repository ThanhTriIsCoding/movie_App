package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.example.domain.model.Movie;
import com.example.domain.model.Settings;
import com.example.domain.usecase.MovieUseCases;
import com.example.domain.usecase.SettingsUseCases;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Named;

@HiltViewModel
public class MovieListViewModel extends ViewModel {
    private final MovieUseCases movieUseCases;
    private final SettingsUseCases settingsUseCases;
    private final String apiKey;
    private Settings currentSettings;
    private final MutableLiveData<Integer> scrollPosition = new MutableLiveData<>(0);
    private final MutableLiveData<List<Movie>> favoriteMovies = new MutableLiveData<>();
    private final MutableLiveData<Movie> navigateToMovieDetail = new MutableLiveData<>();
    private final MutableLiveData<Integer> notifyItemChangedPosition = new MutableLiveData<>();

    @Inject
    public MovieListViewModel(
            MovieUseCases movieUseCases,
            SettingsUseCases settingsUseCases,
            @Named("apiKey") String apiKey) {
        this.movieUseCases = movieUseCases;
        this.settingsUseCases = settingsUseCases;
        this.apiKey = apiKey;
        loadSettings();
    }

    private void loadSettings() {
        settingsUseCases.getSettings()
                .subscribeOn(Schedulers.io())
                .subscribe(settings -> currentSettings = settings)
                .dispose();
    }

    public Flowable<PagingData<Movie>> getMovies(String movieType) {
        return movieUseCases.getMovies(movieType, apiKey);
    }

    public Flowable<List<Movie>> getFavoriteMovies() {
        return movieUseCases.getFavoriteMovies(1);
    }

    public Completable addFavoriteMovie(Movie movie) {
        Movie updatedMovie = new Movie(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getVoteAverage(),
                movie.isAdult(),
                movie.getPosterUrl(),
                true
        );
        return movieUseCases.addFavoriteMovie(updatedMovie, 1);
    }

    public Completable removeFavoriteMovie(Movie movie) {
        Movie updatedMovie = new Movie(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getReleaseDate(),
                movie.getVoteAverage(),
                movie.isAdult(),
                movie.getPosterUrl(),
                false
        );
        return movieUseCases.removeFavoriteMovie(updatedMovie, 1);
    }

    public Single<Boolean> isMovieLiked(int movieId) {
        return movieUseCases.isMovieLiked(movieId, 1);
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

    public LiveData<List<Movie>> getFavoriteMoviesLiveData() {
        return favoriteMovies;
    }

    @SuppressLint("CheckResult")
    public void loadFavoriteMovies() {
        getFavoriteMovies()
                .subscribeOn(Schedulers.io())
                .subscribe(movies -> {
                    Log.d("MovieListViewModel", "Favorite movies loaded: " + movies.size());
                    favoriteMovies.postValue(movies);
                }, throwable -> {
                    Log.e("MovieListViewModel", "Error loading favorite movies: " + throwable.getMessage());
                    favoriteMovies.postValue(null);
                });
    }

    public void onMovieClicked(Movie movie) {
        navigateToMovieDetail.setValue(movie);
    }

    @SuppressLint("CheckResult")
    public void onStarClicked(Movie movie, int position) {
        isMovieLiked(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(isLiked -> {
                    if (isLiked) {
                        removeFavoriteMovie(movie)
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> {
                                    Log.d("MovieListViewModel", "Movie removed from favorites: " + movie.getTitle());
                                    movie.setLiked(false);
                                    notifyItemChangedPosition.postValue(position);
                                    if (isFavoriteMode()) {
                                        loadFavoriteMovies();
                                    }
                                }, throwable -> {
                                    Log.e("MovieListViewModel", "Error removing movie: " + throwable.getMessage());
                                });
                    } else {
                        addFavoriteMovie(movie)
                                .subscribeOn(Schedulers.io())
                                .subscribe(() -> {
                                    Log.d("MovieListViewModel", "Movie added to favorites: " + movie.getTitle());
                                    movie.setLiked(true);
                                    notifyItemChangedPosition.postValue(position);
                                    if (isFavoriteMode()) {
                                        loadFavoriteMovies();
                                    }
                                }, throwable -> {
                                    Log.e("MovieListViewModel", "Error adding movie: " + throwable.getMessage());
                                });
                    }
                }, throwable -> {
                    Log.e("MovieListViewModel", "Error checking isLiked: " + throwable.getMessage());
                });
    }

    public LiveData<Movie> getNavigateToMovieDetail() {
        return navigateToMovieDetail;
    }

    public LiveData<Integer> getNotifyItemChangedPosition() {
        return notifyItemChangedPosition;
    }

    // Thêm phương thức để reset navigateToMovieDetail
    public void resetNavigateToMovieDetail() {
        navigateToMovieDetail.setValue(null);
    }

    private String mode;

    public void setMode(String mode) {
        this.mode = mode;
    }

    private boolean isFavoriteMode() {
        return MovieListFragment.MODE_FAVORITE.equals(mode);
    }
}