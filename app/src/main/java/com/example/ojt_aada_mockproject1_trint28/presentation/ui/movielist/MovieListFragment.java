package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import android.os.Bundle;
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
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.MovieAdapter;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.main.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class MovieListFragment extends Fragment {

    private FragmentMovieListBinding binding;
    private MovieListViewModel viewModel;
    private MainViewModel mainViewModel;
    private MovieAdapter adapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private String lastMovieType; // Track the last loaded movie type
    private boolean isGridMode; // Track the current layout mode

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel (Hilt sẽ tự động inject)
        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        // Khởi tạo MainViewModel để quan sát movieType
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Khởi tạo Adapter
        isGridMode = mainViewModel.getIsGridMode().getValue() != null && mainViewModel.getIsGridMode().getValue();
        adapter = new MovieAdapter(isGridMode);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true); // Improve performance
        switchLayoutManager(isGridMode); // Set initial layout manager

        // Set up SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            if (lastMovieType != null) {
                loadMovies(lastMovieType); // Reload movies for the current movie type
            }
        });

        // Quan sát chế độ List/Grid từ MainViewModel
        mainViewModel.getIsGridMode().observe(getViewLifecycleOwner(), newGridMode -> {
            // Save the current scroll position
            int firstVisiblePosition = getFirstVisibleItemPosition();
            viewModel.setScrollPosition(firstVisiblePosition);

            // Update the layout mode and adapter
            isGridMode = newGridMode;
            switchLayoutManager(isGridMode);
            adapter.setGridMode(isGridMode);

            // Restore the scroll position after layout change
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

        // Quan sát movieType từ MainViewModel
        mainViewModel.getMovieType().observe(getViewLifecycleOwner(), movieType -> {
            if (movieType != null) {
                loadMovies(movieType);
            }
        });

        // Add a scroll listener to save the scroll position
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

        // Monitor the adapter's load state to restore the scroll position
        adapter.addLoadStateListener(loadStates -> {
            // Check if the initial data load is complete (not loading and no errors)
            if (loadStates.getRefresh() instanceof LoadState.NotLoading &&
                    loadStates.getAppend() instanceof LoadState.NotLoading &&
                    loadStates.getPrepend() instanceof LoadState.NotLoading) {
                Integer savedPosition = viewModel.getScrollPosition().getValue();
                if (savedPosition != null && savedPosition > 0) {
                    // Add a layout change listener to ensure the scroll happens after layout
                    binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                        @Override
                        public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                                   int oldLeft, int oldTop, int oldRight, int oldBottom) {
                            binding.recyclerView.removeOnLayoutChangeListener(this);
                            // Ensure the adapter has enough items to scroll to the saved position
                            if (adapter.getItemCount() > savedPosition) {
                                binding.recyclerView.scrollToPosition(savedPosition);
                            } else {
                                // If the adapter doesn't have enough items yet, scroll to the last item to trigger more data loading
                                binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            }
                        }
                    });
                }
            }
            return null; // Explicit return statement for void lambda
        });
    }

    private void loadMovies(String movieType) {
        // Check if the movie type has changed
        boolean shouldResetPosition = lastMovieType == null || !lastMovieType.equals(movieType);
        lastMovieType = movieType; // Update the last movie type

        // If the movie type has changed, reset the scroll position
        if (shouldResetPosition) {
            viewModel.setScrollPosition(0); // Reset scroll position when movie type changes
        }

        // Show the refresh indicator if this is a pull-to-refresh action
        if (binding.swipeRefreshLayout.isRefreshing()) {
            binding.swipeRefreshLayout.setRefreshing(true);
        }

        disposables.add(
                viewModel.getMovies(movieType)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                pagingData -> {
                                    adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
                                    // Hide the refresh indicator after data is submitted
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                },
                                throwable -> {
                                    // Hide the refresh indicator on error
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    // Xử lý lỗi nếu cần
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