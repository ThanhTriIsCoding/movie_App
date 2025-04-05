package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.ActivityMainBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.NavHeaderBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListFragment;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile.EditProfileActivity;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile.ProfileViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private NavController navController;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    UpdateSettingsUseCase updateSettingsUseCase;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    // Permission denied, handle accordingly
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }

        // Thiết lập ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Bind ProfileViewModel to Navigation Drawer Header
        NavigationView navigationView = binding.navView;
        NavHeaderBinding headerBinding = NavHeaderBinding.bind(navigationView.getHeaderView(0));
        headerBinding.setViewModel(profileViewModel);
        headerBinding.setLifecycleOwner(this);

        // Add logging to confirm LiveData updates
        profileViewModel.reminders.observe(this, reminders -> {
            Log.d("MainActivity", "ProfileViewModel reminders updated: " + reminders.size() + " reminders");
        });

        // Observe navigation events from ProfileViewModel
        profileViewModel.navigateToEditProfile.observe(this, shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                profileViewModel.onNavigationHandled();
            }
        });

        profileViewModel.navigateToShowAllReminders.observe(this, shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {
                navController.navigate(R.id.action_global_showAllRemindersFragment);
                profileViewModel.onNavigationHandled();
            }
        });

        // Thiết lập Toolbar trước khi làm việc với Navigation Component
        setSupportActionBar(binding.toolbar);

        // Thiết lập Navigation Component
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) navHostFragment).getNavController();
        } else {
            throw new IllegalStateException("NavHostFragment not found!");
        }

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph())
                .setDrawerLayout(binding.drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Thiết lập Navigation Drawer
        NavigationUI.setupWithNavController(navigationView, navController);

        // Thiết lập TabLayout
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Update the selected tab position in ViewModel
                viewModel.setSelectedTabPosition(tab.getPosition());
                // Navigate to the corresponding fragment
                switch (tab.getPosition()) {
                    case 0:
                        navController.navigate(R.id.movieListFragment);
                        break;
                    case 1:
                        // Navigate to MovieListFragment with "favorite" mode
                        Bundle args = new Bundle();
                        args.putString("mode", MovieListFragment.MODE_FAVORITE);
                        navController.navigate(R.id.movieListFragment, args);
                        break;
                    case 2:
                        navController.navigate(R.id.settingsFragment);
                        break;
                    case 3:
                        navController.navigate(R.id.aboutFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Observe the selected tab position from ViewModel to restore it
        viewModel.getSelectedTabPosition().observe(this, position -> {
            if (position != null) {
                // Select the tab in TabLayout
                TabLayout.Tab tab = binding.tabLayout.getTabAt(position);
                if (tab != null && !tab.isSelected()) {
                    tab.select();
                    // The onTabSelected listener will handle navigation to the correct fragment
                }
            }
        });

        // In MainActivity, modify the observer
        profileViewModel.reminders.observe(this, reminders -> {
            Log.d("MainActivity", "ProfileViewModel reminders updated: " + reminders.size() + " reminders");
            // Invalidate the NavigationView to force a redraw
            binding.navView.invalidate();
        });

        // Cập nhật tiêu đề ActionBar
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.movieListFragment) {
                String mode = arguments != null ? arguments.getString("mode", MovieListFragment.MODE_API) : MovieListFragment.MODE_API;
                if (mode.equals(MovieListFragment.MODE_API)) {
                    String currentMovieType = viewModel.getMovieType().getValue();
                    if (currentMovieType != null) {
                        getSupportActionBar().setTitle(currentMovieType.replace("_", " ").toUpperCase());
                    }
                } else if (mode.equals(MovieListFragment.MODE_FAVORITE)) {
                    getSupportActionBar().setTitle("Favourite");
                }
            } else if (destination.getId() == R.id.settingsFragment) {
                getSupportActionBar().setTitle("Settings");
            } else if (destination.getId() == R.id.aboutFragment) {
                getSupportActionBar().setTitle("About");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_type_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }

        // Cập nhật movieType thông qua ViewModel và đồng bộ với SharedPreferences
        String movieType = null;
        String selectedCategory = null;
        if (item.getItemId() == R.id.menu_popular) {
            movieType = "popular";
            selectedCategory = "Popular Movies";
        } else if (item.getItemId() == R.id.menu_top_rated) {
            movieType = "top_rated";
            selectedCategory = "Top Rated Movies";
        } else if (item.getItemId() == R.id.menu_upcoming) {
            movieType = "upcoming";
            selectedCategory = "Upcoming Movies";
        } else if (item.getItemId() == R.id.menu_now_playing) {
            movieType = "now_playing";
            selectedCategory = "Now Playing Movies";
        } else {
            return super.onOptionsItemSelected(item);
        }

        // Update MainViewModel
        viewModel.setMovieType(movieType);

        // Update SharedPreferences and UpdateSettingsUseCase
        if (selectedCategory != null) {
            sharedPreferences.edit().putString("category", selectedCategory).apply();
            updateSettingsUseCase.updateCategory(selectedCategory).subscribe();
        }

        // Cập nhật tiêu đề ActionBar dựa trên movieType từ ViewModel
        String currentMovieType = viewModel.getMovieType().getValue();
        if (currentMovieType != null) {
            getSupportActionBar().setTitle(currentMovieType.replace("_", " ").toUpperCase());
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}