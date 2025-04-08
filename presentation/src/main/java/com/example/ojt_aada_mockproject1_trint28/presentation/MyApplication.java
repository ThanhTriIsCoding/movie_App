package com.example.ojt_aada_mockproject1_trint28.presentation;

import android.app.Application;

import com.example.domain.repository.IReminderRepository;
import com.google.firebase.FirebaseApp;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltAndroidApp
public class MyApplication extends Application {

    @Inject
    IReminderRepository reminderRepository; // Inject interface từ domain

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

        // Clean up past reminders using injected repository
        reminderRepository.deletePastReminders(1) // Assuming userId = 1
                .subscribeOn(Schedulers.io())
                .subscribe();

        RxJavaPlugins.setErrorHandler(throwable -> {
            throwable.printStackTrace();
            // Optionally, show a user-facing message if appropriate
        });
    }
}