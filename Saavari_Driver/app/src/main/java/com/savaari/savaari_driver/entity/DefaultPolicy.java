package com.savaari.savaari_driver.entity;

public class DefaultPolicy implements com.savaari.savaari_driver.entity.Policy {

    // Main Attributes
    private static com.savaari.savaari_driver.entity.DefaultPolicy instance = null;

    // Main Methods
    private DefaultPolicy() {
        // Empty
    }

    public synchronized static com.savaari.savaari_driver.entity.Policy newInstance() {
        if (instance == null) {
            instance = new com.savaari.savaari_driver.entity.DefaultPolicy();
        }
        return instance;
    }

    // Functions

    @Override
    public void calculateFare(Ride ride) {
        RideType rideType = null;

        // Fetch & assign correct ride type or assume list is in order
        for (RideType currentRideType : rideTypes) {
            if (currentRideType == ride.getRideParameters().getRideType()) {
                rideType = currentRideType;
                break;
            }
        }

        if (rideType == null) {
            return;
        }

        double calculatedFare = rideType.getBaseFare()
                + rideType.getPerKMCharge()*ride.getDistanceTravelled()
                + ((1000.0)/60.0)*rideType.getPerMinuteCharge();
    }

    @Override
    public void calculateEstimatedFare(Ride ride) {
        ride.setEstimatedFare(200);
    }

    @Override
    public int getPolicyID() {
        return 1;
    }
}
