package com.savaari.api.entity.policy;

import com.savaari.api.entity.Ride;

public class DefaultPolicy implements Policy {

    // Main Attributes
    private static DefaultPolicy instance = null;

    // Main Methods
    private DefaultPolicy() {
    }

    public synchronized static Policy getInstance() {
        if (instance == null) {
            instance = new DefaultPolicy();
        }
        return instance;
    }

    // Functions

    @Override
    public double calculateFare(Ride ride) {
        System.out.println("Dist travelled: " + (ride.getDistanceTravelled()/1000));
        System.out.println("Ride duration: " +((ride.getRideDuration()/1000.0)/60.0));
        System.out.println("Start time: " + ride.getStartTime());
        System.out.println("End time: " + ride.getEndTime());

        return Math.max(ride.getRideParameters().getRideType().getBaseFare()
                + ride.getRideParameters().getRideType().getPerKMCharge()*(ride.getDistanceTravelled()/1000)
                + ((ride.getRideDuration()/1000.0)/60.0)*ride.getRideParameters().getRideType().getPerMinuteCharge(),
                ride.getRideParameters().getRideType().getMinimumFare());
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
