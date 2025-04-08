package com.example.domain.repository;

import com.example.domain.model.Reminder;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

import java.util.List;

public interface IReminderRepository {
    Completable addReminder(Reminder reminder, int userId);
    Completable deleteReminder(int movieId, String dateTime, int userId);
    Flowable<List<Reminder>> getReminders(int userId);
    Completable deletePastReminders(int userId); // Thêm phương thức này
}