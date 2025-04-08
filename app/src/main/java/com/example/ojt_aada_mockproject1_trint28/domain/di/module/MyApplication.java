package com.example.ojt_aada_mockproject1_trint28.domain.di.module;

import android.app.Application;

import com.example.ojt_aada_mockproject1_trint28.data.local.database.AppDatabase;
import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;
import com.google.firebase.FirebaseApp;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltAndroidApp
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        // Clean up past reminders
        AppDatabase db = AppDatabase.getDatabase(this);
        ReminderRepository repository = new ReminderRepository(db.reminderDao());
        repository.deletePastReminders(1) // Assuming userId = 1
                .subscribeOn(Schedulers.io())
                .subscribe();

        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            // Optionally, show a user-facing message if appropriate
        });
    }
}