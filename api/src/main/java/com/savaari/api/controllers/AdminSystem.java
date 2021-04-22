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
}
