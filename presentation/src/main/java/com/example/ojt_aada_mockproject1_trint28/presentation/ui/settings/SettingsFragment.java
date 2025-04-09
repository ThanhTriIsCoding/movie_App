package com.example.ojt_aada_mockproject1_trint28.presentation.ui.settings;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentCategoryBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentPagesPerLoadBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentRatingFilterBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentReleaseYearBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentSortBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainViewModel;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences sharedPreferences;
    private MovieListViewModel movieListViewModel;
    private MainViewModel mainViewModel;
    private SettingsViewModel settingsViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Initialize ViewModels
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class); // Hilt sẽ tự động inject
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

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
            settingsViewModel.setCategory(selectedCategory);
            updatePreferenceSummary(findPreference("category"));
        });

        // Observe settings changes from SettingsViewModel to update UI
        settingsViewModel.getCategory().observe(this, category -> updatePreferenceSummary(findPreference("category")));
        settingsViewModel.getRating().observe(this, rating -> updatePreferenceSummary(findPreference("rate")));
        settingsViewModel.getReleaseYear().observe(this, year -> updatePreferenceSummary(findPreference("release_year")));
        settingsViewModel.getSortBy().observe(this, sortBy -> updatePreferenceSummary(findPreference("sort_by")));
        settingsViewModel.getPagesPerLoad().observe(this, pages -> updatePreferenceSummary(findPreference("pages_per_load")));

        // Setup preferences
        setupPreference(findPreference("category"), R.layout.fragment_category);
        setupPreference(findPreference("rate"), R.layout.fragment_rating_filter);
        setupPreference(findPreference("release_year"), R.layout.fragment_release_year);
        setupPreference(findPreference("sort_by"), R.layout.fragment_sort);
        setupPreference(findPreference("pages_per_load"), R.layout.fragment_pages_per_load); // New preference

        // Setup reset button
        Preference resetPreference = findPreference("reset_settings");
        if (resetPreference != null) {
            resetPreference.setOnPreferenceClickListener(p -> {
                settingsViewModel.resetSettings();
                refreshMovieList();
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

        if (layoutResId == R.layout.fragment_category) {
            handleCategoryDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_rating_filter) {
            handleRatingDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_release_year) {
            handleReleaseYearDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_sort) {
            handleSortDialog(dialogView, dialog, preference);
        } else if (layoutResId == R.layout.fragment_pages_per_load) {
            handlePagesPerLoadDialog(dialogView, dialog, preference);
        }
    }

    private void handleCategoryDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentCategoryBinding binding = FragmentCategoryBinding.bind(dialogView);
        binding.setViewModel(settingsViewModel);
        binding.setDialog(dialog);
        binding.setLifecycleOwner(this);
    }

    private void handleRatingDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentRatingFilterBinding binding = FragmentRatingFilterBinding.bind(dialogView);
        binding.setViewModel(settingsViewModel);
        binding.setDialog(dialog);
        binding.setLifecycleOwner(this);
    }

    private void handleReleaseYearDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentReleaseYearBinding binding = FragmentReleaseYearBinding.bind(dialogView);
        binding.setViewModel(settingsViewModel);
        binding.setDialog(dialog);
        binding.setLifecycleOwner(this);
    }

    private void handleSortDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentSortBinding binding = FragmentSortBinding.bind(dialogView);
        binding.setViewModel(settingsViewModel);
        binding.setDialog(dialog);
        binding.setLifecycleOwner(this);
    }

    private void handlePagesPerLoadDialog(View dialogView, AlertDialog dialog, Preference preference) {
        FragmentPagesPerLoadBinding binding = FragmentPagesPerLoadBinding.bind(dialogView);
        binding.setViewModel(settingsViewModel);
        binding.setDialog(dialog);
        binding.setLifecycleOwner(this);
    }

    private void updatePreferenceSummary(Preference preference) {
        if (preference == null) return;

        String key = preference.getKey();
        switch (key) {
            case "category":
                preference.setSummary(settingsViewModel.getCategory().getValue());
                break;
            case "rate":
                preference.setSummary(String.valueOf(settingsViewModel.getRating().getValue()));
                break;
            case "release_year":
                preference.setSummary(String.valueOf(settingsViewModel.getReleaseYear().getValue()));
                break;
            case "sort_by":
                String sortBy = settingsViewModel.getSortBy().getValue();
                preference.setSummary(sortBy != null && sortBy.equals("release_date") ? "Release Date" : "Rating");
                break;
            case "pages_per_load":
                preference.setSummary(String.valueOf(settingsViewModel.getPagesPerLoad().getValue()));
                break;
        }
    }

    private void refreshMovieList() {
        String currentMovieType = mainViewModel.getMovieType().getValue();
        if (currentMovieType != null) {
            movieListViewModel.getMovies(currentMovieType);
        }
    }
}