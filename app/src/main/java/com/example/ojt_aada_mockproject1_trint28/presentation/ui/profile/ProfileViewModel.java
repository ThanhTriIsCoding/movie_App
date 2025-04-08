package com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.ProfileUseCases;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.ReminderUseCases;
import com.example.ojt_aada_mockproject1_trint28.worker.ReminderWorker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final ProfileUseCases profileUseCases;
    private final ReminderUseCases reminderUseCases;

    private final MutableLiveData<Profile> _profile = new MutableLiveData<>();
    public LiveData<Profile> profile = _profile;

    private final MutableLiveData<List<Reminder>> _reminders = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Reminder>> reminders = _reminders;

    private final MutableLiveData<Boolean> _navigateToEditProfile = new MutableLiveData<>();
    public LiveData<Boolean> navigateToEditProfile = _navigateToEditProfile;

    private final MutableLiveData<Boolean> _navigateToShowAllReminders = new MutableLiveData<>();
    public LiveData<Boolean> navigateToShowAllReminders = _navigateToShowAllReminders;
    private final MutableLiveData<Bitmap> avatar = new MutableLiveData<>();

    private final CompositeDisposable disposables = new CompositeDisposable();
    private BroadcastReceiver reminderDeletedReceiver;

    @Inject
    public ProfileViewModel(ProfileUseCases profileUseCases, ReminderUseCases reminderUseCases) {
        this.profileUseCases = profileUseCases;
        this.reminderUseCases = reminderUseCases;
        loadProfile();
        loadReminders();
    }

    public void registerReminderDeletedReceiver(Context context) {
        reminderDeletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("ProfileViewModel", "Received reminder deleted broadcast");
                loadReminders(); // Làm mới danh sách reminders
            }
        };
        LocalBroadcastManager.getInstance(context)
                .registerReceiver(reminderDeletedReceiver, new IntentFilter(ReminderWorker.ACTION_REMINDER_DELETED));
    }

    public void unregisterReminderDeletedReceiver(Context context) {
        if (reminderDeletedReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(reminderDeletedReceiver);
        }
    }

    private void loadProfile() {
        disposables.add(
                profileUseCases.getProfile()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                profile -> {
                                    _profile.setValue(profile);
                                    avatar.setValue(base64ToBitmap(profile.getAvatarBase64()));
                                },
                                throwable -> _profile.setValue(null)
                        )
        );
    }

    public LiveData<Bitmap> getAvatar() {
        return avatar;
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReminders() {
        disposables.add(
                reminderUseCases.getReminders()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                reminders -> {
                                    Log.d("ProfileViewModel", "Flowable emitted: " + reminders.size() + " reminders");
                                    _reminders.setValue(reminders);
                                },
                                throwable -> {
                                    Log.e("ProfileViewModel", "Error loading reminders: " + throwable.getMessage());
                                    _reminders.setValue(new ArrayList<>());
                                }
                        )
        );
    }

    public void onEditClicked() {
        _navigateToEditProfile.setValue(true);
    }

    public void onShowAllClicked() {
        _navigateToShowAllReminders.setValue(true);
    }

    public void onNavigationHandled() {
        _navigateToEditProfile.setValue(false);
        _navigateToShowAllReminders.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}