package com.example.ojt_aada_mockproject1_trint28.presentation.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private final MutableLiveData<Boolean> _isGridMode = new MutableLiveData<>(false);
    public LiveData<Boolean> isGridMode = _isGridMode;

    public MainViewModel() {
        // Không cần UseCase vì chưa cần dữ liệu
    }

    public void toggleDisplayMode() {
        _isGridMode.setValue(!_isGridMode.getValue());
    }
}