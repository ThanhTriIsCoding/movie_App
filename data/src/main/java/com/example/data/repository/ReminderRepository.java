package com.example.data.repository;

import android.os.Build;

import com.example.data.local.dao.ReminderDao;
import com.example.data.local.entity.ReminderEntity;
import com.example.data.mapper.ReminderMapper;
import com.example.domain.model.Reminder;
import com.example.domain.repository.IReminderRepository;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ReminderRepository implements IReminderRepository {
    private final ReminderDao reminderDao;

    public ReminderRepository(ReminderDao reminderDao) {
        this.reminderDao = reminderDao;
    }

    @Override
    public Flowable<List<Reminder>> getReminders(int userId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return reminderDao.getReminders(userId)
                    .map(entities -> entities.stream()
                            .map(ReminderMapper::toDomain)
                            .collect(Collectors.toList()));
        }
        return null;
    }

    @Override
    public Completable addReminder(Reminder reminder, int userId) {
        ReminderEntity entity = ReminderMapper.toEntity(reminder, userId);
        return reminderDao.insertReminder(entity);
    }

    @Override
    public Completable deleteReminder(int movieId, String dateTime, int userId) {
        return reminderDao.deleteReminder(movieId, dateTime, userId);
    }

    @Override
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