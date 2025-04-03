package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Settings;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.AddFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetFavoriteMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMoviesUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.IsMovieLikedUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.RemoveFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;

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
    private final GetMoviesUseCase getMoviesUseCase;
    private final UpdateSettingsUseCase updateSettingsUseCase;
    private final AddFavoriteMovieUseCase addFavoriteMovieUseCase;
    private final RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase;
    private final GetFavoriteMoviesUseCase getFavoriteMoviesUseCase;
    private final IsMovieLikedUseCase isMovieLikedUseCase;
    private final String apiKey;
    private Settings currentSettings;
    private final MutableLiveData<Integer> scrollPosition = new MutableLiveData<>(0);
    private final MutableLiveData<List<Movie>> favoriteMovies = new MutableLiveData<>();

    @Inject
    public MovieListViewModel(GetMoviesUseCase getMoviesUseCase, UpdateSettingsUseCase updateSettingsUseCase, @Named("apiKey") String apiKey,
                              AddFavoriteMovieUseCase addFavoriteMovieUseCase, RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase,
                              GetFavoriteMoviesUseCase getFavoriteMoviesUseCase, IsMovieLikedUseCase isMovieLikedUseCase) {
        this.getMoviesUseCase = getMoviesUseCase;
        this.updateSettingsUseCase = updateSettingsUseCase;
        this.apiKey = apiKey;
        this.addFavoriteMovieUseCase = addFavoriteMovieUseCase;
        this.removeFavoriteMovieUseCase = removeFavoriteMovieUseCase;
        this.getFavoriteMoviesUseCase = getFavoriteMoviesUseCase;
        this.isMovieLikedUseCase = isMovieLikedUseCase;
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

    public Flowable<List<Movie>> getFavoriteMovies() {
        return getFavoriteMoviesUseCase.execute(1);
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
        return addFavoriteMovieUseCase.execute(updatedMovie, 1);
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
        return removeFavoriteMovieUseCase.execute(updatedMovie, 1);
    }

    public Single<Boolean> isMovieLiked(int movieId) {
        return isMovieLikedUseCase.execute(movieId, 1);
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
}