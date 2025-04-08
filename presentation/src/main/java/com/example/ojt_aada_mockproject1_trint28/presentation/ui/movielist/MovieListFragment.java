package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.LoadState;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentMovieListBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.MovieAdapter;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MovieListFragment extends Fragment {

    private static final String ARG_MODE = "mode";
    public static final String MODE_API = "api";
    public static final String MODE_FAVORITE = "favorite";

    private FragmentMovieListBinding binding;
    private MovieListViewModel viewModel;
    private MainViewModel mainViewModel;
    private MovieAdapter adapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private boolean isGridMode; // Chỉ áp dụng cho MODE_API
    private String mode;
    private NavController navController;
    private List<Movie> fullFavoriteMovies;
    private RecyclerView.OnScrollListener scrollListener;
    private boolean hasRestoredScrollPosition = false;
    private int pageSize = 20;

    public static MovieListFragment newInstance(String mode) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getString(ARG_MODE, MODE_API);
        } else {
            mode = MODE_API;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        viewModel.setMode(mode);

        // Khởi tạo isGridMode dựa trên mode
        if (mode.equals(MODE_API)) {
            isGridMode = mainViewModel.getIsGridMode().getValue() != null && mainViewModel.getIsGridMode().getValue();
        } else {
            isGridMode = false; // MODE_FAVORITE luôn là linear
        }
        Log.d("MovieListFragment", "Initial isGridMode: " + isGridMode + " for mode: " + mode);

        adapter = new MovieAdapter(isGridMode, viewModel);

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        switchLayoutManager(isGridMode);

        // Quan sát điều hướng đến MovieDetailFragment
        viewModel.getNavigateToMovieDetail().observe(getViewLifecycleOwner(), movie -> {
            if (movie != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie", movie);
                mainViewModel.setIsInMovieDetail(mode, true);
                navController.navigate(R.id.action_movieListFragment_to_movieDetailFragment, bundle);
                viewModel.resetNavigateToMovieDetail();
            }
        });

        // Quan sát vị trí cần cập nhật trong RecyclerView
        viewModel.getNotifyItemChangedPosition().observe(getViewLifecycleOwner(), position -> {
            if (position != null) {
                adapter.notifyItemChanged(position, "isLiked");
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (mode.equals(MODE_API)) {
                String movieType = mainViewModel.getMovieType().getValue();
                if (movieType != null) {
                    loadMovies(movieType);
                }
            } else if (mode.equals(MODE_FAVORITE)) {
                viewModel.loadFavoriteMovies();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Chỉ quan sát isGridMode cho MODE_API
        if (mode.equals(MODE_API)) {
            mainViewModel.getIsGridMode().observe(getViewLifecycleOwner(), newGridMode -> {
                int firstVisiblePosition = getFirstVisibleItemPosition();
                mainViewModel.setScrollPosition(mode, firstVisiblePosition);
                Log.d("MovieListFragment", "Saved scroll position before layout change: " + firstVisiblePosition + " for mode: " + mode);

                isGridMode = newGridMode;
                binding.recyclerView.removeOnScrollListener(scrollListener);
                switchLayoutManager(isGridMode);
                adapter.setGridMode(isGridMode);

                binding.recyclerView.post(() -> {
                    Integer savedPosition = mainViewModel.getScrollPosition(mode).getValue();
                    if (savedPosition != null && savedPosition >= 0 && savedPosition < adapter.getItemCount()) {
                        binding.recyclerView.scrollToPosition(savedPosition);
                        Log.d("MovieListFragment", "Restored scroll position after layout change to: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
                    } else {
                        Log.d("MovieListFragment", "Cannot restore scroll position after layout change. Saved position: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
                    }
                    binding.recyclerView.addOnScrollListener(scrollListener);
                });
            });
        } else {
            // MODE_FAVORITE: Đảm bảo luôn là linear
            switchLayoutManager(false);
            adapter.setGridMode(false);
        }

        mainViewModel.getMovieType().observe(getViewLifecycleOwner(), movieType -> {
            if (movieType != null && mode.equals(MODE_API)) { // Chỉ áp dụng cho MODE_API
                String lastMovieType = mainViewModel.getLastMovieType(mode).getValue();
                if (lastMovieType == null || !lastMovieType.equals(movieType)) {
                    mainViewModel.setShouldResetPosition(true);
                    Log.d("MovieListFragment", "Movie type changed to: " + movieType + ", shouldResetPosition set to true");
                }
                loadMovies(movieType);
            }
        });

        if (mode.equals(MODE_FAVORITE)) {
            viewModel.loadFavoriteMovies();
            viewModel.getFavoriteMoviesLiveData().observe(getViewLifecycleOwner(), movies -> {
                Log.d("MovieListFragment", "Favorite movies received: " + (movies != null ? movies.size() : "null"));
                if (movies != null) {
                    fullFavoriteMovies = new ArrayList<>(movies);
                    mainViewModel.setLoadedMovies(mode, fullFavoriteMovies);
                    String currentQuery = mainViewModel.getSearchQuery().getValue();
                    List<Movie> filteredMovies;
                    if (currentQuery == null || currentQuery.trim().isEmpty()) {
                        filteredMovies = new ArrayList<>(fullFavoriteMovies);
                    } else {
                        String searchQuery = currentQuery.toLowerCase();
                        filteredMovies = fullFavoriteMovies.stream()
                                .filter(movie -> movie.getTitle().toLowerCase().contains(searchQuery))
                                .collect(Collectors.toList());
                    }
                    adapter.submitData(getViewLifecycleOwner().getLifecycle(), PagingData.from(filteredMovies));
                }
            });

            mainViewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
                if (fullFavoriteMovies != null) {
                    List<Movie> filteredMovies;
                    if (query == null || query.trim().isEmpty()) {
                        filteredMovies = new ArrayList<>(fullFavoriteMovies);
                    } else {
                        String searchQuery = query.toLowerCase();
                        filteredMovies = fullFavoriteMovies.stream()
                                .filter(movie -> movie.getTitle().toLowerCase().contains(searchQuery))
                                .collect(Collectors.toList());
                    }
                    adapter.submitData(getViewLifecycleOwner().getLifecycle(), PagingData.from(filteredMovies));
                }
            });
        } else {
            List<Movie> loadedMovies = mainViewModel.getLoadedMovies(mode).getValue();
            if (loadedMovies != null && !loadedMovies.isEmpty()) {
                adapter.submitData(getViewLifecycleOwner().getLifecycle(), PagingData.from(loadedMovies));
                Log.d("MovieListFragment", "Restored loaded movies from ViewModel: " + loadedMovies.size() + " for mode: " + mode);
            }
        }

        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = getFirstVisibleItemPosition();
                if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                    mainViewModel.setScrollPosition(mode, firstVisiblePosition);
                    mainViewModel.setPageIndex(mode, (firstVisiblePosition / pageSize) + 1);
                    Log.d("MovieListFragment", "Saved scroll position: " + firstVisiblePosition + ", pageIndex: " + ((firstVisiblePosition / pageSize) + 1) + " for mode: " + mode);
                }
            }
        };
        binding.recyclerView.addOnScrollListener(scrollListener);

        adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getRefresh() instanceof LoadState.NotLoading &&
                    loadStates.getAppend() instanceof LoadState.NotLoading &&
                    loadStates.getPrepend() instanceof LoadState.NotLoading) {
                List<Movie> currentMovies = new ArrayList<>();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    Movie movie = adapter.peek(i);
                    if (movie != null) {
                        currentMovies.add(movie);
                    }
                }
                if (!currentMovies.isEmpty()) {
                    mainViewModel.setLoadedMovies(mode, currentMovies);
                    Log.d("MovieListFragment", "Saved " + currentMovies.size() + " movies to ViewModel for mode: " + mode);
                }

                Boolean shouldReset = mainViewModel.getShouldResetPosition().getValue();
                if (shouldReset != null && shouldReset) {
                    binding.recyclerView.scrollToPosition(0);
                    mainViewModel.setScrollPosition(mode, 0);
                    mainViewModel.setPageIndex(mode, 1);
                    mainViewModel.setShouldResetPosition(false);
                    hasRestoredScrollPosition = true;
                    Log.d("MovieListFragment", "Reset scroll position to 0 due to category change for mode: " + mode);
                } else if (!hasRestoredScrollPosition) {
                    Integer targetScrollPosition = mainViewModel.getScrollPosition(mode).getValue();
                    if (targetScrollPosition != null && targetScrollPosition >= 0) {
                        int currentItemCount = adapter.getItemCount();
                        if (targetScrollPosition < currentItemCount) {
                            binding.recyclerView.scrollToPosition(targetScrollPosition);
                            hasRestoredScrollPosition = true;
                            Log.d("MovieListFragment", "Restored scroll position to: " + targetScrollPosition + ", Item count: " + currentItemCount + " for mode: " + mode);
                        } else {
                            Log.d("MovieListFragment", "Data not enough to restore scroll position. Target: " + targetScrollPosition + ", Current item count: " + currentItemCount);
                            if (currentItemCount > 0) {
                                binding.recyclerView.scrollToPosition(currentItemCount - 1);
                                Log.d("MovieListFragment", "Scrolled to last position: " + (currentItemCount - 1) + " to load more data");
                            }
                        }
                    }
                }
            }
            return null;
        });

        if (mode.equals(MODE_API)) {
            String movieType = mainViewModel.getMovieType().getValue();
            if (movieType != null) {
                loadMovies(movieType);
            }
        }
    }

    private void loadMovies(String movieType) {
        String lastMovieType = mainViewModel.getLastMovieType(mode).getValue();
        mainViewModel.setLastMovieType(mode, movieType);

        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }

        disposables.add(
                viewModel.getMovies(movieType)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                pagingData -> {
                                    adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    Log.d("MovieListFragment", "Movies loaded for type: " + movieType);
                                },
                                throwable -> {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    binding.textView.setText("Error loading movies: " + throwable.getMessage());
                                    binding.textView.setVisibility(View.VISIBLE);
                                    binding.recyclerView.setVisibility(View.GONE);
                                    Log.e("MovieListFragment", "Error loading movies: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void switchLayoutManager(boolean isGridMode) {
        RecyclerView.LayoutManager newLayoutManager;
        if (mode.equals(MODE_FAVORITE)) {
            newLayoutManager = new LinearLayoutManager(getContext()); // Luôn là linear cho MODE_FAVORITE
        } else {
            newLayoutManager = isGridMode ?
                    new GridLayoutManager(getContext(), 2) :
                    new LinearLayoutManager(getContext());
        }
        binding.recyclerView.setLayoutManager(newLayoutManager);
        Log.d("MovieListFragment", "Switched layout manager to " + (isGridMode && mode.equals(MODE_API) ? "Grid" : "Linear") + " for mode: " + mode);
    }

    private int getFirstVisibleItemPosition() {
        RecyclerView.LayoutManager layoutManager = binding.recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
        return RecyclerView.NO_POSITION;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.removeOnScrollListener(scrollListener);
        disposables.clear();
        binding = null;
        hasRestoredScrollPosition = false;
    }
}