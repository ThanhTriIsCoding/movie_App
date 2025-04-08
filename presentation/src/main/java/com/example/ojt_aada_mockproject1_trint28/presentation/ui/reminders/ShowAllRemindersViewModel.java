package com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.domain.model.Reminder;
import com.example.domain.usecase.ReminderUseCases;
import com.example.ojt_aada_mockproject1_trint28.presentation.worker.ReminderWorker;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Inject;

@HiltViewModel
public class ShowAllRemindersViewModel extends ViewModel {
    private final ReminderUseCases reminderUseCases;
    private final MutableLiveData<List<Reminder>> _reminders = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Reminder>> reminders = _reminders;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private BroadcastReceiver reminderDeletedReceiver;

    @Inject
    public ShowAllRemindersViewModel(ReminderUseCases reminderUseCases) {
        this.reminderUseCases = reminderUseCases;
        loadReminders();
    }

    public void registerReminderDeletedReceiver(Context context) {
        reminderDeletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ShowAllRemindersVM", "Received reminder deleted broadcast");
                loadReminders();
            }
        };
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(reminderDeletedReceiver, new IntentFilter(ReminderWorker.ACTION_REMINDER_DELETED));
    }

    public void unregisterReminderDeletedReceiver(Context context) {
        if (reminderDeletedReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(reminderDeletedReceiver);
        }
    }

    private void loadReminders() {
        disposables.add(
                reminderUseCases.getReminders()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                reminders -> {
                                    Log.d("ShowAllRemindersVM", "Flowable emitted: " + reminders.size() + " reminders");
                                    _reminders.setValue(reminders);
                                },
                                throwable -> {
                                    Log.e("ShowAllRemindersVM", "Error loading reminders: " + throwable.getMessage());
                                    _reminders.setValue(new ArrayList<>());
                                }
                        )
        );
    }

    public void deleteReminder(Reminder reminder) {
        disposables.add(
                reminderUseCases.deleteReminder(reminder)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> Log.d("ShowAllRemindersVM", "Reminder deleted: " + reminder.getTitle()),
                                throwable -> Log.e("ShowAllRemindersVM", "Error deleting reminder: " + throwable.getMessage())
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}