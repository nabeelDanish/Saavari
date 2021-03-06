package com.savaari.savaari_rider.ride.entity;

import com.savaari.savaari_rider.ride.entity.policy.Policy;

import java.util.ArrayList;

public class Ride {

    // Main Attributes
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
        //TODO: Don't allocate them here
        super();
        payment = new Payment();
        stops = new ArrayList<>();
        rideStatus = -1;
        rideParameters = new RideRequest();
    }

    public int getRideID() {
        return rideID;
    }

    public void setRideID(int rideID) {
        this.rideID = rideID;
    }

    public RideRequest getRideParameters() {
        return rideParameters;
    }

    public void setRideParameters(RideRequest rideParameters) {
        this.rideParameters = rideParameters;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public boolean closeToPickup() {
        return (rideParameters.getDriver().getCurrentLocation().latitude - rideParameters.getPickupLocation().latitude < 0.2
        && rideParameters.getDriver().getCurrentLocation().longitude - rideParameters.getPickupLocation().longitude < 0.2);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public ArrayList<Location> getStops() {
        return stops;
    }

    public void setStops(ArrayList<Location> stops) {
        this.stops = stops;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public long getRideDuration() {
        return endTime - startTime;
    }
}
