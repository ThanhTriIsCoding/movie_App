package com.example.ojt_aada_mockproject1_trint28.data.repository;

import android.net.Uri;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Profile;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class ProfileRepository {
    private final DatabaseReference databaseReference;
    private final StorageReference storageReference;
    private static final String PROFILES_PATH = "profiles";
    private static final String AVATARS_PATH = "avatars";

    public ProfileRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference(PROFILES_PATH);
        storageReference = FirebaseStorage.getInstance().getReference(AVATARS_PATH);
    }

    public Observable<Profile> getProfile(String userId) {
        return Observable.create(emitter -> {
            databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Profile profile = dataSnapshot.getValue(Profile.class);
                    if (profile != null) {
                        profile.setUserId(userId);
                        emitter.onNext(profile);
                    } else {
                        // Default profile if none exists
                        Profile defaultProfile = new Profile(userId, "Thang Nguyen", "abc@abc.com", "2015/11/27", "Female", null);
                        emitter.onNext(defaultProfile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });
        });
    }

    public Completable saveProfile(Profile profile) {
        return Completable.create(emitter -> {
            databaseReference.child(profile.getUserId()).setValue(profile)
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable uploadAvatar(String userId, Uri avatarUri) {
        return Completable.create(emitter -> {
            StorageReference avatarRef = storageReference.child(userId + ".jpg");
            avatarRef.putFile(avatarUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            Profile updatedProfile = new Profile();
                            updatedProfile.setUserId(userId);
                            updatedProfile.setAvatarUrl(uri.toString());
                            databaseReference.child(userId).child("avatarUrl").setValue(uri.toString())
                                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                                    .addOnFailureListener(emitter::onError);
                        });
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }
}