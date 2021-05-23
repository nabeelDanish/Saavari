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

    public boolean reset(boolean checkForResponse) {
        return DBHandlerFactory.getInstance().createDBHandler().resetRider(this, false);
    }

    public boolean fetchData() {
        return DBHandlerFactory.getInstance().createDBHandler().fetchRiderData(this);
    }

    /* Rider location methods */

    public boolean saveLocation() {
        return DBHandlerFactory.getInstance().createDBHandler().saveRiderLocation(this);
    }

    public void fetchLocation() {
        setCurrentLocation(DBHandlerFactory.getInstance().createDBHandler().getRiderLocation(this));
    }

    /* End of section*/
}
