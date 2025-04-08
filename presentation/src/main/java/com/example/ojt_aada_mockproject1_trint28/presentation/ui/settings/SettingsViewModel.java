package com.example.ojt_aada_mockproject1_trint28.presentation.ui.settings;

import android.app.AlertDialog;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.domain.usecase.SettingsUseCases;
import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private final SettingsUseCases settingsUseCases;
    private final SharedPreferences sharedPreferences;
    private final MainViewModel mainViewModel;

    private final MutableLiveData<String> category = new MutableLiveData<>();
    private final MutableLiveData<Integer> rating = new MutableLiveData<>();
    private final MutableLiveData<Integer> releaseYear = new MutableLiveData<>();
    private final MutableLiveData<String> sortBy = new MutableLiveData<>();
    private final MutableLiveData<Integer> seekBarRating = new MutableLiveData<>();
    private final MutableLiveData<String> releaseYearInput = new MutableLiveData<>();

    @Inject
    public SettingsViewModel(SettingsUseCases settingsUseCases, SharedPreferences sharedPreferences, MainViewModel mainViewModel) {
        this.settingsUseCases = settingsUseCases;
        this.sharedPreferences = sharedPreferences;
        this.mainViewModel = mainViewModel;
        loadSettings();
    }

    private void loadSettings() {
        category.setValue(sharedPreferences.getString("category", "Popular Movies"));
        rating.setValue(sharedPreferences.getInt("rate", 5));
        seekBarRating.setValue(sharedPreferences.getInt("rate", 5));
        releaseYear.setValue(Integer.parseInt(sharedPreferences.getString("release_year", "2015")));
        releaseYearInput.setValue(sharedPreferences.getString("release_year", "2015"));
        int sortOption = sharedPreferences.getInt("sort_by", R.id.rb_release_date);
        sortBy.setValue(sortOption == R.id.rb_release_date ? "release_date" : "rating");
    }

    public MutableLiveData<String> getCategory() {
        return category;
    }

    public MutableLiveData<Integer> getRating() {
        return rating;
    }

    public MutableLiveData<Integer> getReleaseYear() {
        return releaseYear;
    }

    public MutableLiveData<String> getSortBy() {
        return sortBy;
    }

    public MutableLiveData<Integer> getSeekBarRating() {
        return seekBarRating;
    }

    public MutableLiveData<String> getReleaseYearInput() {
        return releaseYearInput;
    }

    public void setCategory(String newCategory) {
        category.setValue(newCategory);
        sharedPreferences.edit().putString("category", newCategory).apply();
        settingsUseCases.updateCategory(newCategory).subscribe();
    }

    public void setRating(int newRating) {
        rating.setValue(newRating);
        seekBarRating.setValue(newRating);
        sharedPreferences.edit().putInt("rate", newRating).apply();
        settingsUseCases.updateRating(newRating).subscribe();
    }

    public void setReleaseYear(int newYear) {
        releaseYear.setValue(newYear);
        releaseYearInput.setValue(String.valueOf(newYear));
        sharedPreferences.edit().putString("release_year", String.valueOf(newYear)).apply();
        settingsUseCases.updateReleaseYear(newYear).subscribe();
    }

    public void setSortBy(String newSortBy) {
        sortBy.setValue(newSortBy);
        int sortOption = newSortBy.equals("release_date") ? R.id.rb_release_date : R.id.rb_rating;
        sharedPreferences.edit().putInt("sort_by", sortOption).apply();
        settingsUseCases.updateSortBy(newSortBy).subscribe();
    }

    public void resetSettings() {
        setCategory("Popular Movies");
        setRating(5);
        setReleaseYear(2015);
        setSortBy("release_date");
        settingsUseCases.resetSettings().subscribe();
    }

    public void onCategorySelected(int checkedId, AlertDialog dialog) {
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
            return;
        }

        setCategory(selectedCategory);
        mainViewModel.setMovieType(movieType);
        dialog.dismiss();
    }

    public void onRatingConfirmed(AlertDialog dialog) {
        Integer newRating = seekBarRating.getValue();
        if (newRating != null) {
            setRating(newRating);
        }
        dialog.dismiss();
    }

    public void onReleaseYearConfirmed(AlertDialog dialog) {
        String year = releaseYearInput.getValue();
        if (year != null && !year.isEmpty()) {
            try {
                int newYear = Integer.parseInt(year);
                setReleaseYear(newYear);
            } catch (NumberFormatException e) {
                // Handle invalid input if needed
            }
        }
        dialog.dismiss();
    }

    public void onSortConfirmed(int checkedId, AlertDialog dialog) {
        if (checkedId != -1) {
            String newSortBy = checkedId == R.id.rb_release_date ? "release_date" : "rating";
            setSortBy(newSortBy);
        }
        dialog.dismiss();
    }

    public void dismissDialog(AlertDialog dialog) {
        dialog.dismiss();
    }
}