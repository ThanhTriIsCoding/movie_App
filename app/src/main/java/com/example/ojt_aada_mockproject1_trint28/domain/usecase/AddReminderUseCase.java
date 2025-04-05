package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

import io.reactivex.rxjava3.core.Completable;

import javax.inject.Inject;

public class AddReminderUseCase {
    private final ReminderRepository reminderRepository;
    private static final int DEFAULT_USER_ID = 1;

    @Inject
    public AddReminderUseCase(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public Completable execute(Reminder reminder) {
        return reminderRepository.addReminder(reminder, DEFAULT_USER_ID);
    }

    public ReminderRepository getReminderRepository() {
        return reminderRepository;
    }
}