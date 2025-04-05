package com.example.ojt_aada_mockproject1_trint28.presentation.ui.reminders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.DeleteReminderUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetRemindersUseCase;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.util.Log;

import javax.inject.Inject;

@HiltViewModel
public class ShowAllRemindersViewModel extends ViewModel {
    private final GetRemindersUseCase getRemindersUseCase;
    private final DeleteReminderUseCase deleteReminderUseCase;
    private final MutableLiveData<List<Reminder>> _reminders = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Reminder>> reminders = _reminders;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public ShowAllRemindersViewModel(GetRemindersUseCase getRemindersUseCase, DeleteReminderUseCase deleteReminderUseCase) {
        this.getRemindersUseCase = getRemindersUseCase;
        this.deleteReminderUseCase = deleteReminderUseCase;
        loadReminders();
    }

    private void loadReminders() {
        disposables.add(
                getRemindersUseCase.execute()
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
                deleteReminderUseCase.execute(reminder)
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