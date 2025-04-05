package com.example.ojt_aada_mockproject1_trint28.data.repository;

import com.example.ojt_aada_mockproject1_trint28.data.local.dao.ReminderDao;
import com.example.ojt_aada_mockproject1_trint28.data.local.entity.ReminderEntity;
import com.example.ojt_aada_mockproject1_trint28.data.mapper.ReminderMapper;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReminderRepository {
    private final ReminderDao reminderDao;

    public ReminderRepository(ReminderDao reminderDao) {
        this.reminderDao = reminderDao;
    }

    public Flowable<List<Reminder>> getReminders(int userId) {
        return reminderDao.getReminders(userId)
                .map(entities -> entities.stream()
                        .map(ReminderMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    public Completable addReminder(Reminder reminder, int userId) {
        ReminderEntity entity = ReminderMapper.toEntity(reminder, userId);
        return reminderDao.insertReminder(entity);
    }

    public Flowable<List<Reminder>> getRemindersByMovieAndTime(int movieId, String dateTime, int userId) {
        return reminderDao.getRemindersByMovieAndTime(movieId, dateTime, userId)
                .map(entities -> entities.stream()
                        .map(ReminderMapper::toDomain)
                        .collect(Collectors.toList()));
    }

    public Completable deleteReminder(int movieId, String dateTime, int userId) {
        return reminderDao.deleteReminder(movieId, dateTime, userId);
    }

    public Completable deletePastReminders(int userId) {
        return Completable.fromAction(() -> {
            List<ReminderEntity> reminders = reminderDao.getReminders(userId).blockingFirst();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            for (ReminderEntity reminder : reminders) {
                try {
                    Date reminderDate = sdf.parse(reminder.getDateTime());
                    if (reminderDate.getTime() < System.currentTimeMillis()) {
                        reminderDao.deleteReminder(reminder.getMovieId(), reminder.getDateTime(), userId).blockingAwait();
                    }
                } catch (ParseException e) {
                    // Handle parsing error
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}