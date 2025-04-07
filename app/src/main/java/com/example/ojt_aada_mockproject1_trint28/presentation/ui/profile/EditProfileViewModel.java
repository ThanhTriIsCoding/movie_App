package com.example.ojt_aada_mockproject1_trint28.presentation.ui.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ojt_aada_mockproject1_trint28.data.repository.ProfileRepository;
import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;
import com.example.ojt_aada_mockproject1_trint28.domain.usecase.GetProfileUseCase;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {

    private static final String USER_ID = "1";

    private final ProfileRepository profileRepository;
    private final GetProfileUseCase getProfileUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<String> name = new MutableLiveData<>("");
    private final MutableLiveData<String> email = new MutableLiveData<>("");
    private final MutableLiveData<String> birthday = new MutableLiveData<>("");
    private final MutableLiveData<String> gender = new MutableLiveData<>("Male");
    private final MutableLiveData<Bitmap> avatar = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    @Inject
    public EditProfileViewModel(ProfileRepository profileRepository, GetProfileUseCase getProfileUseCase) {
        this.profileRepository = profileRepository;
        this.getProfileUseCase = getProfileUseCase;
        loadProfile();
    }

    public LiveData<String> getName() { return name; }
    public LiveData<String> getEmail() { return email; }
    public LiveData<String> getBirthday() { return birthday; }
    public LiveData<String> getGender() { return gender; }
    public LiveData<Bitmap> getAvatar() { return avatar; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void setName(String name) { this.name.setValue(name); }
    public void setEmail(String email) { this.email.setValue(email); }
    public void setBirthday(String birthday) { this.birthday.setValue(birthday); }
    public void setGender(String gender) { this.gender.setValue(gender); }

    public void onCameraResult(Bitmap bitmap) { this.avatar.setValue(bitmap); }
    public void onGalleryResult(Bitmap bitmap) { this.avatar.setValue(bitmap); }

    private void loadProfile() {
        disposables.add(
                getProfileUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                profile -> {
                                    name.setValue(profile.getName());
                                    email.setValue(profile.getEmail());
                                    birthday.setValue(profile.getBirthday());
                                    gender.setValue(profile.getGender());
                                    if (profile.getAvatarBase64() != null) {
                                        avatar.setValue(base64ToBitmap(profile.getAvatarBase64()));
                                    }
                                },
                                throwable -> errorMessage.setValue("Failed to load profile: " + throwable.getMessage())
                        )
        );
    }

    public void saveProfile() {
        String nameValue = name.getValue() != null ? name.getValue() : "Unknown";
        String emailValue = email.getValue() != null ? email.getValue() : "unknown@example.com";
        String birthdayValue = birthday.getValue() != null ? birthday.getValue() : "01/01/2000";
        String genderValue = gender.getValue() != null ? gender.getValue() : "Male";

        Profile updatedProfile = new Profile(USER_ID, nameValue, emailValue, birthdayValue, genderValue, null);

        if (avatar.getValue() != null) {
            updatedProfile.setAvatarBase64(bitmapToBase64(avatar.getValue()));
        }

        saveProfileToDatabase(updatedProfile);
    }

    private void saveProfileToDatabase(Profile updatedProfile) {
        disposables.add(
                profileRepository.saveProfile(updatedProfile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> Log.d("EditProfileViewModel", "Profile saved successfully"),
                                throwable -> errorMessage.setValue("Failed to save profile: " + throwable.getMessage())
                        )
        );
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
