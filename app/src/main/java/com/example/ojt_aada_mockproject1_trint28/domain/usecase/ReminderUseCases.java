package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public class ReminderUseCases {
    private final ReminderRepository reminderRepository;

    @Inject
    public ReminderUseCases(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public Completable addReminder(Reminder reminder) {
        return reminderRepository.addReminder(reminder, 1); // Default userId
    }

    public Completable deleteReminder(Reminder reminder) {
        return reminderRepository.deleteReminder(reminder.getMovieId(), reminder.getDateTime(), 1); // Default userId
    }

    public Flowable<List<Reminder>> getReminders() {
        return reminderRepository.getReminders(1); // Default userId
    }
}