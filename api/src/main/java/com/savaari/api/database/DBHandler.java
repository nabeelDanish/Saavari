package com.savaari.api.database;

import com.savaari.api.entity.*;
import org.json.JSONArray;

import java.util.ArrayList;

public interface DBHandler {

    /* CRUD Methods*/
    Boolean addRider(String username, String emailAddress, String password);
    Boolean addDriver(String username, String emailAddress, String password);
    Integer loginRider(Rider rider);
    int loginDriver(Driver driver);
    boolean fetchRiderData(Rider rider);
    boolean fetchDriverData(Driver driver);

    /* Registration Methods */
    boolean sendRegistrationRequest(Driver driver);

    /*Driver-Vehicle methods*/
    boolean sendVehicleRegistrationRequest(Driver driver, Vehicle currentVehicleRequest);
    boolean respondToVehicleRegistrationRequest(Vehicle currentVehicleRequest);

    /* Unused CRUD methods */
    JSONArray riderDetails();
    JSONArray driverDetails();
    Boolean deleteRider();
    Boolean deleteDriver();

    boolean respondToDriverRegistrationRequest(Driver driver);

    boolean addAdmin(Administrator admin);
    boolean loginAdmin(Administrator admin);
    ArrayList<Vehicle> getVehicleRequests();
    ArrayList<Driver> getDriverRequests();
}
