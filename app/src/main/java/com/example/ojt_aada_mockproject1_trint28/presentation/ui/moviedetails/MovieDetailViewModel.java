package com.example.ojt_aada_mockproject1_trint28.presentation.ui.moviedetails;

import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieDetailResponse;
import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.AddFavoriteMovieUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.AddReminderUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetCastCrewUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetMovieDetailsUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.IsMovieLikedUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.RemoveFavoriteMovieUseCase;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import javax.inject.Named;

@HiltViewModel
public class MovieDetailViewModel extends androidx.lifecycle.ViewModel {
    private final GetMovieDetailsUseCase getMovieDetailsUseCase;
    private final GetCastCrewUseCase getCastCrewUseCase;
    private final AddFavoriteMovieUseCase addFavoriteMovieUseCase;
    private final RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase;
    private final IsMovieLikedUseCase isMovieLikedUseCase;
    private final AddReminderUseCase addReminderUseCase;
    private final String apiKey;

    @Inject
    public MovieDetailViewModel(
            GetMovieDetailsUseCase getMovieDetailsUseCase,
            GetCastCrewUseCase getCastCrewUseCase,
            AddFavoriteMovieUseCase addFavoriteMovieUseCase,
            RemoveFavoriteMovieUseCase removeFavoriteMovieUseCase,
            IsMovieLikedUseCase isMovieLikedUseCase,
            AddReminderUseCase addReminderUseCase,
            @Named("apiKey") String apiKey) {
        this.getMovieDetailsUseCase = getMovieDetailsUseCase;
        this.getCastCrewUseCase = getCastCrewUseCase;
        this.addFavoriteMovieUseCase = addFavoriteMovieUseCase;
        this.removeFavoriteMovieUseCase = removeFavoriteMovieUseCase;
        this.isMovieLikedUseCase = isMovieLikedUseCase;
        this.addReminderUseCase = addReminderUseCase;
        this.apiKey = apiKey;
    }

    public Single<MovieDetailResponse> getMovieDetails(int movieId) {
        return getMovieDetailsUseCase.execute(movieId, apiKey);
    }

    public Flowable<PagingData<CastCrew>> getCastCrew(int movieId) {
        return getCastCrewUseCase.execute(movieId, apiKey);
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

    public Completable addReminder(Reminder reminder) {
        return addReminderUseCase.execute(reminder);
    }

    public Flowable<List<Reminder>> checkReminderConflict(int movieId, String dateTime) {
        return addReminderUseCase.getReminderRepository().getRemindersByMovieAndTime(movieId, dateTime, 1);
    }

    // Expose ReminderRepository for conflict checking
    public ReminderRepository getReminderRepository() {
        return addReminderUseCase.getReminderRepository();
    }
}