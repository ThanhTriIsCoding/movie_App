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
    private List<Movie> fullFavoriteMovies; // Lưu danh sách đầy đủ để lọc

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
        Log.d("MovieListFragment", "isGridMode: " + isGridMode);

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
            int firstVisiblePosition = getFirstVisibleItemPosition();
            mainViewModel.setScrollPosition(firstVisiblePosition);

            isGridMode = newGridMode;
            switchLayoutManager(isGridMode);
            adapter.setGridMode(isGridMode);

            binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    binding.recyclerView.removeOnLayoutChangeListener(this);
                    if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                        binding.recyclerView.scrollToPosition(firstVisiblePosition);
                    }
                }
            });
        });

        mainViewModel.getMovieType().observe(getViewLifecycleOwner(), movieType -> {
            if (movieType != null) {
                loadMovies(movieType);
                mainViewModel.setShouldResetPosition(true);
            }
        });

        if (mode.equals(MODE_FAVORITE)) {
            // Load danh sách yêu thích ban đầu
            viewModel.loadFavoriteMovies();
            viewModel.getFavoriteMoviesLiveData().observe(getViewLifecycleOwner(), movies -> {
                Log.d("MovieListFragment", "Favorite movies received: " + (movies != null ? movies.size() : "null"));
                if (movies != null) {
                    fullFavoriteMovies = new ArrayList<>(movies); // Cập nhật lại danh sách đầy đủ
                    // Hiển thị danh sách dựa trên searchQuery hiện tại
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

            // Theo dõi searchQuery để lọc danh sách phim yêu thích
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
        }

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = getFirstVisibleItemPosition();
                if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                    mainViewModel.setScrollPosition(firstVisiblePosition);
                }
            }
        });

        adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getRefresh() instanceof LoadState.NotLoading &&
                    loadStates.getAppend() instanceof LoadState.NotLoading &&
                    loadStates.getPrepend() instanceof LoadState.NotLoading) {
                if (mainViewModel.getShouldResetPosition().getValue() != null && mainViewModel.getShouldResetPosition().getValue()) {
                    binding.recyclerView.scrollToPosition(0);
                    mainViewModel.setShouldResetPosition(false);
                } else {
                    Integer savedPosition = mainViewModel.getScrollPosition().getValue();
                    if (savedPosition != null && savedPosition > 0) {
                        binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                binding.recyclerView.removeOnLayoutChangeListener(this);
                                if (adapter.getItemCount() > savedPosition) {
                                    binding.recyclerView.scrollToPosition(savedPosition);
                                } else {
                                    binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                }
                            }
                        });
                    }
                }
            }
            return null;
        });
    }

    private void loadMovies(String movieType) {
        boolean shouldResetPosition = lastMovieType == null || !lastMovieType.equals(movieType);
        lastMovieType = movieType;

        if (shouldResetPosition) {
            mainViewModel.setScrollPosition(0);
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
                                },
                                throwable -> {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    binding.textView.setText("Error loading movies: " + throwable.getMessage());
                                    binding.textView.setVisibility(View.VISIBLE);
                                    binding.recyclerView.setVisibility(View.GONE);
                                }
                        )
        );
    }

    public void switchLayoutManager(boolean isGridMode) {
        if (isGridMode) {
            binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
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
        disposables.clear();
        binding = null;
    }
}