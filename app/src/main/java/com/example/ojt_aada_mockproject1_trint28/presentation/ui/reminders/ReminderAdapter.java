package com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

import java.util.ArrayList;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Reminder> reminders = new ArrayList<>();
    private final OnReminderClickListener clickListener;
    private final OnReminderDeleteListener deleteListener;

    public interface OnReminderClickListener {
        void onReminderClick(Reminder reminder);
    }

    public interface OnReminderDeleteListener {
        void onReminderDelete(Reminder reminder);
    }

    public ReminderAdapter(OnReminderClickListener clickListener, OnReminderDeleteListener deleteListener) {
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Placeholder, chưa cần layout
        return new RecyclerView.ViewHolder(new ViewGroup(parent.getContext()) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {}
        }) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Placeholder
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    public void submitList(List<Reminder> newReminders) {
        this.reminders = newReminders;
        notifyDataSetChanged();
    }
}