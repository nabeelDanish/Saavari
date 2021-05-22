package com.savaari.api.entity;

import com.savaari.api.database.DBHandlerFactory;

public class Rider extends User {
    public Rider() {

    }

    // Main Methods



    /* Methods for system interactions */

    //Rider-side CRUD methods

    public void login() {
        setUserID(DBHandlerFactory.getInstance().createDBHandler().loginRider(this));
    }

    public boolean fetchData() {
        return DBHandlerFactory.getInstance().createDBHandler().fetchRiderData(this);
    }
}
