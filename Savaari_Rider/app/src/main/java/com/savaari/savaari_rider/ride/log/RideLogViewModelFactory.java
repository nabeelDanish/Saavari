package com.savaari.savaari_rider.ride.log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.savaari.savaari_rider.Repository;

public class RideLogViewModelFactory implements ViewModelProvider.Factory {

    private int USER_ID = -1;
    private Repository repository;

    public RideLogViewModelFactory(int USER_ID, Repository repository) {
        this.USER_ID = USER_ID;
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RideLogViewModel.class)) {
            return (T) new RideLogViewModel(USER_ID, repository);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}