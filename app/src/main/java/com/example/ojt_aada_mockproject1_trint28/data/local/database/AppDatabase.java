package com.example.ojt_aada_mockproject1_trint28.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ojt_aada_mockproject1_trint28.data.local.dao.MovieDao;
import com.example.ojt_aada_mockproject1_trint28.data.local.entity.MovieEntity;

@Database(entities = {MovieEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "movie_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}