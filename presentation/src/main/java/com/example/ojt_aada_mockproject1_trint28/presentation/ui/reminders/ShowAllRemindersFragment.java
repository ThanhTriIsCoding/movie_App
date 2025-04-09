package com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.domain.model.Movie;
import com.example.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentShowAllRemindersBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.ShowAllRemindersAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShowAllRemindersFragment extends Fragment {

    private FragmentShowAllRemindersBinding binding;
    private ShowAllRemindersViewModel viewModel;
    private ShowAllRemindersAdapter adapter;
    private NavController navController;
    private boolean isNavigating = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowAllRemindersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(ShowAllRemindersViewModel.class);
        adapter = new ShowAllRemindersAdapter(viewModel);
        adapter.setDisplayMode(true, true);

        binding.rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReminders.setAdapter(adapter);

        viewModel.reminders.observe(getViewLifecycleOwner(), reminders -> {
            Log.d("ShowAllRemindersFrag", "LiveData updated: " + reminders.size() + " reminders");
            adapter.setReminders(reminders);
        });

        viewModel.navigateToMovieDetail.observe(getViewLifecycleOwner(), reminder -> {
            if (reminder != null && !isNavigating) {
                isNavigating = true;
                Movie movie = new Movie(
                        reminder.getMovieId(),
                        reminder.getTitle(),
                        "",
                        reminder.getReleaseDate(),
                        reminder.getVoteAverage(),
                        false,
                        reminder.getPosterUrl(),
                        false
                );
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie", movie);
                navController.navigate(R.id.action_showAllRemindersFragment_to_movieDetailsFragment, bundle);
                getViewLifecycleOwner().getLifecycle().addObserver(new androidx.lifecycle.LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull androidx.lifecycle.LifecycleOwner source, @NonNull androidx.lifecycle.Lifecycle.Event event) {
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            isNavigating = false;
                            getViewLifecycleOwner().getLifecycle().removeObserver(this);
                        }
                    }
                });
            }
        });

        viewModel.registerReminderDeletedReceiver(requireContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ShowAllRemindersFrag", "onResume called, isNavigating = " + isNavigating);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.unregisterReminderDeletedReceiver(requireContext());
        binding = null;
    }
}