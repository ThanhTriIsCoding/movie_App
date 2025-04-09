package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.domain.model.Movie;
import com.example.domain.model.Reminder;
import com.example.domain.usecase.SettingsUseCases;
import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.ActivityMainBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.NavHeaderBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.ShowAllRemindersAdapter;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListFragment;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModel;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile.EditProfileActivity;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile.ProfileViewModel;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders.ShowAllRemindersViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ProfileViewModel profileViewModel;
    private MovieListViewModel movieListViewModel;
    private ShowAllRemindersViewModel remindersViewModel;
    private NavController navController;
    private boolean shouldShowMoreIcon = false;
    private ShowAllRemindersAdapter remindersAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    SettingsUseCases settingsUseCases;


    //Khởi tạo activity, thiết lập giao diện, ViewModel và điều hướng
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Khởi tạo các ViewModel để quản lý dữ liệu và trạng thái giao diện
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        remindersViewModel = new ViewModelProvider(this).get(ShowAllRemindersViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Thiết lập các sự kiện giao diện người dùng
        binding.setOnGridClick(v -> viewModel.toggleDisplayMode()); // Chuyển đổi giữa chế độ lưới và danh sách

        binding.setOnSearchClick(v -> {
            viewModel.setSearchViewVisible(true);
            viewModel.setSearchIconVisible(false);
            viewModel.setCloseIconVisible(true);
        });

        binding.setOnCloseClick(v -> {
            viewModel.clearSearch(); // Xóa nội dung tìm kiếm
            // Nếu đang ở MovieListFragment, kiểm tra mode và tải lại danh sách yêu thích
            if (navController.getCurrentDestination() != null &&
                    navController.getCurrentDestination().getId() == R.id.movieListFragment) {
                Bundle args = Objects.requireNonNull(navController.getCurrentBackStackEntry()).getArguments();
                String mode = args != null ? args.getString("mode", MovieListFragment.MODE_API) : MovieListFragment.MODE_API;
                if (mode.equals(MovieListFragment.MODE_FAVORITE)) {
                    movieListViewModel.loadFavoriteMovies();
                }
            }
        });

        // Theo dõi thay đổi văn bản trong thanh tìm kiếm
        binding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setSearchQuery(s.toString()); // Cập nhật truy vấn tìm kiếm trong ViewModel
            }
        });

        // Thiết lập Navigation Drawer
        NavigationView navigationView = binding.navView;
        NavHeaderBinding headerBinding = NavHeaderBinding.bind(navigationView.getHeaderView(0));
        headerBinding.setViewModel(profileViewModel);
        headerBinding.setLifecycleOwner(this);

        // Khởi tạo adapter cho danh sách lời nhắc trong drawer
        remindersAdapter = new ShowAllRemindersAdapter(remindersViewModel);
        remindersAdapter.setDisplayMode(false, false);
        headerBinding.rvReminders.setLayoutManager(new LinearLayoutManager(this));
        headerBinding.rvReminders.setAdapter(remindersAdapter);

        // Quan sát danh sách lời nhắc và hiển thị tối đa 2 mục trong drawer
        profileViewModel.reminders.observe(this, reminders -> {
            Log.d("MainActivity", "ProfileViewModel reminders updated: " + reminders.size() + " reminders");
            List<Reminder> limitedReminders = reminders.size() > 2 ? reminders.subList(0, 2) : reminders;
            remindersAdapter.setReminders(limitedReminders);
        });

        // Đăng ký receiver để xử lý xóa lời nhắc
        profileViewModel.registerReminderDeletedReceiver(this);

        // Chuyển hướng đến EditProfileActivity khi cần
        profileViewModel.navigateToEditProfile.observe(this, shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {
                Intent intent = new Intent(this, EditProfileActivity.class);
                startActivity(intent);
                profileViewModel.onNavigationHandled();
            }
        });

        // Chuyển hướng đến ShowAllRemindersFragment khi cần
        profileViewModel.navigateToShowAllReminders.observe(this, shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {
                navController.navigate(R.id.action_global_showAllRemindersFragment);
                profileViewModel.onNavigationHandled();
            }
        });

        // Thiết lập toolbar làm ActionBar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Tắt tiêu đề mặc định
        }

        // Khởi tạo NavController từ NavHostFragment
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment instanceof NavHostFragment) {
            navController = ((NavHostFragment) navHostFragment).getNavController();
        } else {
            throw new IllegalStateException("NavHostFragment not found!");
        }

        // Thiết lập cấu hình cho ActionBar và Drawer
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.movieListFragment, R.id.settingsFragment, R.id.aboutFragment)
                .setDrawerLayout(binding.drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Xử lý sự kiện chọn tab trong TabLayout
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewModel.setSelectedTabPosition(tab.getPosition());
                Bundle args;
                switch (tab.getPosition()) {
                    case 0: // Tab Movies
                        String modeApi = MovieListFragment.MODE_API;
                        handleTabSelection(modeApi);
                        break;
                    case 1: // Tab Favourite
                        String modeFavorite = MovieListFragment.MODE_FAVORITE;
                        handleTabSelection(modeFavorite);
                        break;
                    case 2: // Tab Settings
                        navController.navigate(R.id.settingsFragment);
                        break;
                    case 3: // Tab About
                        navController.navigate(R.id.aboutFragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Đồng bộ vị trí tab với ViewModel
        viewModel.getSelectedTabPosition().observe(this, position -> {
            if (position != null) {
                TabLayout.Tab tab = binding.tabLayout.getTabAt(position);
                if (tab != null && !tab.isSelected()) {
                    tab.select();
                }
            }
        });

        // Theo dõi thay đổi đích đến để cập nhật giao diện
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Log.d("MainActivity", "Destination changed to: " + destination.getId());
            // Đặt lại trạng thái các biểu tượng và thanh tìm kiếm
            viewModel.setGridIconVisible(false);
            viewModel.setSearchIconVisible(false);
            viewModel.setCloseIconVisible(false);
            viewModel.setSearchViewVisible(false);
            shouldShowMoreIcon = false;

            if (destination.getId() == R.id.movieListFragment) {
                String mode = arguments != null ? arguments.getString("mode", MovieListFragment.MODE_API) : MovieListFragment.MODE_API;
                Log.d("MainActivity", "MovieListFragment mode: " + mode);
                if (mode.equals(MovieListFragment.MODE_API)) {
                    binding.toolbarTitle.setText("Movies");
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                    viewModel.setGridIconVisible(true);
                    shouldShowMoreIcon = true;
                } else if (mode.equals(MovieListFragment.MODE_FAVORITE)) {
                    binding.toolbarTitle.setText("Favourite");
                    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                    viewModel.setSearchIconVisible(Boolean.FALSE.equals(viewModel.getIsSearchViewVisible().getValue()));
                    viewModel.setCloseIconVisible(Boolean.TRUE.equals(viewModel.getIsSearchViewVisible().getValue()));
                }
            } else if (destination.getId() == R.id.movieDetailsFragment) {
                if (arguments != null) {
                    Movie movie = (Movie) arguments.getSerializable("movie");
                    if (movie != null) {
                        binding.toolbarTitle.setText(movie.getTitle());
                        NavBackStackEntry previousEntry = navController.getPreviousBackStackEntry();
                        if (previousEntry != null && previousEntry.getDestination().getId() == R.id.movieListFragment) {
                            String mode = viewModel.getSelectedTabPosition().getValue() == 1 ? MovieListFragment.MODE_FAVORITE : MovieListFragment.MODE_API;
                            viewModel.setLastSelectedMovie(mode, movie);
                            viewModel.setIsInMovieDetail(mode, true);
                            Log.d("MainActivity", "Set isInMovieDetail for mode " + mode + " to true");
                        }
                    }
                }
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            } else if (destination.getId() == R.id.showAllRemindersFragment) {
                binding.toolbarTitle.setText("All Reminders");
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
            } else if (destination.getId() == R.id.settingsFragment) {
                binding.toolbarTitle.setText("Settings");
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            } else if (destination.getId() == R.id.aboutFragment) {
                binding.toolbarTitle.setText("About");
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            }

            // Cập nhật lại menu khi đích đến thay đổi
            invalidateOptionsMenu();
        });

        // Cập nhật badge cho tab Favourite dựa trên số lượng phim yêu thích
        movieListViewModel.getFavoriteMoviesLiveData().observe(this, favoriteMovies -> {
            TabLayout.Tab favoriteTab = binding.tabLayout.getTabAt(1);
            if (favoriteTab != null) {
                if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
                    favoriteTab.getOrCreateBadge().setNumber(favoriteMovies.size());
                    Objects.requireNonNull(favoriteTab.getBadge()).setVisible(true);
                } else {
                    favoriteTab.removeBadge();
                }
            }
        });

        // Tải danh sách phim yêu thích khi khởi tạo
        movieListViewModel.loadFavoriteMovies();
    }
    private void handleTabSelection(String mode) {
        Movie lastMovie = viewModel.getLastSelectedMovie(mode).getValue();
        Boolean isInMovieDetail = viewModel.getIsInMovieDetail(mode).getValue();

        Bundle args = new Bundle();
        if (isInMovieDetail != null && isInMovieDetail && lastMovie != null) {
            args.putSerializable("movie", lastMovie);
            navController.navigate(R.id.action_global_movieDetailsFragment, args);
        } else {
            args.putString("mode", mode);
            navigateToMovieList(args);
        }
    }


    //Điều hướng đến MovieListFragment với tham số mode
    private void navigateToMovieList(Bundle args) {
        int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        if (currentDestinationId == R.id.settingsFragment) {
            navController.navigate(R.id.action_settingsFragment_to_movieListFragment, args); // Từ Settings về MovieList
        } else if (currentDestinationId == R.id.aboutFragment) {
            navController.navigate(R.id.action_aboutFragment_to_movieListFragment, args); // Từ About về MovieList
        } else {
            navController.navigate(R.id.movieListFragment, args); // Điều hướng trực tiếp
        }
    }

    //Tạo menu tùy chọn trên toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (shouldShowMoreIcon) { // Chỉ hiển thị menu khi ở tab Movies
            getMenuInflater().inflate(R.menu.movie_type_menu, menu);
            return true;
        }
        return false;
    }

    //Xử lý sự kiện nhấn nút trên ActionBar (nút home/back hoặc drawer)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int currentDestinationId = navController.getCurrentDestination().getId();
            Log.d("MainActivity", "onOptionsItemSelected: Current Destination = " + currentDestinationId);

            if (currentDestinationId == R.id.movieDetailsFragment) {
                NavBackStackEntry previousEntry = navController.getPreviousBackStackEntry();
                if (previousEntry != null) {
                    int previousDestinationId = previousEntry.getDestination().getId();
                    Log.d("MainActivity", "onOptionsItemSelected: Previous Destination = " + previousDestinationId);
                    if (previousDestinationId == R.id.showAllRemindersFragment) {
                        navController.navigate(R.id.action_movieDetailsFragment_to_showAllRemindersFragment); // Quay lại All Reminders
                        return true;
                    } else if (previousDestinationId == R.id.movieListFragment) {
                        String mode = viewModel.getSelectedTabPosition().getValue() == 1 ? MovieListFragment.MODE_FAVORITE : MovieListFragment.MODE_API;
                        viewModel.setIsInMovieDetail(mode, false); // Đặt lại trạng thái
                        viewModel.clearLastSelectedMovie(mode);    // Xóa phim cuối cùng
                        Bundle args = new Bundle();
                        args.putString("mode", mode);
                        navController.navigate(R.id.action_movieDetailsFragment_to_movieListFragment, args); // Quay lại MovieList
                        viewModel.setSelectedTabPosition(mode.equals(MovieListFragment.MODE_FAVORITE) ? 1 : 0);
                        return true;
                    }
                }
                return navController.navigateUp(); // Quay lại theo back stack nếu không xử lý
            } else if (currentDestinationId == R.id.showAllRemindersFragment) {
                Bundle args = new Bundle();
                args.putString("mode", MovieListFragment.MODE_API);
                navController.navigate(R.id.action_showAllRemindersFragment_to_movieListFragment, args); // Quay lại MovieList (mode API)
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START); // Đóng drawer nếu đang mở
                }
                return true;
            } else {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START); // Đóng drawer
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START);  // Mở drawer
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item); // Gọi phương thức cha nếu không xử lý
    }

    //Hỗ trợ điều hướng lên khi nhấn nút back trên ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, binding.drawerLayout) || super.onSupportNavigateUp();
    }

    //Xử lý sự kiện nhấn nút back vật lý
    @Override
    public void onBackPressed() {
        int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();
        Log.d("MainActivity", "onBackPressed: Current Destination = " + currentDestinationId);

        if (currentDestinationId == R.id.movieDetailsFragment) {
            NavBackStackEntry previousEntry = navController.getPreviousBackStackEntry();
            if (previousEntry != null) {
                int previousDestinationId = previousEntry.getDestination().getId();
                Log.d("MainActivity", "onBackPressed: Previous Destination = " + previousDestinationId);
                if (previousDestinationId == R.id.showAllRemindersFragment) {
                    navController.navigate(R.id.action_movieDetailsFragment_to_showAllRemindersFragment); // Quay lại All Reminders
                    return;
                } else if (previousDestinationId == R.id.movieListFragment) {
                    String mode = viewModel.getSelectedTabPosition().getValue() == 1 ? MovieListFragment.MODE_FAVORITE : MovieListFragment.MODE_API;
                    viewModel.setIsInMovieDetail(mode, false); // Đặt lại trạng thái
                    viewModel.clearLastSelectedMovie(mode);    // Xóa phim cuối cùng
                    Bundle args = new Bundle();
                    args.putString("mode", mode);
                    navController.navigate(R.id.action_movieDetailsFragment_to_movieListFragment, args); // Quay lại MovieList
                    viewModel.setSelectedTabPosition(mode.equals(MovieListFragment.MODE_FAVORITE) ? 1 : 0);
                    return;
                }
            }
            if (navController.navigateUp()) { // Quay lại theo back stack nếu không xử lý đặc biệt
                return;
            }
        } else if (currentDestinationId == R.id.movieListFragment) {
            Bundle args = Objects.requireNonNull(navController.getCurrentBackStackEntry()).getArguments();
            String mode = args != null ? args.getString("mode", MovieListFragment.MODE_API) : MovieListFragment.MODE_API;
            if (mode.equals(MovieListFragment.MODE_FAVORITE)) {
                Log.d("MainActivity", "onBackPressed: Finishing app from Favorite tab");
                finish(); // Thoát ứng dụng từ tab Favorite
                return;
            }
        }
        if (NavigationUI.navigateUp(navController, binding.drawerLayout)) { // Dùng NavigationUI để quay lại hoặc đóng drawer
            return;
        }
        super.onBackPressed(); // Thoát ứng dụng hoặc quay lại mặc định nếu không xử lý
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileViewModel.unregisterReminderDeletedReceiver(this);
    }
}