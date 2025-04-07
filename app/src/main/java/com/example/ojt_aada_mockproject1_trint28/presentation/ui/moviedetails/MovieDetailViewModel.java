package com.example.ojt_aada_mockproject1_trint28.presentation.ui.moviedetails;

import androidx.paging.PagingData;

import com.example.ojt_aada_mockproject1_trint28.data.remote.model.MovieDetailResponse;
import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.CastCrew;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.MovieUseCases;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.ReminderUseCases;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import javax.inject.Inject;
import javax.inject.Named;

@HiltViewModel
public class MovieDetailViewModel extends androidx.lifecycle.ViewModel {
    private final MovieUseCases movieUseCases;
    private final ReminderUseCases reminderUseCases;
    private final String apiKey;

    @Inject
    public MovieDetailViewModel(
            MovieUseCases movieUseCases,
            ReminderUseCases reminderUseCases,
            @Named("apiKey") String apiKey) {
        this.movieUseCases = movieUseCases;
        this.reminderUseCases = reminderUseCases;
        this.apiKey = apiKey;
    }

    public Single<MovieDetailResponse> getMovieDetails(int movieId) {
        return movieUseCases.getMovieDetails(movieId, apiKey);
    }

    public Flowable<PagingData<CastCrew>> getCastCrew(int movieId) {
        return movieUseCases.getCastCrew(movieId, apiKey);
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

    public Completable addReminder(Reminder reminder) {
        return reminderUseCases.addReminder(reminder);
    }

    public Flowable<List<Reminder>> checkReminderConflict(int movieId, String dateTime) {
        return reminderUseCases.getReminders()
                .map(reminders -> {
                    List<Reminder> conflictingReminders = new ArrayList<>();
                    for (Reminder reminder : reminders) {
                        if (reminder.getMovieId() == movieId && reminder.getDateTime().equals(dateTime)) {
                            conflictingReminders.add(reminder);
                        }
                    }
                    return conflictingReminders;
                });
    }

    // Expose ReminderRepository for conflict checking
    public ReminderRepository getReminderRepository() {
        // Note: Truy cập trực tiếp ReminderRepository từ ReminderUseCases không được khuyến nghị.
        // Thay vào đó, bạn nên thêm một phương thức trong ReminderUseCases để lấy reminders theo movieId và dateTime.
        throw new UnsupportedOperationException("Direct access to ReminderRepository is not supported. Use ReminderUseCases instead.");
    }
}