package com.example.ojt_aada_mockproject1_trint28.presentation.ui.movielist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentMovieListFavouriteBinding;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentProfileBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MovieListFavouriteFragment extends Fragment {

    private FragmentMovieListFavouriteBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieListFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textview.setText("Movie List Favourite Fragment");
    }
}