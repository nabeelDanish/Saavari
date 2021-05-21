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
    // Sending Register Driver request
    public void sendRegisterDriverRequest(OnDataLoadedListener callback, Driver driver) {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().sendRegistrationRequest(driver)));
    }
    // Sending Vehicle Registration Request
    public void sendVehicleRegistrationRequest(OnDataLoadedListener callback, Driver driver, Vehicle vehicle)
    {
        executor.execute(() -> callback.onDataLoaded(NetworkUtil.getInstance().sendVehicleRegistrationRequest(driver, vehicle)));
    }
    // Send Last Location
    public void sendLastLocation(OnDataLoadedListener callback, int currentUserID, double latitude, double longitude) {
        executor.execute(() ->
                callback.onDataLoaded(NetworkUtil.getInstance().sendLastLocation(currentUserID, latitude, longitude)));
    }


    // Getters and Setters
    public Driver getDriver() {
        return driver;
    }
    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
