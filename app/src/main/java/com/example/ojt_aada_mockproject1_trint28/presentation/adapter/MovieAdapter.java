package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojt_aada_mockproject1_trint28.databinding.ItemMovieBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.ItemMovieGridBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Movie;

public class MovieAdapter extends PagingDataAdapter<Movie, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LIST = 1;
    private static final int VIEW_TYPE_GRID = 2;

    private boolean isGridMode;

    public MovieAdapter(boolean isGridMode) {
        super(DIFF_CALLBACK);
        this.isGridMode = isGridMode;
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
                ((ListViewHolder) holder).bind(movie);
            } else if (holder instanceof GridViewHolder) {
                ((GridViewHolder) holder).bind(movie);
            }
        }
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieBinding binding;

        ListViewHolder(ItemMovieBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Movie movie) {
            binding.setMovie(movie);
            binding.executePendingBindings();
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        private final ItemMovieGridBinding binding;

        GridViewHolder(ItemMovieGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
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