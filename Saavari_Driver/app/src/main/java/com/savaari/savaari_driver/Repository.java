package com.savaari.savaari_driver;

import com.savaari.savaari_driver.entity.*;
import com.savaari.savaari_driver.services.network.NetworkUtil;
import com.savaari.savaari_driver.services.network.OnDataLoadedListener;
import java.util.concurrent.Executor;

public class Repository
{
    // Main Attributes
    private final Executor executor;
    private Driver driver;

    // Constructor
    Repository(Executor executor) {
        this.executor = executor;
    }

    // ---------------------------------------------------------------------------------------------
    //                                  NETWORK FUNCTIONS
    // ---------------------------------------------------------------------------------------------

    // Sign-Up
    public void signup(OnDataLoadedListener callback, String nickname, String username, String password) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().signup(nickname, username, password)));
    }
    // Login
    public void login(OnDataLoadedListener callback, String username, String password) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().login(username, password)));
    }
    // Persist Connection
    public void persistLogin(OnDataLoadedListener callback, int userID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().persistConnection(userID)));
    }
    // Logout
    public void logout(OnDataLoadedListener callback, int userID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().logout(userID)));
    }
    // Loading User Data
    public void loadUserData(OnDataLoadedListener callback, int currentUserID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().loadUserData(currentUserID)));
    }

    // Get User Locations
    public void getUserLocations(OnDataLoadedListener callback) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().getUserLocations()));
    }

    // Send Last Location
    public void sendLastLocation(OnDataLoadedListener callback, int currentUserID, double latitude, double longitude) {
        executor.execute(() ->
                callback.onDataLoaded(NetworkUtil.getInstance().sendLastLocation(currentUserID, latitude, longitude)));
    }

    // Set Mark Active
    public void setMarkActive(OnDataLoadedListener callback, int userID, int active_status)
    {
        executor.execute(() ->
                callback.onDataLoaded(NetworkUtil.getInstance().setMarkActive(userID, active_status)));
    }

    // Check Ride Request Status
    public void checkRideRequestStatus(OnDataLoadedListener callback, int userID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().checkRideRequestStatus(userID)));
    }

    // Start Matchmaking Service
    public void startMatchmaking(OnDataLoadedListener callback, int userID) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().startMatchmaking(userID)));
    }

    // Check ride status
    public void checkRideStatus(OnDataLoadedListener callback, int userID, int riderID, int rideTypeID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().checkRideStatus(userID, riderID, rideTypeID)));
    }

    // Confirm Ride Request
    public void confirmRideRequest(OnDataLoadedListener callback, int userID, int found_status, int riderID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().confirmRideRequest(userID, found_status, riderID)));
    }

    // Marking Arrival
    public void markArrival(OnDataLoadedListener callback, int rideID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().markArrival(rideID)));
    }

    // Starting Ride
    public void startRide(OnDataLoadedListener callback, int rideID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().startRide(rideID)));
    }

    // Marking Arrival at destination
    public void markDriverAtDestination(OnDataLoadedListener callback, Ride ride, double dist_travelled, int driverID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().markDriverAtDestination(ride, dist_travelled, driverID)));
    }

    // Ending Ride with Payment
    public void endRideWithPayment(OnDataLoadedListener callback, int rideID, Payment payment, int driverID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().endRideWithPayment(rideID, payment, driverID)));
    }

    // Sending Register Driver request
    public void sendRegisterDriverRequest(OnDataLoadedListener callback, Driver driver) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().sendRegistrationRequest(driver)));
    }

    // Sending Vehicle Registration Request
    public void sendVehicleRegistrationRequest(OnDataLoadedListener callback, Driver driver, Vehicle vehicle)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().sendVehicleRegistrationRequest(driver, vehicle)));
    }

    // Select Active Vehicle
    public void selectActiveVehicle(OnDataLoadedListener callback, int driverID, int vehicleID)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().selectActiveVehicle(driverID, vehicleID)));
    }

    // Give Feedback for Rider
    public void giveFeedbackForRider(OnDataLoadedListener callback, Ride ride, float rating)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().giveFeedbackForRider(ride, rating)));
    }

    // Getters and Setters
    public Driver getDriver() {
        return driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
