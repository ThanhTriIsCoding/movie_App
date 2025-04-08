package com.example.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


import com.example.data.local.entity.ReminderEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId")
    Flowable<List<ReminderEntity>> getReminders(int userId);

    @Insert
    Completable insertReminder(ReminderEntity reminder);

    @Query("SELECT * FROM reminders WHERE movieId = :movieId AND dateTime = :dateTime AND userId = :userId")
    Flowable<List<ReminderEntity>> getRemindersByMovieAndTime(int movieId, String dateTime, int userId);

    @Query("DELETE FROM reminders WHERE movieId = :movieId AND dateTime = :dateTime AND userId = :userId")
    Completable deleteReminder(int movieId, String dateTime, int userId);
}