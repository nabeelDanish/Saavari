package com.savaari.savaari_rider.ride.log;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.savaari.savaari_rider.Repository;
import com.savaari.savaari_rider.ride.RideViewModel;
import com.savaari.savaari_rider.ride.entity.Ride;

import java.util.ArrayList;

public class RideLogViewModel extends ViewModel {

    private static String LOG_TAG = RideViewModel.class.getSimpleName();
    private Repository repository = null;

    /* Credentials for network operations */
    private int USER_ID = -1;

    private final MutableLiveData<ArrayList<Ride>> rideLogFetched = new MutableLiveData<>();

    public RideLogViewModel(int USER_ID, Repository repository) {
        this.repository = repository;
        this.USER_ID = USER_ID;
    }

    public void fetchRideLog() {

        repository.fetchRideLog(object -> {
            try {
                if (object == null) {
                    Log.d(LOG_TAG, " fetchRideLog: Failed to fetch ride");
                }
                else {
                    rideLogFetched.postValue((ArrayList<Ride>) object);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "fetchRideLog(): Exception");
            }
        });
    }

    public LiveData<ArrayList<Ride>> isRideLogFetched() {
        return rideLogFetched;
    }
}
