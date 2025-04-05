package com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ojt_aada_mockproject1_trint28.databinding.FragmentShowAllRemindersBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.adapter.ShowAllRemindersAdapter;
import com.example.ojt_aada_mockproject1_trint28.worker.ReminderWorker;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ShowAllRemindersFragment extends Fragment {

    private FragmentShowAllRemindersBinding binding;
    private ShowAllRemindersViewModel viewModel;
    private ShowAllRemindersAdapter adapter;
    private BroadcastReceiver reminderDeletedReceiver;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowAllRemindersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShowAllRemindersViewModel.class);
        adapter = new ShowAllRemindersAdapter();

        binding.rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReminders.setAdapter(adapter);

        viewModel.reminders.observe(getViewLifecycleOwner(), reminders -> {
            Log.d("ShowAllRemindersFrag", "LiveData updated: " + reminders.size() + " reminders");
            adapter.setReminders(reminders);
        });

        adapter.setDeleteClickListener(reminder -> {
            // Show confirmation dialog before deleting
            new AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this reminder?")
                    .setNegativeButton("No", (dialog, which) -> {
                        // Dismiss the dialog and do nothing
                        dialog.dismiss();
                    })
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Proceed with deletion
                        viewModel.deleteReminder(reminder);
                    })
                    .setCancelable(true) // Allow dismissing the dialog by pressing back
                    .show();
        });

        // Register the broadcast receiver
        reminderDeletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ShowAllRemindersFrag", "Received reminder deleted broadcast");
                int movieId = intent.getIntExtra(ReminderWorker.KEY_MOVIE_ID, -1);
                String dateTime = intent.getStringExtra(ReminderWorker.KEY_DATE_TIME);
                Log.d("ShowAllRemindersFrag", "Deleted reminder: movieId=" + movieId + ", dateTime=" + dateTime);
                // The UI should already update via LiveData, but we can log or add additional logic here if needed
            }
        };
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(reminderDeletedReceiver, new IntentFilter(ReminderWorker.ACTION_REMINDER_DELETED));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(reminderDeletedReceiver);
        binding = null;
    }
}