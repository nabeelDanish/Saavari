package com.savaari.api.controllers;

import com.savaari.api.database.DBHandlerFactory;
import com.savaari.api.entity.Driver;
import com.savaari.api.entity.Location;
import com.savaari.api.entity.Rider;

import java.util.ArrayList;

public class LocationController
{
    // Main Attributes
    private static final String LOG_TAG = LocationController.class.getSimpleName();

    /* Location update & retrieval methods*/


    public boolean saveRiderLocation(Rider rider) {
        return rider.saveLocation();
    }

    public boolean saveDriverLocation(Driver driver) {

        return driver.saveDriverLocation();
    }

    public void getDriverLocation(Driver driver) {
        driver.getDriverLocation();
    }

    public void getRiderLocation(Rider rider) {
        rider.fetchLocation();
    }

    public ArrayList<Location> getDriverLocations() {
        return DBHandlerFactory.getInstance().createDBHandler().getDriverLocations();
    }
    public ArrayList<Location> getRiderLocations() {
        return DBHandlerFactory.getInstance().createDBHandler().getRiderLocations();
    }
    /* End of section */
}
