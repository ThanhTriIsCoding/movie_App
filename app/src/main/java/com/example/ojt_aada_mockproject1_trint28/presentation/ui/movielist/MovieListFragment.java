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

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentMovieListBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
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
    private String lastMovieType;
    private boolean isGridMode;
    private String mode;
    private NavController navController;
    private List<Movie> fullFavoriteMovies;
    private RecyclerView.OnScrollListener scrollListener;

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

        isGridMode = mainViewModel.getIsGridMode().getValue() != null && mainViewModel.getIsGridMode().getValue();
        Log.d("MovieListFragment", "Initial isGridMode: " + isGridMode);

        adapter = new MovieAdapter(isGridMode, (movie, position) -> {
            Log.d("MovieAdapter", "Star clicked for movie: " + movie.getTitle() + " at position: " + position);
            if (!isGridMode) {
                disposables.add(
                        viewModel.isMovieLiked(movie.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(isLiked -> {
                                    if (isLiked) {
                                        disposables.add(
                                                viewModel.removeFavoriteMovie(movie)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(() -> {
                                                            Log.d("MovieListFragment", "Movie removed from favorites: " + movie.getTitle());
                                                            movie.setLiked(false);
                                                            adapter.notifyItemChanged(position, "isLiked");
                                                            if (mode.equals(MODE_FAVORITE)) {
                                                                viewModel.loadFavoriteMovies();
                                                            }
                                                        }, throwable -> {
                                                            Log.e("MovieListFragment", "Error removing movie: " + throwable.getMessage());
                                                        })
                                        );
                                    } else {
                                        disposables.add(
                                                viewModel.addFavoriteMovie(movie)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(() -> {
                                                            Log.d("MovieListFragment", "Movie added to favorites: " + movie.getTitle());
                                                            movie.setLiked(true);
                                                            adapter.notifyItemChanged(position, "isLiked");
                                                            if (mode.equals(MODE_FAVORITE)) {
                                                                viewModel.loadFavoriteMovies();
                                                            }
                                                        }, throwable -> {
                                                            Log.e("MovieListFragment", "Error adding movie: " + throwable.getMessage());
                                                        })
                                        );
                                    }
                                }, throwable -> {
                                    Log.e("MovieListFragment", "Error checking isLiked: " + throwable.getMessage());
                                })
                );
            }
        }, movie -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("movie", movie);
            mainViewModel.setIsInMovieDetail(true);
            navController.navigate(R.id.action_movieListFragment_to_movieDetailFragment, bundle);
        });

        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        switchLayoutManager(isGridMode);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (mode.equals(MODE_API) && lastMovieType != null) {
                loadMovies(lastMovieType);
            } else if (mode.equals(MODE_FAVORITE)) {
                viewModel.loadFavoriteMovies();
                binding.swipeRefreshLayout.setRefreshing(false);
            }
        });

        mainViewModel.getIsGridMode().observe(getViewLifecycleOwner(), newGridMode -> {
            // Lưu vị trí scroll trước khi chuyển đổi layout
            int firstVisiblePosition = getFirstVisibleItemPosition();
            mainViewModel.setScrollPosition(mode, firstVisiblePosition);
            Log.d("MovieListFragment", "Saved scroll position before layout change: " + firstVisiblePosition + " for mode: " + mode);

            isGridMode = newGridMode;
            // Gỡ bỏ OnScrollListener để ngăn chặn việc ghi đè scrollPosition
            binding.recyclerView.removeOnScrollListener(scrollListener);
            switchLayoutManager(isGridMode);
            adapter.setGridMode(isGridMode);

            // Khôi phục vị trí scroll sau khi layout hoàn tất và dữ liệu đã được tải
            binding.recyclerView.post(() -> {
                Integer savedPosition = mainViewModel.getScrollPosition(mode).getValue();
                if (savedPosition != null && savedPosition >= 0 && savedPosition < adapter.getItemCount()) {
                    binding.recyclerView.scrollToPosition(savedPosition);
                    Log.d("MovieListFragment", "Restored scroll position after layout change to: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
                } else {
                    Log.d("MovieListFragment", "Cannot restore scroll position after layout change. Saved position: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
                }
                // Thêm lại OnScrollListener sau khi khôi phục vị trí scroll
                binding.recyclerView.addOnScrollListener(scrollListener);
            });
        });

        mainViewModel.getMovieType().observe(getViewLifecycleOwner(), movieType -> {
            if (movieType != null) {
                loadMovies(movieType);
                mainViewModel.setShouldResetPosition(true);
                Log.d("MovieListFragment", "Movie type changed to: " + movieType + ", shouldResetPosition set to true");
            }
        });

        if (mode.equals(MODE_FAVORITE)) {
            viewModel.loadFavoriteMovies();
            viewModel.getFavoriteMoviesLiveData().observe(getViewLifecycleOwner(), movies -> {
                Log.d("MovieListFragment", "Favorite movies received: " + (movies != null ? movies.size() : "null"));
                if (movies != null) {
                    fullFavoriteMovies = new ArrayList<>(movies);
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
                    // Khôi phục vị trí scroll sau khi dữ liệu được tải
                    restoreScrollPosition("favorites");
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
                    // Khôi phục vị trí scroll sau khi lọc
                    restoreScrollPosition("search");
                }
            });
        }

        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = getFirstVisibleItemPosition();
                if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                    mainViewModel.setScrollPosition(mode, firstVisiblePosition);
                    Log.d("MovieListFragment", "Saved scroll position: " + firstVisiblePosition + " for mode: " + mode);
                }
            }
        };
        binding.recyclerView.addOnScrollListener(scrollListener);

        adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getRefresh() instanceof LoadState.NotLoading &&
                    loadStates.getAppend() instanceof LoadState.NotLoading &&
                    loadStates.getPrepend() instanceof LoadState.NotLoading) {
                Boolean shouldReset = mainViewModel.getShouldResetPosition().getValue();
                if (shouldReset != null && shouldReset) {
                    binding.recyclerView.scrollToPosition(0);
                    mainViewModel.setScrollPosition(mode, 0);
                    mainViewModel.setShouldResetPosition(false);
                    Log.d("MovieListFragment", "Reset scroll position to 0 due to category change for mode: " + mode);
                } else {
                    restoreScrollPosition("load");
                }
            }
            return null;
        });
    }

    private void loadMovies(String movieType) {
        boolean shouldResetPosition = lastMovieType == null || !lastMovieType.equals(movieType);
        lastMovieType = movieType;

        if (shouldResetPosition) {
            mainViewModel.setScrollPosition(mode, 0);
            mainViewModel.setShouldResetPosition(true);
            Log.d("MovieListFragment", "Set scroll position to 0 and shouldResetPosition to true due to movie type change: " + movieType + " for mode: " + mode);
        }

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
        RecyclerView.LayoutManager newLayoutManager = isGridMode ?
                new GridLayoutManager(getContext(), 2) :
                new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(newLayoutManager);
        Log.d("MovieListFragment", "Switched layout manager to " + (isGridMode ? "Grid" : "Linear"));
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

    private void restoreScrollPosition(String source) {
        Integer savedPosition = mainViewModel.getScrollPosition(mode).getValue();
        if (savedPosition != null && savedPosition >= 0 && savedPosition < adapter.getItemCount()) {
            binding.recyclerView.scrollToPosition(savedPosition);
            Log.d("MovieListFragment", "Restored scroll position after " + source + " to: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
        } else if (savedPosition != null && savedPosition > 0) {
            // Thử lại sau 100ms nếu dữ liệu chưa tải xong
            binding.recyclerView.postDelayed(() -> restoreScrollPosition(source), 100);
            Log.d("MovieListFragment", "Retrying scroll position restore after " + source + ". Saved position: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
        } else {
            Log.d("MovieListFragment", "Cannot restore scroll position after " + source + ". Saved position: " + savedPosition + ", Item count: " + adapter.getItemCount() + " for mode: " + mode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.removeOnScrollListener(scrollListener);
        disposables.clear();
        binding = null;
    }
}