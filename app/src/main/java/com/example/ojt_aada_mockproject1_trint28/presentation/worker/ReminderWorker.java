package com.example.ojt_aada_mockproject1_trint28.presentation.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ojt_aada_mockproject1_trint28.data.local.database.AppDatabase;
import com.example.ojt_aada_mockproject1_trint28.data.repository.ReminderRepository;

import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReminderWorker extends Worker {

    public static final String KEY_MOVIE_TITLE = "movie_title";
    public static final String KEY_MOVIE_ID = "movie_id";
    public static final String KEY_DATE_TIME = "date_time";
    public static final String ACTION_REMINDER_DELETED = "com.example.ojt_aada_mockproject1_trint28.ACTION_REMINDER_DELETED"; // Added constant
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final int DEFAULT_USER_ID = 1;

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Extract data from input
        String movieTitle = getInputData().getString(KEY_MOVIE_TITLE);
        int movieId = getInputData().getInt(KEY_MOVIE_ID, -1);
        String dateTime = getInputData().getString(KEY_DATE_TIME);

        // Show the notification
        showNotification(movieTitle);

        // Delete the reminder from the database
        if (movieId != -1 && dateTime != null) {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            ReminderRepository repository = new ReminderRepository(db.reminderDao());
            try {
                repository.deleteReminder(movieId, dateTime, DEFAULT_USER_ID)
                        .subscribeOn(Schedulers.io())
                        .blockingAwait(); // Ensure the deletion completes
                Log.d("ReminderWorker", "Deleted reminder: movieId=" + movieId + ", dateTime=" + dateTime);

                // Broadcast that the reminder has been deleted
                Intent intent = new Intent(ACTION_REMINDER_DELETED);
                intent.putExtra(KEY_MOVIE_ID, movieId);
                intent.putExtra(KEY_DATE_TIME, dateTime);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                Log.d("ReminderWorker", "Broadcast sent: " + ACTION_REMINDER_DELETED);

            } catch (Exception e) {
                Log.e("ReminderWorker", "Error deleting reminder: " + e.getMessage());
                return Result.failure();
            }
        } else {
            Log.e("ReminderWorker", "Invalid movieId or dateTime: movieId=" + movieId + ", dateTime=" + dateTime);
            return Result.failure();
        }

        return Result.success();
    }

    private void showNotification(String movieTitle) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Movie Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Movie Reminder")
                .setContentText("Reminder for movie: " + movieTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}