package com.savaari.savaari_driver.ride;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.savaari.savaari_driver.Repository;

public class RideViewModelFactory implements ViewModelProvider.Factory {

    private int USER_ID = -1;
    private final Repository repository;

    public RideViewModelFactory(int USER_ID, Repository repository) {
        this.USER_ID = USER_ID;
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RideViewModel.class)) {
            return (T) new RideViewModel(USER_ID, repository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}