package com.savaari.api.controllers;

import com.savaari.api.database.DBHandlerFactory;
import com.savaari.api.entity.Driver;
import com.savaari.api.entity.User;
import com.savaari.api.entity.Vehicle;
import org.json.JSONObject;

public class CRUDController
{
    // Main Attributes
    private static final String LOG_TAG = CRUDController.class.getSimpleName();
    Driver driver;

    public CRUDController()
    {

    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    /* User CRUD methods */

    // Login Driver
    public Integer loginDriver(Driver driver) {
        driver.login();
        this.driver = driver;
        return this.driver.getUserID();
    }

    public void persistDriverLogin(Driver driver) {
        this.driver = driver;
    }

    // Add a new inactive Driver method
    public boolean addDriver(String username, String email_address, String password) {
        return DBHandlerFactory.getInstance().createDBHandler().addDriver(username, email_address,
                User.hashPassword(password));
    }

    public Driver driverData() {
        if (driver.fetchData()) {
            return driver;
        }
        return null;
    }

    public JSONObject deleteDriver() {
        return null;
    }
    /* End of section */


    /* Send Registration Request methods */

    public boolean registerDriver(Driver driver) {
        this.driver.setRegistrationDetails(driver.getFirstName(),
                driver.getLastName(),
                driver.getPhoneNo(),
                driver.getCNIC(),
                driver.getLicenseNumber());

        return driver.sendRegistrationRequest();
    }
}
