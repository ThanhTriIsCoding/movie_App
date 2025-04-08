// com.example.domain.usecase.ReminderUseCases
package com.example.domain.usecase;

import com.example.domain.model.Reminder;
import com.example.domain.repository.IReminderRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import javax.inject.Inject;
import java.util.List;

public class ReminderUseCases {
    private final IReminderRepository reminderRepository;

    @Inject
    public ReminderUseCases(IReminderRepository reminderRepository) {
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