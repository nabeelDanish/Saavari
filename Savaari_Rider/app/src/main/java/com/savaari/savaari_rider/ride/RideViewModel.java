package com.savaari.savaari_rider.ride;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.savaari.savaari_rider.Repository;
import com.savaari.savaari_rider.ride.entity.Location;
import com.savaari.savaari_rider.ride.entity.Ride;
import com.savaari.savaari_rider.ride.entity.RideRequest;
import com.savaari.savaari_rider.ride.entity.Rider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RideViewModel extends ViewModel {

    private static String LOG_TAG = RideViewModel.class.getSimpleName();
    private final Repository repository = null;

    /* Credentials for netowrk operations */
    private int USER_ID = -1;

    /* User account data*/
    private LatLng userCoordinates;

    private Ride previousRide;
    private Ride ride;

    /* User locations data for pinging */
    private ArrayList<Location> mUserLocations = new ArrayList<>();

    /* Data Loaded status flags */
    private final MutableLiveData<Boolean> userDataLoaded = new MutableLiveData<>();
    private final MutableLiveData<Boolean> userLocationsLoaded = new MutableLiveData<>();
    private final MutableLiveData<Boolean> driverLocationFetched = new MutableLiveData<>(false);

    /* Get user data */
    public LatLng getUserCoordinates() {
        return userCoordinates;
    }
    public ArrayList<Location> getUserLocations() {
        return mUserLocations;
    }
    public Ride getRide() { return ride; }

    public void resetRide() {
        setPreviousRide(ride);
        ride = new Ride();
        ride.getRideParameters().getRider().setUserID(USER_ID);
    }

    /* Set USER_ID */
    //public void setUserID(int USER_ID) { this.USER_ID = USER_ID; }

    /* Return LiveData to observe Data Loaded Flags */
    public LiveData<Boolean> isLiveUserDataLoaded() {
        return userDataLoaded;
    }
    public LiveData<Boolean> isLiveUserLocationsLoaded() { return userLocationsLoaded; }
    public LiveData<Boolean> isDriverLocationFetched() { return driverLocationFetched; }

    /* Need a setter since coordinates are received from activity */
    public void setUserCoordinates(double latitude, double longitude) {
        userCoordinates = new LatLng(latitude, longitude);
    }

    public void loadUserData() {
        repository.loadUserData(object -> {
            try {
                if (object == null) {
                    Log.d(LOG_TAG, "onDataLoaded(): resultString is null");
                    userDataLoaded.postValue(false);
                }
                else {
                    Rider fetchedRider = (Rider) object;
                    ride.getRideParameters().setRider(fetchedRider);
                    userDataLoaded.postValue(true);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                userDataLoaded.postValue(false);
                Log.d(LOG_TAG, "onDataLoaded(): exception thrown");
            }
        }, USER_ID);
    }

    /* loads ArrayList of Location*/
    public void loadUserLocations() {
        //if (!userLocationsLoaded.getValue())
        {

            repository.getUserLocations(object -> {
                try {
                    if (object != null)
                    {
                        Log.d(TAG, "loadUserLocations: not null");

                        mUserLocations = (ArrayList<Location>) object;
                        userLocationsLoaded.postValue(true);
                    }
                    else {
                        userLocationsLoaded.postValue(false);
                    }
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Exception in loadUserLocations");
                    e.printStackTrace();
                    userLocationsLoaded.postValue(false);
                }
            });
        }
        /*
        else {
            userLocationsLoaded.postValue(true);
        }*/
    }

    private void setPreviousRide(Ride ride) {
        previousRide = ride;
    }

    public Ride getPreviousRide() {
        return previousRide;
    }
}
