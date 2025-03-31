package com.example.ojt_aada_mockproject1_trint28.domain.usecase;

import com.example.ojt_aada_mockproject1_trint28.domain.model.Reminder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class GetRemindersUseCase {
    public Observable<List<Reminder>> execute() {
        List<Reminder> reminders = new ArrayList<>();
        return Observable.just(reminders); // Placeholder
    }
}