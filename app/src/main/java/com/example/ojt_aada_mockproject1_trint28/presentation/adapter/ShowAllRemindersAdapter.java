package com.example.ojt_aada_mockproject1_trint28.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojt_aada_mockproject1_trint28.R;
import com.example.ojt_aada_mockproject1_trint28.databinding.ItemReminderBinding;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowAllRemindersAdapter extends RecyclerView.Adapter<ShowAllRemindersAdapter.ReminderViewHolder> {

    private List<Reminder> reminders = new ArrayList<>();
    private OnDeleteClickListener deleteClickListener;
    private boolean showPoster = true; // Mặc định hiển thị poster
    private boolean showDeleteButton = true; // Mặc định hiển thị nút xóa

    public interface OnDeleteClickListener {
        void onDeleteClick(Reminder reminder);
    }

    public void setDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    public void setReminders(List<Reminder> reminders) {
        this.reminders = reminders != null ? reminders : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Thêm phương thức để cấu hình chế độ hiển thị
    public void setDisplayMode(boolean showPoster, boolean showDeleteButton) {
        this.showPoster = showPoster;
        this.showDeleteButton = showDeleteButton;
        notifyDataSetChanged(); // Làm mới toàn bộ adapter để áp dụng chế độ hiển thị mới
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
            binding.setShowPoster(showPoster);
            binding.setShowDeleteButton(showDeleteButton);
            binding.setDeleteClickListener(v -> {
                if (deleteClickListener != null) {
                    deleteClickListener.onDeleteClick(reminder);
                }
            });

            // Load the poster image using Picasso
            Picasso.get()
                    .load(reminder.getPosterUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.reminderPoster);

            binding.executePendingBindings();
        }
    }
}