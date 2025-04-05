package com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile;

import android.net.Uri;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ProfileRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetProfileUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {
    private final GetProfileUseCase getProfileUseCase;
    private final ProfileRepository profileRepository;

    public final ObservableField<String> name = new ObservableField<>();
    public final ObservableField<String> email = new ObservableField<>();
    public final ObservableField<String> birthday = new ObservableField<>();
    public final ObservableField<String> gender = new ObservableField<>();
    public final ObservableField<Uri> avatarUri = new ObservableField<>();

    private final CompositeDisposable disposables = new CompositeDisposable();
    private static final String USER_ID = "1"; // Default userId

    @Inject
    public EditProfileViewModel(GetProfileUseCase getProfileUseCase, ProfileRepository profileRepository) {
        this.getProfileUseCase = getProfileUseCase;
        this.profileRepository = profileRepository;
        loadProfile();
    }

    private void loadProfile() {
        disposables.add(
                getProfileUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                profile -> {
                                    name.set(profile.getName());
                                    email.set(profile.getEmail());
                                    birthday.set(profile.getBirthday());
                                    gender.set(profile.getGender());
                                    avatarUri.set(profile.getAvatarUri());
                                },
                                throwable -> {
                                    // Handle error if needed
                                }
                        )
        );
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setBirthday(String birthday) {
        this.birthday.set(birthday);
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public void setAvatarUri(Uri uri) {
        this.avatarUri.set(uri);
        // Upload the avatar to Firebase Storage
        if (uri != null) {
            disposables.add(
                    profileRepository.uploadAvatar(USER_ID, uri)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        // Avatar uploaded successfully
                                    },
                                    throwable -> {
                                        // Handle error
                                    }
                            )
            );
        }
    }

    public void saveProfile() {
        Profile updatedProfile = new Profile(
                USER_ID,
                name.get(),
                email.get(),
                birthday.get(),
                gender.get(),
                avatarUri.get() != null ? avatarUri.get().toString() : null
        );
        disposables.add(
                profileRepository.saveProfile(updatedProfile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    // Profile saved successfully
                                },
                                throwable -> {
                                    // Handle error
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}