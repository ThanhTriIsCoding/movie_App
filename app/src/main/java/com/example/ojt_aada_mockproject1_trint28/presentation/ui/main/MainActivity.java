package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private NavController navController;
    private String movieType = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Thiết lập Toolbar trước khi làm việc với Navigation Component
        setSupportActionBar(binding.toolbar);

        // Thiết lập Navigation Component
        // Sử dụng NavHostFragment để lấy NavController
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
        NavigationView navigationView = binding.navView;
        NavigationUI.setupWithNavController(navigationView, navController);

        // Thiết lập TabLayout
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        navController.navigate(R.id.movieListFragment);
                        break;
                    case 1:
                        navController.navigate(R.id.favoriteFragment);
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

        // Cập nhật tiêu đề ActionBar
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.movieListFragment) {
                getSupportActionBar().setTitle(movieType.replace("_", " ").toUpperCase());
            } else if (destination.getId() == R.id.favoriteFragment) {
                getSupportActionBar().setTitle("Favourite");
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

        if (item.getItemId() == R.id.menu_popular) {
            movieType = "popular";
        } else if (item.getItemId() == R.id.menu_top_rated) {
            movieType = "top_rated";
        } else if (item.getItemId() == R.id.menu_upcoming) {
            movieType = "upcoming";
        } else if (item.getItemId() == R.id.menu_now_playing) {
            movieType = "now_playing";
        } else {
            return super.onOptionsItemSelected(item);
        }

        getSupportActionBar().setTitle(movieType.replace("_", " ").toUpperCase());
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}