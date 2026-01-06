package com.niccher.pctophonecopier.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> selectedFragment = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public HomeViewModel() {
        // Initialize with home fragment
        selectedFragment.setValue("home");
        isLoading.setValue(false);
    }

    public LiveData<String> getSelectedFragment() {
        return selectedFragment;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void selectFragment(String fragmentTag) {
        selectedFragment.setValue(fragmentTag);
    }

    public void setLoading(boolean loading) {
        isLoading.setValue(loading);
    }
}
