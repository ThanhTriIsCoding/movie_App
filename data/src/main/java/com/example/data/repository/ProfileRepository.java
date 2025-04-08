package com.example.data.repository;

import android.util.Log;

import com.example.domain.model.Profile;
import com.example.domain.repository.IProfileRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class ProfileRepository implements IProfileRepository {
    private final DatabaseReference databaseReference;
    private static final String PROFILES_PATH = "profiles";

    public ProfileRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference(PROFILES_PATH);
    }

    @Override
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

    @Override
    public Completable saveProfile(Profile profile) {
        return Completable.create(emitter -> {
            databaseReference.child(profile.getUserId()).setValue(profile)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("ProfileRepository", "Profile saved successfully for userId: " + profile.getUserId());
                        emitter.onComplete();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileRepository", "Failed to save profile", e);
                        emitter.onError(e);
                    });
        });
    }
}
