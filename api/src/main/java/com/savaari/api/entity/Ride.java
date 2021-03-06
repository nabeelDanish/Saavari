package com.savaari.api.entity;

import com.savaari.api.database.DBHandlerFactory;
import com.savaari.api.entity.policy.Policy;
import com.savaari.api.entity.policy.PolicyFactory;

import java.util.ArrayList;

public class Ride {

    // Main Attributes
    public static final int
            RS_DEFAULT = 10,
            PICKUP = 11,
            DRIVER_ARRIVED = 12,
            CANCELLED = 13,
            STARTED = 14,
            ARRIVED_AT_DEST = 15,
            PAYMENT_MADE = 16,
            END_ACKED = 20;

    int rideID;
    private RideRequest rideParameters;
    private Payment payment;
    private long startTime;
    private long endTime;
    private double distanceTravelled;
    private double estimatedFare;
    private double fare;
    private Policy policy;
    private int rideStatus;
    private ArrayList<Location> stops;

    public Ride() {
        payment = new Payment();
        rideParameters = new RideRequest();
    }

    public Ride(RideRequest rideRequest) {

        rideParameters = rideRequest;
        rideParameters.setFindStatus(RideRequest.FOUND);

        setRideStatus(RideRequest.MS_REQ_ACCEPTED);
        setPolicy(PolicyFactory.getInstance().determinePolicy(this));
        getPolicy().calculateEstimatedFare(this);
    }

    // ---------------------------------------------------------------------------------
    //                          GETTER and SETTER
    // ---------------------------------------------------------------------------------

    public RideRequest getRideParameters() {
        return rideParameters;
    }

    public void setRideParameters(RideRequest rideParameters) {
        this.rideParameters = rideParameters;
    }

    public Long getStartTime() {
        return startTime;
    }
    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
    public Long getEndTime() {
        return endTime;
    }
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
    public void setRideID(int rideID) { this.rideID = rideID; }
    public int getRideID() { return rideID; }
    public Payment getPayment() {
        return payment;
    }
    public void setPayment(Payment payment) {
        this.payment = payment;
    }
    public double getEstimatedFare() {
        return estimatedFare;
    }
    public void setEstimatedFare(double estimatedFare) {
        this.estimatedFare = estimatedFare;
    }
    public int getRideStatus() {
        return rideStatus;
    }
    public void setRideStatus(int rideStatus) {
        this.rideStatus = rideStatus;
    }
    public double getDistanceTravelled() {
        return distanceTravelled;
    }
    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }
    public double getFare() {
        return fare;
    }
    public void setFare(double fare) {
        this.fare = fare;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public ArrayList<Location> getStops() {
        return stops;
    }
    public void setStops(ArrayList<Location> stops) {
        this.stops = stops;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    // ---------------------------------------------------------------------------------
    // PUBLIC METHODS for POLICIES
    public long getRideDuration() {
        return endTime - startTime;
    }

    // ---------------------------------------------------------------------------------
    //                          System interaction methods
    // ---------------------------------------------------------------------------------
    public void fetchRideStatus() {
        DBHandlerFactory.getInstance().createDBHandler().fetchRideStatus(this);
    }
    public boolean markArrivalAtPickup() {
        return DBHandlerFactory.getInstance().createDBHandler().markArrivalAtPickup(this);
    }
    public boolean startRide() { return DBHandlerFactory.getInstance().createDBHandler().startRide(this); }


    // Acknowledge end of ride (final call)
    public boolean acknowledgeEndOfRide() {

        if (DBHandlerFactory.getInstance().createDBHandler().acknowledgeEndOfRide(this)) {
            return getRideParameters().getRider().reset(false);
        }

        return false;
    }

    // TODO: Methods need to get more data from tables

    // Main End Ride with Driver Method
    public double markArrivalAtDestination() {
        setFare(policy.calculateFare(this));

        if (DBHandlerFactory.getInstance().createDBHandler().markArrivalAtDestination(this)) {
            return fare;
        }
        return -1;
    }

    // Main Method for Ending Ride with Payment: Cash Mode
    public boolean endRideWithPayment(Double amountPaid, Double change)
    {
        //TODO: handle credit card payment

        payment = new Payment(amountPaid, change, rideParameters.getPaymentMethod());

        // Add Payment to DB
        if (payment.record()) {
            return DBHandlerFactory.getInstance().createDBHandler().endRideWithPayment(this);
        }
        else {
            System.out.println("addPayment returned false!");
            return false;
        }
    }

    /* Feedback methods */

    public boolean giveFeedbackForDriver(float rating) {
        return DBHandlerFactory.getInstance().createDBHandler().giveFeedbackForDriver(this, rating);
    }

    public boolean giveFeedbackForRider(float rating) {
        return DBHandlerFactory.getInstance().createDBHandler().giveFeedbackForRider(this, rating);
    }
}
