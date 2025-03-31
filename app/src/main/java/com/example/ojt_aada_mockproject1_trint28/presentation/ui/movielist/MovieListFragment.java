package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentMovieListBinding;

public class MovieListFragment extends Fragment {

    private FragmentMovieListBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textView.setText("Movies Fragment");
    }

    // Phương thức để MainActivity gọi khi cần chuyển đổi List/Grid
    public void switchLayoutManager(boolean isGridMode) {
        // Chưa cần xử lý vì chưa hiển thị danh sách phim
    }

    // Phương thức để MainActivity gọi khi chọn loại phim
    public void updateMovieType(String movieType) {
        // Chưa cần xử lý vì chưa hiển thị danh sách phim
    }
}