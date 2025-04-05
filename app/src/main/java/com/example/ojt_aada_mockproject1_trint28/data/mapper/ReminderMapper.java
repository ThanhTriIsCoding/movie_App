package com.example.ojt_aada_mockproject1_trint28.data.mapper;

import com.example.ojt_aada_mockproject1_trint28.data.local.entity.ReminderEntity;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

public class ReminderMapper {
    public static Reminder toDomain(ReminderEntity entity) {
        return new Reminder(
                entity.getMovieId(),
                entity.getTitle(),
                entity.getDateTime(),
                entity.getPosterUrl(),
                entity.getReleaseDate(),
                entity.getVoteAverage()
        );
    }

    public static ReminderEntity toEntity(Reminder reminder, int userId) {
        return new ReminderEntity(
                userId,
                reminder.getMovieId(),
                reminder.getTitle(),
                reminder.getDateTime(),
                reminder.getPosterUrl(),
                reminder.getReleaseDate(),
                reminder.getVoteAverage()
        );
    }
}