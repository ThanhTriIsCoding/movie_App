package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.domain.model.Movie;
import com.example.ojt_aada_mockproject1_trint28.databinding.ItemMovieBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.ItemMovieGridBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist.MovieListViewModel;

import java.util.List;

public class MovieAdapter extends PagingDataAdapter<Movie, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LIST = 1;
    private static final int VIEW_TYPE_GRID = 2;

    private boolean isGridMode;
    private final MovieListViewModel viewModel;

    public MovieAdapter(boolean isGridMode, MovieListViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.isGridMode = isGridMode;
        this.viewModel = viewModel;
    }

    public void setGridMode(boolean isGridMode) {
        this.isGridMode = isGridMode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return isGridMode ? VIEW_TYPE_GRID : VIEW_TYPE_LIST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GRID) {
            ItemMovieGridBinding binding = ItemMovieGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new GridViewHolder(binding);
        } else {
            ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ListViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Movie movie = getItem(position);
        if (movie != null) {
            if (holder instanceof ListViewHolder) {
                ((ListViewHolder) holder).bind(movie, position);
            } else if (holder instanceof GridViewHolder) {
                ((GridViewHolder) holder).bind(movie);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Movie movie = getItem(position);
            if (movie != null && holder instanceof ListViewHolder) {
                ItemMovieBinding binding = ((ListViewHolder) holder).binding;
                for (Object payload : payloads) {
                    if ("isLiked".equals(payload)) {
                        binding.setMovie(movie);
                        binding.executePendingBindings();
                    }
                }
            }
        }
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        ListViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie, int position) {
            binding.setMovie(movie);
            binding.setViewModel(viewModel);
            binding.setPosition(position);
            binding.executePendingBindings();
        }
    }

    class GridViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieGridBinding binding;

        GridViewHolder(ItemMovieGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

                binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Movie movie = getItem(position);
                    if (movie != null) {
                        viewModel.onMovieClicked(movie);
                    }
                }
            });
        }

        void bind(Movie movie) {
            binding.setMovie(movie);
            binding.executePendingBindings();
        }
    }

    private static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }
    };
}