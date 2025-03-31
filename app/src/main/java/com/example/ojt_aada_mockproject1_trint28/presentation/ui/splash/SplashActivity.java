package com.example.ojt_aada_mockproject1_trint28.presentation.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainActivity;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 3 seconds
    private MovieListViewModel viewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Khởi tạo ViewModel (Hilt sẽ tự động inject)
        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        // Start fetching popular movies in the background
        fetchPopularMovies();

        // Use a Handler to delay the navigation to MainActivity
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Start MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Finish SplashActivity so the user can't go back to it
            finish();
        }, SPLASH_DELAY);
    }

    private void fetchPopularMovies() {
        viewModel.getMovies("popular"); // Start the fetch without subscribing
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // Clear disposables to prevent memory leaks
    }
}