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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentShowAllRemindersBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.ShowAllRemindersAdapter;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShowAllRemindersFragment extends Fragment {

    private FragmentShowAllRemindersBinding binding;
    private ShowAllRemindersViewModel viewModel;
    private ShowAllRemindersAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowAllRemindersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShowAllRemindersViewModel.class);
        adapter = new ShowAllRemindersAdapter(viewModel); // Truyền ViewModel vào adapter
        adapter.setDisplayMode(true, true);

        binding.rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReminders.setAdapter(adapter);

        viewModel.reminders.observe(getViewLifecycleOwner(), reminders -> {
            Log.d("ShowAllRemindersFrag", "LiveData updated: " + reminders.size() + " reminders");
            adapter.setReminders(reminders);
        });

        // Đăng ký BroadcastReceiver
        viewModel.registerReminderDeletedReceiver(requireContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.unregisterReminderDeletedReceiver(requireContext());
        binding = null;
    }
}