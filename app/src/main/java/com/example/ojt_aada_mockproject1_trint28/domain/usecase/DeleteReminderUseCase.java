package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;

public class DeleteReminderUseCase {
    private final ReminderRepository reminderRepository;
    private static final int DEFAULT_USER_ID = 1;

    @Inject
    public DeleteReminderUseCase(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public Completable execute(Reminder reminder) {
        return reminderRepository.deleteReminder(reminder.getMovieId(), reminder.getDateTime(), DEFAULT_USER_ID);
    }
}