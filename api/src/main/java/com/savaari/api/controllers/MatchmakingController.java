package com.savaari.api.controllers;

import com.savaari.api.database.DBHandlerFactory;
import com.savaari.api.entity.*;

public class MatchmakingController {
    // Main Attributes
    private static final String LOG_TAG = MatchmakingController.class.getSimpleName();
    Ride ride;
    RideRequest rideRequest;

    // Main Constructor
    public MatchmakingController() {
        // Empty
    }

    public Ride getRide() {
        return ride;
    }

    /*
     * Rider-side matchmaking method
     * Shortlists potential drivers, sends request to ideal
     * Waits for and returns response (driver accepts/declines)
     */
    public Ride searchForRide(Rider rider, Location source, Location destination, int paymentMode, RideType rideType) {
        ride = rider.searchForRide(source, destination, paymentMode, rideType);
        return ride;
    }
    // End of method:findDriver()

    /* Rider-side in-ride methods*/

    public Ride getRideForRider(Rider rider) {
        ride = rider.getRideForRider();
        return ride;
    }

    public boolean acknowledgeEndOfRide() {
        return ride.acknowledgeEndOfRide();
    }

    public void getRideStatus() {
        ride.fetchRideStatus();
    }

    /* End of section */



    /* Driver-side matchmaking methods */
    // Setting Driver as Active
    // TODO: Policy on how a ride is sent to driver, implemented in checkRideStatus()
    public RideRequest startMatchmaking(Driver driver)
    {
        rideRequest = driver.startMatchmaking();
        return rideRequest;
    }

    public RideRequest checkRideRequestStatus() {
        return ride.getRideParameters().getDriver().checkRideRequestStatus();
    }

    public boolean confirmRideRequest(int found_status)
    {
        if (found_status == 1) {
            ride = new Ride(rideRequest);
            System.out.println("Rider ID: " + ride.getRideParameters().getRider().getUserID()
            + ", Driver ID: " + ride.getRideParameters().getDriver().getUserID()
            + ", Payment mode: " + ride.getRideParameters().getPaymentMethod());
            return DBHandlerFactory.getInstance().createDBHandler().confirmRideRequest(ride);
        }
        else {
            // Rider: FIND_STATUS = RideRequest.REJECTED
            // Driver: RIDE_STATUS = RideRequest.MS_DEFAULT
            return DBHandlerFactory.getInstance().createDBHandler().rejectRideRequest(rideRequest);
        }
    }

    public boolean markArrivalAtPickup() {
        return ride.markArrivalAtPickup();
    }

    public boolean startRide() {
        return ride.startRide();
    }

    public double markArrivalAtDestination(long endTime, double distanceTravelled) {
        ride.setEndTime(endTime);
        ride.setDistanceTravelled(distanceTravelled);
        return ride.markArrivalAtDestination();
    }

    public Ride getRideForDriver() {
        ride = ride.getRideParameters().getDriver().getRideForDriver(ride.getRideParameters());
        return ride;
    }

    public boolean endRideWithPayment(Double amountPaid, Double change)
    {
        return ride.endRideWithPayment(amountPaid, change);
    }
}
