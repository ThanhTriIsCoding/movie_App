package com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetProfileUseCase;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetRemindersUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import android.util.Log;

@HiltViewModel
public class ProfileViewModel extends ViewModel {
    private final GetProfileUseCase getProfileUseCase;
    private final GetRemindersUseCase getRemindersUseCase;

    private final MutableLiveData<Profile> _profile = new MutableLiveData<>();
    public LiveData<Profile> profile = _profile;

    private final MutableLiveData<List<Reminder>> _reminders = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Reminder>> reminders = _reminders;

    private final MutableLiveData<Boolean> _navigateToEditProfile = new MutableLiveData<>();
    public LiveData<Boolean> navigateToEditProfile = _navigateToEditProfile;

    private final MutableLiveData<Boolean> _navigateToShowAllReminders = new MutableLiveData<>();
    public LiveData<Boolean> navigateToShowAllReminders = _navigateToShowAllReminders;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public ProfileViewModel(GetProfileUseCase getProfileUseCase, GetRemindersUseCase getRemindersUseCase) {
        this.getProfileUseCase = getProfileUseCase;
        this.getRemindersUseCase = getRemindersUseCase;
        loadProfile();
        loadReminders();
    }

    private void loadProfile() {
        disposables.add(
                getProfileUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                profile -> _profile.setValue(profile),
                                throwable -> _profile.setValue(null)
                        )
        );
    }

    private void loadReminders() {
        disposables.add(
                getRemindersUseCase.execute()
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