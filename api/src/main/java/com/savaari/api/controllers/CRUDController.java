package com.savaari.api.controllers;

import com.savaari.api.database.DBHandlerFactory;
import com.savaari.api.entity.*;
import org.json.JSONObject;

import java.util.ArrayList;

public class CRUDController
{
    // Main Attributes
    private static final String LOG_TAG = CRUDController.class.getSimpleName();
    Driver driver;
    Rider rider;

    public CRUDController()
    {

    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Rider getRider() { return rider; }

    /* User CRUD methods */

    public Integer loginRider(Rider rider) {
        rider.login();
        this.rider = rider;
        return this.rider.getUserID();
    }

    public void persistRiderLogin(Rider rider) {
        this.rider = rider;
    }

    // Login Driver
    public Integer loginDriver(Driver driver) {
        driver.login();
        this.driver = driver;
        return this.driver.getUserID();
    }

    public void persistDriverLogin(Driver driver) {
        this.driver = driver;
    }

    // Adding a new Rider Account
    public boolean addRider(String username, String email_address, String password)
    {
        return DBHandlerFactory.getInstance().createDBHandler().addRider(username, email_address,
                User.hashPassword(password));
    }

    // Add a new inactive Driver method
    public boolean addDriver(String username, String email_address, String password) {
        return DBHandlerFactory.getInstance().createDBHandler().addDriver(username, email_address,
                User.hashPassword(password));
    }

    public Rider riderData() {
        if (rider.fetchData()) {
            return rider;
        }
        else {
            return null;
        }
    }

    public Driver driverData() {
        if (driver.fetchData()) {
            return driver;
        }
        return null;
    }

    public boolean setMarkActive(boolean activeStatus)
    {
        driver.setActive(activeStatus);
        return driver.setMarkActive();
    }

    public JSONObject deleteRider() {
        return null;
    }

    public JSONObject deleteDriver() {
        return null;
    }
    /* End of section */

    public boolean setActiveVehicle(Vehicle vehicle) {
        return driver.selectActiveVehicle(vehicle);
    }


    /* Send Registration Request methods */

    public boolean registerDriver(Driver driver) {
        this.driver.setRegistrationDetails(driver.getFirstName(),
                driver.getLastName(),
                driver.getPhoneNo(),
                driver.getCNIC(),
                driver.getLicenseNumber());

        return driver.sendRegistrationRequest();
    }

    public boolean sendVehicleRegistrationRequest(Vehicle vehicle) {
        return driver.sendVehicleRegistrationRequest(vehicle);
    }

    public ArrayList<Ride> getRideLogForRider() {
        return rider.getRideLog();
    }

    public ArrayList<Ride> getRideLogForDriver() {
        return driver.getRideLog();
    }
}
