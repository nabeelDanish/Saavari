package com.savaari.savaari_rider.ride;

import android.util.Log;

import androidx.annotation.VisibleForTesting;
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
    private Repository repository = null;

    /* Credentials for network operations */
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
    private MutableLiveData<Ride> rideFound = new MutableLiveData<>();
    private final MutableLiveData<Boolean> endOfRideAcknowledged = new MutableLiveData<>();
    private final MutableLiveData<Boolean> rideStatusChanged = new MutableLiveData<>();
    private final MutableLiveData<Boolean> closeToPickup = new MutableLiveData<>();



    /* Get user data */
    public LatLng getUserCoordinates() {
        return userCoordinates;
    }
    public ArrayList<Location> getUserLocations() {
        return mUserLocations;
    }
    public Ride getRide() { return ride; }

    @VisibleForTesting
    public void setRide(Ride ride) {
        this.ride = ride;
        rideFound.postValue(ride);
    }

    public void resetRide() {
        setPreviousRide(ride);
        initBasicRide();
    }

    public void initBasicRide() {
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
    public LiveData<Ride> isRideFound() { return rideFound; }
    public LiveData<Boolean> isEndOfRideAcknowledged() { return endOfRideAcknowledged; }
    public LiveData<Boolean> isRideStatusChanged() { return rideStatusChanged; }

    public RideViewModel(int USER_ID, Repository repository) {
        this.USER_ID = USER_ID;
        this.repository = repository;
        ride = new Ride();
        ride.getRideParameters().getRider().setUserID(USER_ID);
    }

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

    /*
     * sets driver status to:
     * PAIRED -> If driver has been matched
     * NOT_PAIRED -> If drivers available but all declined
     * NOT_FOUND -> If drivers not available
     * */
    public void findDriver(int USER_ID, LatLng pickupLocation, LatLng dropoffLocation) {

        repository.findDriver(object -> {

                    if (object == null) {
                        Log.d(LOG_TAG, "findDriver(): NOT FOUND");
                        ride.getRideParameters().setFindStatus(RideRequest.STATUS_ERROR);
                    }
                    else {
                        Log.d(LOG_TAG, "findDriver(): FOUND");
                        ride = (Ride) object;
                        ride.getRideParameters().setFindStatus(RideRequest.PAIRED);
                    }
                    rideFound.postValue(ride);

                }, USER_ID, pickupLocation.latitude, pickupLocation.longitude, dropoffLocation.latitude,
                dropoffLocation.longitude, ride.getRideParameters().getPaymentMethod(), ride.getRideParameters().getRideType().getTypeID());
    }

    public void getRideStatus() {
        Log.d(LOG_TAG, "getRideStatus called!");

        if (ride.getRideID() > 0) {
            repository.getRideStatus(object -> {
                if (object == null) {
                    Log.d(LOG_TAG, "getRideStatus: null recieved");
                }
                else {
                    JSONObject result = (JSONObject) object;

                    try {
                        if (ride.getRideStatus() != result.getInt("RIDE_STATUS")) {
                            ride.setRideStatus(result.getInt("RIDE_STATUS"));

                            if (ride.getRideStatus() == Ride.ARRIVED_AT_DEST) {
                                ride.setFare(result.getInt("FARE"));
                            }
                            rideStatusChanged.postValue(true);
                        }
                    }
                    catch (JSONException e) {
                        Log.d(LOG_TAG, ":getRideStatus: JSONException");
                    }
                }
            }, ride.getRideID());

        }
    }

    public void acknowledgeEndOfRide() {
        repository.acknowledgeEndOfRide(object -> {
            ride.setRideStatus(Ride.END_ACKED);
            endOfRideAcknowledged.postValue(true);
        }, ride.getRideID(), ride.getRideParameters().getRider().getUserID());
    }

    public void giveFeedbackForDriver(float rating) {
        repository.giveFeedbackForDriver(object -> {
            Log.d(LOG_TAG, "giveFeedbackForDriver: success!");
        }, previousRide.getRideID(), previousRide.getRideParameters().getDriver().getUserID(), rating);
    }

    public void fetchDriverLocation() {
        Log.d(LOG_TAG, "fetchDriverLocation called!");
        int driverID = ride.getRideParameters().getDriver().getUserID();

        if (driverID > 0) {
            repository.getDriverLocation(object -> {
                if (object != null) {
                    try {
                        JSONObject result = (JSONObject) object;
                        if (result.getInt("STATUS_CODE") == 200) {

                            Log.d(LOG_TAG, " fetchDriverLocation: Got driver location!");
                            ride.getRideParameters().getDriver().setCurrentLocation(new Location(result.getDouble("LATITUDE"),
                                    result.getDouble("LONGITUDE"), null));
                            driverLocationFetched.postValue(true);

                            if (ride.closeToPickup()) {
                                closeToPickup.postValue(true);
                            }
                        }
                        else {
                            Log.d(LOG_TAG, " fetchDriverLocation: Failed to fetch driver location");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Log.d(LOG_TAG, "fetchDriverLocation(): Exception");
                    }
                }
            }, driverID);
        }
    }

    public void loadRide() {
        Log.d(LOG_TAG, ":loadRide() called!");

        if (USER_ID > 0) {
            repository.getRide(object -> {
                try {
                    if (object == null) {
                        Log.d(LOG_TAG, " getRide: Failed to fetch ride");
                        ride.getRideParameters().setFindStatus(Ride.RS_DEFAULT);
                    }
                    else {
                        ride = (Ride) object;

                        Log.d(LOG_TAG, " getRide: Is taking a ride!");
                        ride.getRideParameters().setFindStatus(RideRequest.ALREADY_PAIRED);
                    }
                }
                catch (Exception e) {
                    ride.getRideParameters().setFindStatus(Ride.RS_DEFAULT);
                    e.printStackTrace();
                    Log.d(LOG_TAG, "loadRide(): Exception");
                }
                finally {
                    Log.d(LOG_TAG, " getRide: POSTING RIDE VAL");
                    rideFound.postValue(ride);
                }
            }, USER_ID);
        }
    }

    private void setPreviousRide(Ride ride) {
        previousRide = ride;
    }

    public Ride getPreviousRide() {
        return previousRide;
    }
}
