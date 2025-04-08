package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.ItemReminderBinding;
import com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders.ShowAllRemindersViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowAllRemindersAdapter extends RecyclerView.Adapter<ShowAllRemindersAdapter.ReminderViewHolder> {

    private List<Reminder> reminders = new ArrayList<>();
    private ShowAllRemindersViewModel viewModel; // Thêm ViewModel
    private boolean showPoster = true;
    private boolean showDeleteButton = true;

    // Constructor nhận ViewModel
    public ShowAllRemindersAdapter(ShowAllRemindersViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders != null ? reminders : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setDisplayMode(boolean showPoster, boolean showDeleteButton) {
        this.showPoster = showPoster;
        this.showDeleteButton = showDeleteButton;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderBinding binding = ItemReminderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReminderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class ReminderViewHolder extends RecyclerView.ViewHolder {
        private final ItemReminderBinding binding;

        ReminderViewHolder(ItemReminderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Reminder reminder) {
            binding.setReminder(reminder);
            binding.setViewModel(viewModel); // Gắn ViewModel vào binding
            binding.setShowPoster(showPoster);
            binding.setShowDeleteButton(showDeleteButton);

            // Load poster image using Picasso
            Picasso.get()
                    .load(reminder.getPosterUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.reminderPoster);

            binding.executePendingBindings();
        }
    }
}