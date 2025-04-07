package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.UpdateSettingsUseCase;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListFragment;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModel;
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
    private MovieListViewModel movieListViewModel;
    private NavController navController;
    private boolean shouldShowMoreIcon = false;

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

        // Set up ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Set up click listeners for toolbar icons
        binding.setOnGridClick(v -> viewModel.toggleDisplayMode());
        binding.setOnSearchClick(v -> {
            viewModel.setSearchViewVisible(true);
            viewModel.setSearchIconVisible(false);
            viewModel.setCloseIconVisible(true);
        });
        binding.setOnCloseClick(v -> {
            viewModel.clearSearch();
            // Tải lại danh sách phim yêu thích khi bấm nút "close"
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.movieListFragment) {
                // Sử dụng getCurrentBackStackEntry().getArguments() để lấy Bundle
                Bundle args = navController.getCurrentBackStackEntry().getArguments();
                String mode = args != null ? args.getString("mode", MovieListFragment.MODE_API) : MovieListFragment.MODE_API;
                if (mode.equals(MovieListFragment.MODE_FAVORITE)) {
                    movieListViewModel.loadFavoriteMovies();
                }
            }
        });

        // Theo dõi sự thay đổi của search_view
        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSearchQuery(s.toString());
            }
        });

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

        // Set up Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set up Navigation Component
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

        // Set up Navigation Drawer
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set up TabLayout
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setSelectedTabPosition(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        navController.navigate(R.id.movieListFragment);
                        break;
                    case 1:
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
                TabLayout.Tab tab = binding.tabLayout.getTabAt(position);
                if (tab != null && !tab.isSelected()) {
                    tab.select();
                }
            }
        });

        // Update toolbar based on destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Reset all toolbar elements to GONE by default
            viewModel.setGridIconVisible(false);
            viewModel.setSearchIconVisible(false);
            viewModel.setCloseIconVisible(false);
            viewModel.setSearchViewVisible(false);

            // Reset biến điều khiển menu
            shouldShowMoreIcon = false;

            // Tùy chỉnh icon điều hướng và menu dựa trên destination
            if (destination.getId() == R.id.movieListFragment) {
                String mode = arguments != null ? arguments.getString("mode", MovieListFragment.MODE_API) : MovieListFragment.MODE_API;
                if (mode.equals(MovieListFragment.MODE_API)) {
                    binding.toolbarTitle.setText("Movies");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    viewModel.setGridIconVisible(true);
                    shouldShowMoreIcon = true;
                } else if (mode.equals(MovieListFragment.MODE_FAVORITE)) {
                    binding.toolbarTitle.setText("Favourite");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    viewModel.setSearchIconVisible(!viewModel.getIsSearchViewVisible().getValue());
                    viewModel.setCloseIconVisible(viewModel.getIsSearchViewVisible().getValue());
                }
            } else if (destination.getId() == R.id.movieDetailsFragment) {
                if (arguments != null) {
                    Movie movie = (Movie) arguments.getSerializable("movie");
                    if (movie != null) {
                        binding.toolbarTitle.setText(movie.getTitle());
                    }
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else if (destination.getId() == R.id.showAllRemindersFragment) {
                binding.toolbarTitle.setText("All Reminders");
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else if (destination.getId() == R.id.settingsFragment) {
                binding.toolbarTitle.setText("Settings");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else if (destination.getId() == R.id.aboutFragment) {
                binding.toolbarTitle.setText("About");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // Cập nhật menu để hiển thị hoặc ẩn icon ic_more
            invalidateOptionsMenu();
        });

        // Theo dõi danh sách yêu thích để cập nhật badge
        movieListViewModel.getFavoriteMoviesLiveData().observe(this, favoriteMovies -> {
            TabLayout.Tab favoriteTab = binding.tabLayout.getTabAt(1);
            if (favoriteTab != null) {
                if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
                    favoriteTab.getOrCreateBadge().setNumber(favoriteMovies.size());
                    favoriteTab.getBadge().setVisible(true);
                } else {
                    favoriteTab.removeBadge();
                }
            }
        });

        // Load danh sách yêu thích ban đầu
        movieListViewModel.loadFavoriteMovies();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (shouldShowMoreIcon) {
            getMenuInflater().inflate(R.menu.movie_type_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (navController.getCurrentDestination() != null) {
                int currentDestinationId = navController.getCurrentDestination().getId();
                if (currentDestinationId == R.id.movieDetailsFragment) {
                    navController.navigateUp();
                } else if (currentDestinationId != R.id.showAllRemindersFragment) {
                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        binding.drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            }
            return true;
        }

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

        viewModel.setMovieType(movieType);
        if (selectedCategory != null) {
            sharedPreferences.edit().putString("category", selectedCategory).apply();
            updateSettingsUseCase.updateCategory(selectedCategory).subscribe();
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, binding.drawerLayout) || super.onSupportNavigateUp();
    }
}