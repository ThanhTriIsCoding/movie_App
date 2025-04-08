package com.example.ojt_aada_mockproject1_trint28.presentation.ui.moviedetails;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentMovieDetailBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.CastCrewAdapter;
import com.example.ojt_aada_mockproject1_trint28.presentation.worker.ReminderWorker;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MovieDetailFragment extends Fragment {

    private FragmentMovieDetailBinding binding;
    private MovieDetailViewModel viewModel;
    private CastCrewAdapter castCrewAdapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Movie movie;
    private String selectedDateTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = (Movie) getArguments().getSerializable("movie");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);

        // Set up the cast & crew RecyclerView
        castCrewAdapter = new CastCrewAdapter();
        binding.rvCastCrew.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCastCrew.setAdapter(castCrewAdapter);

        // Load movie details and cast & crew
        if (movie != null) {
            loadMovieDetails(movie.getId());
            loadCastCrew(movie.getId());
            setupInitialUI(movie);
        }

        // Set up star click listener
        binding.ivStar.setOnClickListener(v -> toggleFavorite());

        // Set up reminder button
        binding.btnReminder.setOnClickListener(v -> showDateTimePicker());
    }

    private void setupInitialUI(Movie movie) {
        binding.tvReleaseDate.setText(movie.getReleaseDate());
        binding.tvRatingNumber.setText(String.format(Locale.getDefault(), "%.1f/10.0", movie.getVoteAverage()));
        binding.tvOverview.setText(movie.getOverview());
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            Picasso.get()
                    .load(movie.getPosterUrl())
                    .resize(200, 300)
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(binding.imagePoster);
        } else {
            binding.imagePoster.setImageResource(R.drawable.ic_launcher_foreground);
        }
        updateStarIcon(movie.isLiked());
    }

    private void loadMovieDetails(int movieId) {
        disposables.add(
                viewModel.getMovieDetails(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                movieDetail -> {
                                    binding.tvReleaseDate.setText(movieDetail.getReleaseDate());
                                    binding.tvRatingNumber.setText(String.format(Locale.getDefault(), "%.1f/10.0", movieDetail.getVoteAverage()));
                                    binding.tvOverview.setText(movieDetail.getOverview());
                                    String posterUrl = "https://image.tmdb.org/t/p/w500" + movieDetail.getPosterPath();
                                    if (posterUrl != null && !posterUrl.isEmpty()) {
                                        Picasso.get()
                                                .load(posterUrl)
                                                .resize(200, 300)
                                                .centerCrop()
                                                .placeholder(R.drawable.ic_launcher_foreground)
                                                .error(R.drawable.ic_launcher_foreground)
                                                .into(binding.imagePoster);
                                    }
                                },
                                throwable -> {
                                    Log.e("MovieDetailFragment", "Error loading movie details: " + throwable.getMessage());
                                    Toast.makeText(getContext(), "Error loading movie details", Toast.LENGTH_SHORT).show();
                                }
                        )
        );
    }

    private void loadCastCrew(int movieId) {
        disposables.add(
                viewModel.getCastCrew(movieId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                pagingData -> castCrewAdapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData),
                                throwable -> {
                                    Log.e("MovieDetailFragment", "Error loading cast & crew: " + throwable.getMessage());
                                    Toast.makeText(getContext(), "Error loading cast & crew", Toast.LENGTH_SHORT).show();
                                }
                        )
        );
    }

    private void toggleFavorite() {
        disposables.add(
                viewModel.isMovieLiked(movie.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isLiked -> {
                                    if (isLiked) {
                                        disposables.add(
                                                viewModel.removeFavoriteMovie(movie)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                () -> {
                                                                    movie.setLiked(false);
                                                                    updateStarIcon(false);
                                                                    Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                                                                },
                                                                throwable -> {
                                                                    Log.e("MovieDetailFragment", "Error removing favorite: " + throwable.getMessage());
                                                                    Toast.makeText(getContext(), "Error removing favorite", Toast.LENGTH_SHORT).show();
                                                                }
                                                        )
                                        );
                                    } else {
                                        disposables.add(
                                                viewModel.addFavoriteMovie(movie)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                () -> {
                                                                    movie.setLiked(true);
                                                                    updateStarIcon(true);
                                                                    Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                                                                },
                                                                throwable -> {
                                                                    Log.e("MovieDetailFragment", "Error adding favorite: " + throwable.getMessage());
                                                                    Toast.makeText(getContext(), "Error adding favorite", Toast.LENGTH_SHORT).show();
                                                                }
                                                        )
                                        );
                                    }
                                },
                                throwable -> {
                                    Log.e("MovieDetailFragment", "Error checking isLiked: " + throwable.getMessage());
                                    Toast.makeText(getContext(), "Error checking favorite status", Toast.LENGTH_SHORT).show();
                                }
                        )
        );
    }

    private void updateStarIcon(boolean isLiked) {
        binding.ivStar.setImageResource(isLiked ? R.drawable.icons8_star_48 : R.drawable.icons8_star_50);
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                selectedDateTime = sdf.format(calendar.getTime());

                                try {
                                    Date reminderDate = sdf.parse(selectedDateTime);
                                    long delay = reminderDate.getTime() - System.currentTimeMillis();
                                    if (delay <= 0) {
                                        Toast.makeText(getContext(), "Cannot set reminder for a past time", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    disposables.add(
                                            viewModel.checkReminderConflict(movie.getId(), selectedDateTime)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(
                                                            reminders -> {
                                                                if (!reminders.isEmpty()) {
                                                                    Toast.makeText(getContext(), "A reminder for this movie at this time already exists!", Toast.LENGTH_SHORT).show();
                                                                } else {
                                                                    String posterUrl = "https://image.tmdb.org/t/p/w500" + movie.getPosterUrl();
                                                                    String releaseDate = movie.getReleaseDate();
                                                                    double voteAverage = movie.getVoteAverage();
                                                                    Reminder reminder = new Reminder(
                                                                            movie.getId(),
                                                                            movie.getTitle(),
                                                                            selectedDateTime,
                                                                            posterUrl,
                                                                            releaseDate,
                                                                            voteAverage
                                                                    );
                                                                    disposables.add(
                                                                            viewModel.addReminder(reminder)
                                                                                    .subscribeOn(Schedulers.io())
                                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                                    .subscribe(
                                                                                            () -> {
                                                                                                binding.tvReminderDateTime.setText("Reminder set for: " + selectedDateTime);
                                                                                                binding.tvReminderDateTime.setVisibility(View.VISIBLE);
                                                                                                Toast.makeText(getContext(), "Reminder set for " + selectedDateTime, Toast.LENGTH_SHORT).show();
                                                                                                scheduleReminder(reminder, calendar);
                                                                                            },
                                                                                            throwable -> {
                                                                                                Log.e("MovieDetailFragment", "Error setting reminder: " + throwable.getMessage());
                                                                                                Toast.makeText(getContext(), "Error setting reminder", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                    )
                                                                    );
                                                                }
                                                            },
                                                            throwable -> {
                                                                Log.e("MovieDetailFragment", "Error checking reminder conflict: " + throwable.getMessage());
                                                                Toast.makeText(getContext(), "Error checking reminder conflict", Toast.LENGTH_SHORT).show();
                                                            }
                                                    )
                                    );
                                } catch (ParseException e) {
                                    Log.e("MovieDetailFragment", "Error parsing selected date: " + e.getMessage());
                                    Toast.makeText(getContext(), "Error setting reminder", Toast.LENGTH_SHORT).show();
                                }
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void scheduleReminder(Reminder reminder, Calendar calendar) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date reminderDate = sdf.parse(reminder.getDateTime());
            long delay = reminderDate.getTime() - System.currentTimeMillis();

            if (delay > 0) {
                Data inputData = new Data.Builder()
                        .putString(ReminderWorker.KEY_MOVIE_TITLE, reminder.getTitle())
                        .putInt(ReminderWorker.KEY_MOVIE_ID, reminder.getMovieId())
                        .putString(ReminderWorker.KEY_DATE_TIME, reminder.getDateTime())
                        .build();

                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .setInputData(inputData)
                        .build();

                WorkManager.getInstance(requireContext())
                        .enqueueUniqueWork("reminder_" + reminder.getMovieId() + "_" + reminder.getDateTime(),
                                ExistingWorkPolicy.REPLACE, workRequest);
            } else {
                Toast.makeText(getContext(), "Cannot set reminder for a past time", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            Log.e("MovieDetailFragment", "Error parsing reminder date: " + e.getMessage());
            Toast.makeText(getContext(), "Error scheduling reminder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }
}