package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

public class GetRemindersUseCase {
    private final ReminderRepository reminderRepository;
    private static final int DEFAULT_USER_ID = 1;

    @Inject
    public GetRemindersUseCase(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    public Flowable<List<Reminder>> execute() {
        return reminderRepository.getReminders(DEFAULT_USER_ID);
    }
}