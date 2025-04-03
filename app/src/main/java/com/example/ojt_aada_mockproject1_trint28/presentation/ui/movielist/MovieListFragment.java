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
import androidx.paging.LoadState;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentMovieListBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.MovieAdapter;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainViewModel;

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

        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        isGridMode = mainViewModel.getIsGridMode().getValue() != null && mainViewModel.getIsGridMode().getValue();
        Log.d("MovieListFragment", "isGridMode: " + isGridMode);
        adapter = new MovieAdapter(isGridMode, (movie, position) -> {
            Log.d("MovieAdapter", "Star clicked for movie: " + movie.getTitle() + " at position: " + position);
            if (!isGridMode) {
                // Check the current isLiked state in the database before proceeding
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
                                                            movie.setLiked(false); // Update the isLiked state
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
                                                            movie.setLiked(true); // Update the isLiked state
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
            viewModel.setScrollPosition(firstVisiblePosition);

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

        if (mode.equals(MODE_API)) {
            mainViewModel.getMovieType().observe(getViewLifecycleOwner(), movieType -> {
                if (movieType != null) {
                    loadMovies(movieType);
                }
            });
        } else if (mode.equals(MODE_FAVORITE)) {
            viewModel.loadFavoriteMovies();
            viewModel.getFavoriteMoviesLiveData().observe(getViewLifecycleOwner(), movies -> {
                Log.d("MovieListFragment", "Favorite movies received: " + (movies != null ? movies.size() : "null"));
                if (movies != null) {
                    adapter.submitData(getViewLifecycleOwner().getLifecycle(), PagingData.from(movies));
                }
            });
        }

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisiblePosition = getFirstVisibleItemPosition();
                if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                    viewModel.setScrollPosition(firstVisiblePosition);
                }
            }
        });

        adapter.addLoadStateListener(loadStates -> {
            if (loadStates.getRefresh() instanceof LoadState.NotLoading &&
                    loadStates.getAppend() instanceof LoadState.NotLoading &&
                    loadStates.getPrepend() instanceof LoadState.NotLoading) {
                Integer savedPosition = viewModel.getScrollPosition().getValue();
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
            return null;
        });
    }

    private void loadMovies(String movieType) {
        boolean shouldResetPosition = lastMovieType == null || !lastMovieType.equals(movieType);
        lastMovieType = movieType;

        if (shouldResetPosition) {
            viewModel.setScrollPosition(0);
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