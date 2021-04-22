package com.savaari.api.database;

import com.savaari.api.entity.Administrator;
import com.savaari.api.entity.Driver;
import com.savaari.api.entity.Location;
import com.savaari.api.entity.Vehicle;
import org.json.JSONArray;

import java.util.ArrayList;

public interface DBHandler {

    /* CRUD Methods*/
    Boolean addDriver(String username, String emailAddress, String password);
    int loginDriver(Driver driver);
    boolean fetchDriverData(Driver driver);

    /* Registration Methods */
    boolean sendRegistrationRequest(Driver driver);

    /*Driver-Vehicle methods*/
    boolean sendVehicleRegistrationRequest(Driver driver, Vehicle currentVehicleRequest);
    boolean respondToVehicleRegistrationRequest(Vehicle currentVehicleRequest);

    /* Unused CRUD methods */
    JSONArray driverDetails();
    Boolean deleteDriver();

    boolean respondToDriverRegistrationRequest(Driver driver);

    boolean addAdmin(Administrator admin);
    boolean loginAdmin(Administrator admin);
    ArrayList<Vehicle> getVehicleRequests();
    ArrayList<Driver> getDriverRequests();
}
