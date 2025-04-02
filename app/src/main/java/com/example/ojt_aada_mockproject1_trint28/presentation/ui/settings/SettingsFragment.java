package com.example.ojt_aada_mockproject1_trint28.presentation.ui.settings;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentCategoryBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentRatingFilterBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentReleaseYearBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentSortBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainViewModel;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModel;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModelFactory;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences sharedPreferences;
    private MovieListViewModel movieListViewModel;
    private MainViewModel mainViewModel;

    @Inject
    MovieListViewModelFactory movieListViewModelFactory;

    @Inject
    UpdateSettingsUseCase updateSettingsUseCase;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Initialize ViewModels
        movieListViewModel = new ViewModelProvider(this, movieListViewModelFactory).get(MovieListViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Observe movieType changes from MainViewModel to update category
        mainViewModel.getMovieType().observe(this, movieType -> {
            String selectedCategory;
            switch (movieType) {
                case "popular":
                    selectedCategory = "Popular Movies";
                    break;
                case "top_rated":
                    selectedCategory = "Top Rated Movies";
                    break;
                case "upcoming":
                    selectedCategory = "Upcoming Movies";
                    break;
                case "now_playing":
                    selectedCategory = "Now Playing Movies";
                    break;
                default:
                    selectedCategory = "Popular Movies";
                    break;
            }
            // Update SharedPreferences and UpdateSettingsUseCase to ensure consistency
            sharedPreferences.edit().putString("category", selectedCategory).apply();
            updateSettingsUseCase.updateCategory(selectedCategory).subscribe();
            updatePreferenceSummary(findPreference("category"));
        });

        // Setup preferences
        setupPreference(findPreference("category"), R.layout.fragment_category);
        setupPreference(findPreference("rate"), R.layout.fragment_rating_filter);
        setupPreference(findPreference("release_year"), R.layout.fragment_release_year);
        setupPreference(findPreference("sort_by"), R.layout.fragment_sort);

        // Setup reset button
        Preference resetPreference = findPreference("reset_settings");
        if (resetPreference != null) {
            resetPreference.setOnPreferenceClickListener(p -> {
                resetSettings();
                return true;
            });
        }
    }

    private void setupPreference(Preference preference, int layoutResId) {
        if (preference == null) return;

        preference.setOnPreferenceClickListener(p -> {
            showDialog(layoutResId, p);
            return true;
        });

        updatePreferenceSummary(preference);
    }

    private void showDialog(int layoutResId, Preference preference) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(layoutResId, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Gán sự kiện đóng dialog khi bấm Cancel hoặc No
        View cancelBtn = dialogView.findViewById(R.id.tvCancel);
        if (cancelBtn != null) cancelBtn.setOnClickListener(v -> dialog.dismiss());

        View noBtn = dialogView.findViewById(R.id.tvNo);
        if (noBtn != null) noBtn.setOnClickListener(v -> dialog.dismiss());

        if (layoutResId == R.layout.fragment_category) {
            handleCategoryDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_rating_filter) {
            handleRatingDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_release_year) {
            handleReleaseYearDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_sort) {
            handleSortDialog(dialogView, dialog, preference);
        }
    }

    private void handleCategoryDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentCategoryBinding binding = FragmentCategoryBinding.bind(dialogView);

        // Map the current category to the corresponding RadioButton
        String currentCategory = sharedPreferences.getString(preference.getKey(), "Popular Movies");
        switch (currentCategory) {
            case "Popular Movies":
                binding.radioGroupCategory.check(R.id.rb_popular);
                break;
            case "Top Rated Movies":
                binding.radioGroupCategory.check(R.id.rb_top_rated);
                break;
            case "Upcoming Movies":
                binding.radioGroupCategory.check(R.id.rb_upcoming);
                break;
            case "Now Playing Movies":
                binding.radioGroupCategory.check(R.id.rb_now_playing);
                break;
        }

        binding.radioGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedCategory;
            String movieType;
            if (checkedId == R.id.rb_popular) {
                selectedCategory = "Popular Movies";
                movieType = "popular";
            } else if (checkedId == R.id.rb_top_rated) {
                selectedCategory = "Top Rated Movies";
                movieType = "top_rated";
            } else if (checkedId == R.id.rb_upcoming) {
                selectedCategory = "Upcoming Movies";
                movieType = "upcoming";
            } else if (checkedId == R.id.rb_now_playing) {
                selectedCategory = "Now Playing Movies";
                movieType = "now_playing";
            } else {
                return; // No selection, do nothing
            }

            // Update SharedPreferences and settings
            sharedPreferences.edit().putString(preference.getKey(), selectedCategory).apply();
            updateSettingsUseCase.updateCategory(selectedCategory).subscribe();
            updatePreferenceSummary(preference);

            // Update the movie type in MainViewModel to refresh the movie list
            mainViewModel.setMovieType(movieType);
            refreshMovieList();
            dialog.dismiss();
        });
    }

    private void handleRatingDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentRatingFilterBinding binding = FragmentRatingFilterBinding.bind(dialogView);
        binding.seekBarRating.setProgress(sharedPreferences.getInt(preference.getKey(), 5));

        binding.tvYes.setOnClickListener(v -> {
            int rating = binding.seekBarRating.getProgress();
            sharedPreferences.edit().putInt(preference.getKey(), rating).apply();
            updateSettingsUseCase.updateRating(rating).subscribe();
            updatePreferenceSummary(preference);
            refreshMovieList();
            dialog.dismiss();
        });
    }

    private void handleReleaseYearDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentReleaseYearBinding binding = FragmentReleaseYearBinding.bind(dialogView);
        binding.etReleaseYear.setText(sharedPreferences.getString(preference.getKey(), "2015"));

        binding.tvOk.setOnClickListener(v -> {
            String year = binding.etReleaseYear.getText().toString().trim();
            if (!year.isEmpty()) {
                sharedPreferences.edit().putString(preference.getKey(), year).apply();
                updateSettingsUseCase.updateReleaseYear(Integer.parseInt(year)).subscribe();
                updatePreferenceSummary(preference);
                refreshMovieList();
            }
            dialog.dismiss();
        });
    }

    private void handleSortDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentSortBinding binding = FragmentSortBinding.bind(dialogView);
        int selectedId = sharedPreferences.getInt(preference.getKey(), R.id.rb_release_date);
        binding.radioGroupSort.check(selectedId);

        binding.tvOk.setOnClickListener(v -> {
            int selectedSortOption = binding.radioGroupSort.getCheckedRadioButtonId();
            if (selectedSortOption != -1) {
                sharedPreferences.edit().putInt(preference.getKey(), selectedSortOption).apply();
                updateSettingsUseCase.updateSortBy(selectedSortOption == R.id.rb_release_date ? "release_date" : "rating").subscribe();
                updatePreferenceSummary(preference);
                refreshMovieList();
            }
            dialog.dismiss();
        });
    }

    private void updatePreferenceSummary(Preference preference) {
        if (preference == null) return;

        String key = preference.getKey();
        switch (key) {
            case "category":
                preference.setSummary(sharedPreferences.getString(key, "Popular Movies"));
                break;
            case "rate":
                preference.setSummary(String.valueOf(sharedPreferences.getInt(key, 5)));
                break;
            case "release_year":
                preference.setSummary(sharedPreferences.getString(key, "2015"));
                break;
            case "sort_by":
                int sortOption = sharedPreferences.getInt(key, R.id.rb_release_date);
                preference.setSummary(sortOption == R.id.rb_release_date ? "Release Date" : "Rating");
                break;
        }
    }

    private void resetSettings() {
        sharedPreferences.edit()
                .putString("category", "Popular Movies")
                .putInt("rate", 5)
                .putString("release_year", "2015")
                .putInt("sort_by", R.id.rb_release_date)
                .apply();

        updateSettingsUseCase.resetSettings().subscribe();
        updatePreferenceSummary(findPreference("category"));
        updatePreferenceSummary(findPreference("rate"));
        updatePreferenceSummary(findPreference("release_year"));
        updatePreferenceSummary(findPreference("sort_by"));
        refreshMovieList();
    }

    private void refreshMovieList() {
        String currentMovieType = mainViewModel.getMovieType().getValue();
        if (currentMovieType != null) {
            movieListViewModel.getMovies(currentMovieType);
        }
    }
}