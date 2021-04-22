package com.savaari.api.controllers;

import com.savaari.api.database.DBHandlerFactory;
import com.savaari.api.entity.Administrator;
import com.savaari.api.entity.Driver;
import com.savaari.api.entity.User;
import com.savaari.api.entity.Vehicle;

import java.util.ArrayList;

public class AdminSystem {
    private Administrator administrator;

    public boolean addAdmin(Administrator administrator) {
        administrator.setPassword(User.hashPassword(administrator.getPassword()));
        return DBHandlerFactory.getInstance().createDBHandler().addAdmin(administrator);
    }
    public boolean loginAdmin(Administrator administrator) {
        this.administrator = administrator;
        return this.administrator.login();
    }

    public ArrayList<Vehicle> getVehicleRequests() {
        return DBHandlerFactory.getInstance().createDBHandler().getVehicleRequests();
    }

    public ArrayList<Driver> getDriverRequests() {
        return DBHandlerFactory.getInstance().createDBHandler().getDriverRequests();
    }

    public boolean respondToVehicleRegistrationRequest(Vehicle vehicleRequest) {
        return DBHandlerFactory.getInstance().createDBHandler().respondToVehicleRegistrationRequest(vehicleRequest);
    }

    public boolean respondToDriverRegistrationRequest(Driver driver) {
        return DBHandlerFactory.getInstance().createDBHandler().respondToDriverRegistrationRequest(driver);
    }
}
