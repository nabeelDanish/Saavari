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
    private int categorySelected = -1;

    /* Credentials for network operations */
    private int USER_ID = -1;

    private final MutableLiveData<ArrayList<Ride>> rideLogFetched = new MutableLiveData<>();
    private final MutableLiveData<Boolean> problemReported = new MutableLiveData<>();

    public RideLogViewModel(int USER_ID, Repository repository) {
        this.repository = repository;
        this.USER_ID = USER_ID;
    }

    public void setCategorySelected(int catgoryId) {
        categorySelected = catgoryId;
    }

    public int getCategorySelected() {
        return categorySelected;
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

    public LiveData<Boolean> reportProblem(String problemDescription, int position) {
        int rideId = -1;

        ArrayList<Ride> rideLog = rideLogFetched.getValue();
        if (rideLog != null) {
            rideId = rideLog.get(position).getRideID();
        }

        if (rideId == -1) {
            problemReported.postValue(false);
        }
        else {
            repository.reportProblem(object -> {
                try {
                    if (object == null) {
                        Log.d(LOG_TAG, " fetchRideLog: Failed to fetch ride");
                    }
                    else {
                        problemReported.postValue(true);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "fetchRideLog(): Exception");
                }
            }, problemDescription, rideId, categorySelected);
        }

        return problemReported;
    }
}
