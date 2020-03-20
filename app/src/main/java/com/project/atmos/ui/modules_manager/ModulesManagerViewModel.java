package com.project.atmos.ui.modules_manager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ModulesManagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ModulesManagerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}